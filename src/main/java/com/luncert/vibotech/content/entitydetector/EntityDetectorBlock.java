package com.luncert.vibotech.content.entitydetector;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

import com.luncert.vibotech.index.AllShapes;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EntityDetectorBlock extends Block {

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
  public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    return SHAPE;
  }

  @Override
  public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    return COLLISION;
  }

  @Override
  public void tick(BlockState pState, ServerLevel level, BlockPos pos, RandomSource pRandom) {
    checkPressed(level, pos);
  }


  private void checkPressed(Level level, BlockPos pos) {
    boolean foundEntity = false;
    List<? extends Entity> list = level.getEntities(null, new AABB(pos));
    if (!list.isEmpty()) {
      for (Entity entity : list) {
        if (!entity.isIgnoringBlockTriggers()) {
          foundEntity = true;
          break;
        }
      }
    }

    BlockState blockstate = level.getBlockState(pos);
    boolean poweredLastTime = blockstate.getValue(POWERED);
    if (foundEntity != poweredLastTime) {
      level.setBlockAndUpdate(pos, blockstate.setValue(POWERED, foundEntity));
      level.updateNeighborsAt(pos, this);
    }
  }
}
