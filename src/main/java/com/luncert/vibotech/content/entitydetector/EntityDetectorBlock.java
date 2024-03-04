package com.luncert.vibotech.content.entitydetector;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;

public class EntityDetectorBlock extends Block {

  public EntityDetectorBlock(Properties properties) {
    super(properties);
    registerDefaultState(defaultBlockState().setValue(POWERED, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, POWERED);
    super.createBlockStateDefinition(builder);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource prandom) {
    super.tick(state, level, pos, prandom);

    checkPressed(level, pos);
  }


  private void checkPressed(Level pLevel, BlockPos pPos) {
    BlockState blockstate = pLevel.getBlockState(pPos);
    boolean foundEntity = false;
    List<? extends Entity> list = pLevel.getEntities(null, new AABB(pPos));
    if (!list.isEmpty()) {
      for (Entity entity : list) {
        if (!entity.isIgnoringBlockTriggers()) {
          foundEntity = true;
          break;
        }
      }
    }

    blockstate = blockstate.setValue(POWERED, foundEntity);
    pLevel.setBlock(pPos, blockstate, 3);

    if (foundEntity) {
      pLevel.scheduleTick(new BlockPos(pPos), this, 10);
    }
  }
}
