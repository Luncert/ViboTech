package com.luncert.vibotech.content.camera;

import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import org.slf4j.Logger;

public class ConnectCameraPacket implements Packet<ClientConnectCameraPacketListener> {

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
  public void handle(ClientConnectCameraPacketListener handler) {
    handler.handle(cameraEntityId);
  }
}
