package com.luncert.vibotech.content.entitydetector;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.luncert.vibotech.index.AllShapes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class EntityDetectorBlock extends Block implements IBE<EntityDetectorBlockEntity> {

  public static final VoxelShape SHAPE = AllShapes
      .shape(0, 0, 0, 16, 1, 16)
      .build();

  public static final VoxelShape COLLISION = AllShapes
      .shape(0, 0, 0, 0, 0, 0)
      .build();

  public EntityDetectorBlock(Properties properties) {
    super(properties);
    registerDefaultState(defaultBlockState().setValue(POWERED, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder.add(POWERED));
  }

  @Override
  public boolean isSignalSource(BlockState state) {
    return state.getValue(POWERED);
  }

  @Override
  public int getSignal(BlockState blockState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
    return isSignalSource(blockState) ? 15 : 0;
  }

  @Override
  public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
    return true;
  }

  @Override
  public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
    IBE.onRemove(pState, pLevel, pPos, pNewState);
  }

  @Override
  public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    return SHAPE;
  }

  @Override
  public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    return COLLISION;
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
