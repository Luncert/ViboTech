package com.luncert.vibotech.content;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

import com.luncert.vibotech.ViboTechMod;
import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.luncert.vibotech.index.AllBlocks;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.block.IBE;
import dan200.computercraft.shared.computer.blocks.AbstractComputerBlockEntity;
import dan200.computercraft.shared.computer.blocks.ComputerBlockEntity;
import dan200.computercraft.shared.computer.items.ComputerItem;
import java.util.Iterator;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

/**
 * AssembleStationBlockEntity assemble in tick() if block is powered.
 * - AssembleStationBlockEntity#assemble: create TransportMachineEntity and call assemble
 * - TransportMachineEntity#assemble: create contraption, call assemble and start riding
 */
public class AssembleStationBlock extends Block implements IBE<AssembleStationBlockEntity> {

  private static final ResourceLocation DROP = new ResourceLocation(ViboTechMod.ID, "assemble_station");

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
  public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
    super.playerWillDestroy(world, pos, state, player);
    if (world instanceof ServerLevel serverWorld) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof AssembleStationBlockEntity assembleStation) {
        LootParams.Builder context = (new LootParams.Builder(serverWorld))
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
            .withParameter(LootContextParams.TOOL, player.getMainHandItem())
            .withParameter(LootContextParams.THIS_ENTITY, player)
            .withParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
            .withDynamicDrop(DROP, (out) -> {
              out.accept(this.getItem(assembleStation));
            });

        for (ItemStack item : state.getDrops(context)) {
          popResource(world, pos, item);
        }

        state.spawnAfterBreak(serverWorld, pos, player.getMainHandItem(), true);
      }
    }
  }

  private ItemStack getItem(AssembleStationBlockEntity blockEntity) {
    AssembleStationItem item = (AssembleStationItem) this.asItem();
    if (blockEntity.isAssembled()) {
      return item.create(blockEntity);
    } else {
      return ItemStack.EMPTY;
    }
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
