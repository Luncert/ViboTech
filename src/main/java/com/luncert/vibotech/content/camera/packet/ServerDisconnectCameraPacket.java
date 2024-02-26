package com.luncert.vibotech.content.camera.packet;

import com.luncert.vibotech.index.AllPackets;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class ServerDisconnectCameraPacket extends SimplePacketBase {

  public ServerDisconnectCameraPacket() {
  }

  public ServerDisconnectCameraPacket(FriendlyByteBuf friendlyByteBuf) {
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

      AllPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> player), new ClientDisconnectCameraPacket());
    });
    return true;
  }
}
