package com.luncert.vibotech.content.entitydetector;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.luncert.vibotech.index.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class EntityDetectorBlock extends Block implements IBE<EntityDetectorBlockEntity>, IWrenchable {

  public static final VoxelShape SHAPE = AllShapes
      .shape(0, 0, 0, 16, 16, 16)
      .build();

  public static final VoxelShape Z_AXIS_COLLISION = AllShapes
      .shape(0, 0, 0, 1, 16, 16)
      .add(15, 0, 0, 16, 16, 16)
      .build();

  public static final VoxelShape X_AXIS_COLLISION = AllShapes
      .shape(0, 0, 0, 16, 16, 1)
      .add(0, 0, 15, 16, 16, 16)
      .build();

  public EntityDetectorBlock(Properties properties) {
    super(properties);
    registerDefaultState(defaultBlockState().setValue(POWERED, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder.add(POWERED, FACING));
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
  }

  @Override
  public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    return SHAPE;
  }

  @Override
  public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    boolean xAxis = pState.getValue(FACING).getAxis().equals(Direction.Axis.X);
    return xAxis ? X_AXIS_COLLISION : Z_AXIS_COLLISION;
  }

  @Override
  public boolean isSignalSource(BlockState state) {
    return state.getValue(POWERED);
  }

  @Override
  public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
    return true;
  }

  @Override
  public int getSignal(BlockState blockState, BlockGetter pLevel, BlockPos pPos, Direction direction) {
    return isSignalSource(blockState) && !Direction.UP.equals(direction) ? 15 : 0;
  }

  @Override
  public int getDirectSignal(BlockState blockState, BlockGetter level, BlockPos pos, Direction direction) {
    return getSignal(blockState, level, pos, direction);
  }

  @Override
  public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
    IBE.onRemove(pState, pLevel, pPos, pNewState);
  }

  @Override
  public Class<EntityDetectorBlockEntity> getBlockEntityClass() {
    return EntityDetectorBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends EntityDetectorBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.ENTITY_DETECTOR.get();
  }
}
