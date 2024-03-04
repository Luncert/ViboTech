package com.luncert.vibotech.content.entitydetector;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class EntityDetectorBlockEntity extends SmartBlockEntity {

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
      level.setBlockAndUpdate(pos, blockstate.setValue(POWERED, foundEntity));
      level.updateNeighborsAt(pos, blockstate.getBlock());
    }
  }
}
