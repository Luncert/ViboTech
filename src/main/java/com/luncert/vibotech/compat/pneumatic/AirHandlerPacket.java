package com.luncert.vibotech.compat.pneumatic;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

public class AirHandlerPacket extends SimplePacketBase {
  private static final Logger LOGGER = LogUtils.getLogger();

  public static float clientPressure;
  public static int clientVolume;

  private float pressure;
  private int volume;


  public AirHandlerPacket(float pressure, int volume) {
    this.pressure = pressure;
    this.volume = volume;
  }

  public AirHandlerPacket(FriendlyByteBuf buffer) {
      this.pressure = buffer.readInt();
      this.volume = buffer.readInt();
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeFloat(pressure);
    buf.writeInt(volume);
  }

  @Override
  public boolean handle(NetworkEvent.Context context) {
    context.enqueueWork(() -> {
      try {
        updateClientCache(pressure, volume);
      } catch (Exception e) {
        LOGGER.error("exception", e);
      }
    });
    return true;
  }

  private static void updateClientCache(float pressure, int volume) {
    clientPressure = pressure;
    clientVolume = volume;
  }
}
