package com.luncert.vibotech.content.camera;

import com.luncert.vibotech.index.AllPackets;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class PreDisconnectCameraPacket extends SimplePacketBase {

  public PreDisconnectCameraPacket() {
  }

  public PreDisconnectCameraPacket(FriendlyByteBuf friendlyByteBuf) {
  }

  @Override
  public void write(FriendlyByteBuf friendlyByteBuf) {
  }

  @Override
  public boolean handle(NetworkEvent.Context context) {
    context.enqueueWork(() -> {
      ServerPlayer player = context.getSender();
      if (player == null)
        return;

      AllPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> player), new DisconnectCameraPacket());
    });
    return true;
  }
}
