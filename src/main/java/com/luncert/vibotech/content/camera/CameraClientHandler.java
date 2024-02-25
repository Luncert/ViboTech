package com.luncert.vibotech.content.camera;

import com.luncert.vibotech.index.AllKeys;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

public class CameraClientHandler {

  public static void tick() {
    if (AllKeys.isActuallyPressed(InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_LSHIFT))) {
      CameraData.popCameraEntity().ifPresent(entity ->
          Minecraft.getInstance().setCameraEntity(entity));
    }
  }
}
