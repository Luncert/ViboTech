package com.luncert.vibotech.content.camera;

import com.luncert.vibotech.index.AllKeys;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public class CameraClientHandler {

  public static void tick() {
    if (AllKeys.isActuallyPressed(InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_LSHIFT))) {
      Entity entity = CameraData.popCameraEntity();
      if (entity != null) {
        Minecraft mc = Minecraft.getInstance();
        mc.setCameraEntity(entity);
      }
    }
  }
}
