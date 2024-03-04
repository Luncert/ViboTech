package com.luncert.vibotech.content.entitydetector;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class EntityDetectorBlockEntity extends SmartBlockEntity {

  private static final Logger LOGGER = LogUtils.getLogger();

  public EntityDetectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> list) {
  }

  @Override
  public void tick() {
    super.tick();
    checkPressed(level, worldPosition);
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
      // level.setBlockAndUpdate(pos, blockstate.setValue(POWERED, foundEntity));
      level.setBlock(pos, blockstate.setValue(POWERED, foundEntity), EntityDetectorBlock.UPDATE_ALL);

      for (Direction direction : Direction.values()) {
        // level.neighborChanged(pos.east(), blockstate.getBlock(), pos);
        level.updateNeighborsAt(pos.relative(direction), blockstate.getBlock());
      }

      // pLevel.neighborChanged($$4, this, pPos);
      // pLevel.updateNeighborsAtExceptFromFacing($$4, this, $$3);
    }
  }
}
