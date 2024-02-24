package com.luncert.vibotech.content.camera;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public class ClientConnectCameraPacketListener implements PacketListener {

  private static final Logger LOGGER = LogUtils.getLogger();

  @Override
  public void onDisconnect(Component pReason) {

  }

  @Override
  public boolean isAcceptingMessages() {
    return false;
  }

  public void handle(int cameraEntityId) {
    Minecraft mc = Minecraft.getInstance();
    if (mc.player == null || mc.level == null)
      return;

    Entity cameraEntity = mc.level.getEntity(cameraEntityId);
    LOGGER.info("xx {}", cameraEntity);
  }
}
