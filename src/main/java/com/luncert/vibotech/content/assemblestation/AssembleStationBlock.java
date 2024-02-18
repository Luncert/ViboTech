package com.luncert.vibotech.content.assemblestation;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

import com.google.common.collect.ImmutableList;
import com.luncert.vibotech.ViboTechMod;
import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
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
import org.jetbrains.annotations.Nullable;

/**
 * AssembleStationBlockEntity assemble in tick() if block is powered.
 * - AssembleStationBlockEntity#assemble: create TransportMachineEntity and call assemble
 * - TransportMachineEntity#assemble: create contraption, call assemble and start riding
 */
public class AssembleStationBlock extends Block implements IBE<AssembleStationBlockEntity> {

  private static final ResourceLocation DROP = ViboTechMod.asResource("assemble_station");

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
  public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity tile, ItemStack tool) {
    // Don't drop blocks here - see playerWillDestroy.
    player.awardStat(Stats.BLOCK_MINED.get(this));
    player.causeFoodExhaustion(0.005F);
  }

  @Override
  public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
    super.playerWillDestroy(world, pos, state, player);
    if (!(world instanceof ServerLevel serverWorld)) return;

    // We drop the item here instead of doing it in the harvest method, as we should
    // drop computers for creative players too.

    var tile = world.getBlockEntity(pos);
    if (tile instanceof AssembleStationBlockEntity) {
      var context = new LootParams.Builder(serverWorld)
          .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
          .withParameter(LootContextParams.TOOL, player.getMainHandItem())
          .withParameter(LootContextParams.THIS_ENTITY, player)
          .withParameter(LootContextParams.BLOCK_ENTITY, tile);
      for (var item : state.getDrops(context)) {
        popResource(world, pos, item);
      }

      state.spawnAfterBreak(serverWorld, pos, player.getMainHandItem(), true);
    }
  }

  @Override
  public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
    if (!(builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof AssembleStationBlockEntity assembleStation))
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
}
