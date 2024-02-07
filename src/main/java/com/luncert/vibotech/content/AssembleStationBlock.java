package com.luncert.vibotech.content;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

import com.google.common.collect.ImmutableList;
import com.luncert.vibotech.ViboTechMod;
import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.luncert.vibotech.index.AllBlocks;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.block.IBE;
import dan200.computercraft.shared.computer.blocks.AbstractComputerBlockEntity;
import dan200.computercraft.shared.computer.blocks.ComputerBlockEntity;
import dan200.computercraft.shared.computer.items.ComputerItem;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

/**
 * AssembleStationBlockEntity assemble in tick() if block is powered.
 * - AssembleStationBlockEntity#assemble: create TransportMachineEntity and call assemble
 * - TransportMachineEntity#assemble: create contraption, call assemble and start riding
 */
public class AssembleStationBlock extends Block implements IBE<AssembleStationBlockEntity> {

  private static final ResourceLocation DROP = ViboTechMod.asResource("assemble_station");

  private static final Logger LOGGER = LogUtils.getLogger();

  public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

  public AssembleStationBlock(Properties properties) {
    super(properties);
    registerDefaultState(defaultBlockState().setValue(POWERED, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(POWERED, HORIZONTAL_FACING);
    super.createBlockStateDefinition(builder);
  }

  @Override
  public Class<AssembleStationBlockEntity> getBlockEntityClass() {
    return AssembleStationBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends AssembleStationBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.ASSEMBLE_STATION.get();
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  public void neighborChanged(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos,
                              @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
    if (worldIn.isClientSide)
      return;
    boolean previouslyPowered = state.getValue(POWERED);
    if (previouslyPowered != worldIn.hasNeighborSignal(pos))
      worldIn.setBlock(pos, state.cycle(POWERED), 2);
    super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
  }

  @Override
  public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
    if (!(builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof AssembleStationBlockEntity assembleStation)
      || !assembleStation.isAssembled())
      return super.getDrops(state, builder);

    builder.withDynamicDrop(DROP, (out) -> {
      out.accept(getItem(assembleStation));
    });
    return ImmutableList.of(getItem(assembleStation));
  }

  private ItemStack getItem(AssembleStationBlockEntity blockEntity) {
    AssembleStationItem item = (AssembleStationItem) this.asItem();
    return item.create(blockEntity);
  }

  public enum AssembleStationAction {
    ASSEMBLE, DISASSEMBLE;

    public boolean shouldAssemble() {
      return this == ASSEMBLE;
    }

    public boolean shouldDisassemble() {
      return this == DISASSEMBLE;
    }
  }

  public static AssembleStationAction getAction(BlockState state) {
    boolean powered = state.getValue(POWERED);
    return powered ? AssembleStationAction.ASSEMBLE : AssembleStationAction.DISASSEMBLE;
  }

  public static BlockState createAnchor(BlockState state) {
    return AllBlocks.TRANSPORT_MACHINE_ANCHOR.getDefaultState();
  }
}
