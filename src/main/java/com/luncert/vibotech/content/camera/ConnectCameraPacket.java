package com.luncert.vibotech.content.camera;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

public class ConnectCameraPacket extends SimplePacketBase {

  private static final Logger LOGGER = LogUtils.getLogger();

  private final int cameraEntityId;

  public ConnectCameraPacket(int cameraEntityId) {
    this.cameraEntityId = cameraEntityId;
  }

  public ConnectCameraPacket(FriendlyByteBuf buf) {
    this.cameraEntityId = buf.readVarInt();
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeVarInt(cameraEntityId);
  }

  @Override
  public boolean handle(NetworkEvent.Context context) {
    context.enqueueWork(() -> {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level == null) {
        LOGGER.warn("invalid level");
        return;
      }
      Entity cameraEntity = mc.level.getEntity(cameraEntityId);
      if (cameraEntity == null) {
        LOGGER.warn("camera entity {} not found", cameraEntityId);
        return;
      }
      CameraData.pushCameraEntity(mc.getCameraEntity());
      mc.setCameraEntity(cameraEntity);
    });
    return true;
  }
}
