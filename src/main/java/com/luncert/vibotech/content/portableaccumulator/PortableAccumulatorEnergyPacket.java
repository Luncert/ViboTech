package com.luncert.vibotech.content.portableaccumulator;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

public class PortableAccumulatorEnergyPacket extends SimplePacketBase {
  private static final Logger LOGGER = LogUtils.getLogger();
  public static double clientSaturation = 0.0;
  public static int clientDemand = 0;
  public static int clientBuff = 0;

  private final BlockPos pos;
  private final int demand;
  private final int buff;

  public PortableAccumulatorEnergyPacket(BlockPos pos, int demand, int buff) {
    this.pos = pos;
    this.demand = demand;
    this.buff = buff;
  }

  public PortableAccumulatorEnergyPacket(FriendlyByteBuf buffer) {
      this.pos = buffer.readBlockPos();
      this.demand = buffer.readInt();
      this.buff = buffer.readInt();
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeBlockPos(pos);
    buf.writeInt(demand);
    buf.writeInt(buff);
  }

  @Override
  public boolean handle(NetworkEvent.Context context) {
    context.enqueueWork(() -> {
      try {
        updateClientCache(pos, demand, buff);
      } catch (Exception e) {
        LOGGER.error("exception", e);
      }
    });
    return true;
  }

  private static void updateClientCache(BlockPos pos, int demand, int buff) {
    clientDemand = demand;
    clientBuff = buff;
    clientSaturation = buff - demand;
  }
}
