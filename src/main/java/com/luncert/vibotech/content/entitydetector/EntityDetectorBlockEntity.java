package com.luncert.vibotech.content.entitydetector;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class EntityDetectorBlockEntity extends SmartBlockEntity {

  private static final Logger LOGGER = LogUtils.getLogger();

  private int powerDelay;

  public EntityDetectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> list) {
  }

  @Override
  public void tick() {
    super.tick();
    if (powerDelay > 0) {
      powerDelay--;
      return;
    }
    checkPressed();
  }


  private void checkPressed() {
    boolean foundEntity = false;
    List<? extends Entity> list = level.getEntities(null, new AABB(worldPosition));
    if (!list.isEmpty()) {
      for (Entity entity : list) {
        if (!entity.isIgnoringBlockTriggers()) {
          foundEntity = true;
          break;
        }
      }
    }

    BlockState blockstate = level.getBlockState(worldPosition);
    blockstate = blockstate.setValue(POWERED, foundEntity);
    if (foundEntity) {
      powerDelay = 10;
    }
    level.setBlock(worldPosition, blockstate, 3);
  }
}
