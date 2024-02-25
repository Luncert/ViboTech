package com.luncert.vibotech.content.camera;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class DisconnectCameraPacket extends SimplePacketBase {

  public DisconnectCameraPacket() {
  }

  public DisconnectCameraPacket(FriendlyByteBuf friendlyByteBuf) {
  }

  @Override
  public void write(FriendlyByteBuf friendlyByteBuf) {
  }

  @Override
  public boolean handle(NetworkEvent.Context context) {
    context.enqueueWork(() ->
        CameraData.popCameraEntity().ifPresent(entity ->
            Minecraft.getInstance().setCameraEntity(entity)));
    return true;
  }
}
