package com.luncert.vibotech.content.camera;

import com.luncert.vibotech.index.AllKeys;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public class CameraClientHandler {

  public static int PACKET_RATE = 5;
  private static int packetCooldown;

  public static void tick() {
    if (packetCooldown > 0) {
      --packetCooldown;
    }

    if (packetCooldown == 0) {
      packetCooldown = PACKET_RATE;

      if (AllKeys.isActuallyPressed(InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_LSHIFT))) {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = CameraData.popCameraEntity();
        if (entity != null) {
          mc.setCameraEntity(entity);
        }
      }
    }
  }
}
