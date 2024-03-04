package com.luncert.vibotech.content.entitydetector;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.luncert.vibotech.index.AllShapes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

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
    builder.add(POWERED);
    super.createBlockStateDefinition(builder);
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
