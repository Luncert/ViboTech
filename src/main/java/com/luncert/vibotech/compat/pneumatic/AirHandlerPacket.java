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
  public static int clientAir;
  public static float clientHeat;

  private float pressure;
  private int volume;
  private int air;
  private float heat;


  public AirHandlerPacket(float pressure, int volume, int air, float heat) {
    this.pressure = pressure;
    this.volume = volume;
    this.air = air;
    this.heat = heat;
  }

  public AirHandlerPacket(FriendlyByteBuf buffer) {
      this.pressure = buffer.readFloat();
      this.volume = buffer.readInt();
      this.air = buffer.readInt();
      this.heat = buffer.readFloat();
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeFloat(pressure);
    buf.writeInt(volume);
    buf.writeInt(air);
    buf.writeFloat(heat);
  }

  @Override
  public boolean handle(NetworkEvent.Context context) {
    context.enqueueWork(() -> {
      try {
        updateClientCache(pressure, volume, air, heat);
      } catch (Exception e) {
        LOGGER.error("exception", e);
      }
    });
    return true;
  }

  private static void updateClientCache(float pressure, int volume, int air, float heat) {
    clientPressure = pressure;
    clientVolume = volume;
    clientAir = air;
    clientHeat = heat;
  }
}
