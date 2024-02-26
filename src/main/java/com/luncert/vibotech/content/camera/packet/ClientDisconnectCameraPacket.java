package com.luncert.vibotech.content.camera.packet;

import com.luncert.vibotech.content.camera.CameraData;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ClientDisconnectCameraPacket extends SimplePacketBase {

  public ClientDisconnectCameraPacket() {
  }

  public ClientDisconnectCameraPacket(FriendlyByteBuf friendlyByteBuf) {
  }

  @Override
  public void write(FriendlyByteBuf friendlyByteBuf) {
  }

  @Override
  public boolean handle(NetworkEvent.Context context) {
    context.enqueueWork(CameraData::restoreCamera);
    return true;
  }
}
