package com.luncert.vibotech.content.camera;

import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CameraData {

  private static Entity orignalCameraEntity;

  static void pushCameraEntity(Entity currentCameraEntity) {
    orignalCameraEntity = currentCameraEntity;
  }

  static Optional<Entity> popCameraEntity() {
    Entity r = orignalCameraEntity;
    orignalCameraEntity = null;
    return Optional.ofNullable(r);
  }

  public static boolean isEnabled() {
    return orignalCameraEntity != null;
  }
}
