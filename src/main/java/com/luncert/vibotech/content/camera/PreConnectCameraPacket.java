package com.luncert.vibotech.content.camera;

import com.luncert.vibotech.index.AllPackets;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

public class PreConnectCameraPacket extends SimplePacketBase {

  private static final Logger LOGGER = LogUtils.getLogger();

  private final BlockPos pos;

  public PreConnectCameraPacket(BlockPos pos) {
    this.pos = pos;
  }

  public PreConnectCameraPacket(FriendlyByteBuf buf) {
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

      List<CameraEntity> entities = level.getEntitiesOfClass(CameraEntity.class, new AABB(pos));
      CameraEntity entity;
      if (entities.isEmpty()) {
        entity = CameraEntity.create(level);
        entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        level.addFreshEntity(entity);
      } else {
        entity = entities.get(0);
      }
      LOGGER.info("yy {}", entity);

      AllPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> player), new ConnectCameraPacket(entity.getId()));
    });
    return true;
  }
}
