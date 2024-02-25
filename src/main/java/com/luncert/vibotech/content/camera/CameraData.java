package com.luncert.vibotech.content.camera;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CameraData {

  private static Entity orignalCameraEntity;

  static void pushCameraEntity(Entity currentCameraEntity) {
    orignalCameraEntity = currentCameraEntity;
  }

  static Entity popCameraEntity() {
    Entity r = orignalCameraEntity;
    orignalCameraEntity = null;
    return r;
  }

  public static boolean isEnabled() {
    return orignalCameraEntity != null;
  }
}
