package com.luncert.vibotech.content.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CameraData {

  private static Entity orignalCameraEntity;

  static void pushCameraEntity(Entity currentCameraEntity) {
    orignalCameraEntity = currentCameraEntity;
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
