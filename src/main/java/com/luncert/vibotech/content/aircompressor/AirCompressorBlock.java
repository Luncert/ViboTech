package com.luncert.vibotech.content.aircompressor;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.slf4j.Logger;

public class AirCompressorBlock extends HorizontalKineticBlock implements IBE<AirCompressorBlockEntity> {

  private static final Logger LOGGER = LogUtils.getLogger();

  public AirCompressorBlock(Properties pProperties) {
    super(pProperties);
  }

  @Override
  public Class<AirCompressorBlockEntity> getBlockEntityClass() {
    return AirCompressorBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends AirCompressorBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.AIR_COMPRESSOR.get();
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Direction preferredSide = getPreferredHorizontalFacing(context);
    if (preferredSide != null)
      return defaultBlockState().setValue(HORIZONTAL_FACING, preferredSide);
    return super.getStateForPlacement(context);
  }

  @Override
  public Direction.Axis getRotationAxis(BlockState blockState) {
    return blockState.getValue(HORIZONTAL_FACING).getAxis();
  }

  @Override
  public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
    return face == state.getValue(HORIZONTAL_FACING);
  }

  @Override
  public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
    super.onNeighborChange(state, level, pos, neighbor);
  }
}
