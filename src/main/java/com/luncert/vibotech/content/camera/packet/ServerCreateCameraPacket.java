package com.luncert.vibotech.content.camera.packet;

import com.luncert.vibotech.content.camera.CameraData;
import com.luncert.vibotech.index.AllPackets;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

public class ServerCreateCameraPacket extends SimplePacketBase {

  private static final Logger LOGGER = LogUtils.getLogger();

  private final BlockPos pos;

  public ServerCreateCameraPacket(BlockPos pos) {
    this.pos = pos;
  }

  public ServerCreateCameraPacket(FriendlyByteBuf buf) {
    this.pos = new BlockPos(buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeVarInt(pos.getX());
    buf.writeVarInt(pos.getY());
    buf.writeVarInt(pos.getZ());
  }

  @Override
  public boolean handle(NetworkEvent.Context context) {
    context.enqueueWork(() -> {
      ServerPlayer player = context.getSender();
      if (player == null)
        return;
      Level level = player.level();
      Entity entity = CameraData.getOrCreateCameraEntity(level, pos);
      AllPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> player), new ClientConnectCameraPacket(entity.getId()));
    });
    return true;
  }
}
