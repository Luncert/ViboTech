package com.luncert.vibotech.content.camera;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CameraData {

  private static Entity orignalCameraEntity;

  public static CameraEntity getOrCreateCameraEntity(Level level, BlockPos pos, Direction orientation) {
    List<CameraEntity> entities = level.getEntitiesOfClass(CameraEntity.class, new AABB(pos));
    CameraEntity entity;
    if (entities.isEmpty()) {
      entity = CameraEntity.create(level, pos, orientation);
      level.addFreshEntity(entity);
    } else {
      entity = entities.get(0);
    }
    return entity;
  }

  public static void pushCameraEntity(Entity currentCameraEntity) {
    if (currentCameraEntity instanceof LocalPlayer) {
      orignalCameraEntity = currentCameraEntity;
    }
  }

  public static void restoreCamera() {
    if (orignalCameraEntity != null) {
      Minecraft.getInstance().setCameraEntity(orignalCameraEntity);
      orignalCameraEntity = null;
    }
  }

  public static boolean isEnabled() {
    return orignalCameraEntity != null;
  }
}
