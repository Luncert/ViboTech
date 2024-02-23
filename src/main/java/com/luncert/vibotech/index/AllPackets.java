package com.luncert.vibotech.index;

import com.luncert.vibotech.ViboTechMod;
import com.luncert.vibotech.content.vibomachinecontrolseat.ControlSeatInputPacket;
import com.luncert.vibotech.foundation.network.EnergyNetworkPacket;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public enum AllPackets {

  CONTROL_SEAT_INPUT(ControlSeatInputPacket.class, ControlSeatInputPacket::new, NetworkDirection.PLAY_TO_SERVER),
  // PORTABLE_ACCUMULATOR_ENERGY(EnergyNetworkPacket.class, EnergyNetworkPacket::new, NetworkDirection.PLAY_TO_CLIENT);
  ;

  public static final ResourceLocation CHANNEL_NAME = ViboTechMod.asResource("main");
  public static final int NETWORK_VERSION = 3;
  public static final String NETWORK_VERSION_STR = String.valueOf(NETWORK_VERSION);
  private static SimpleChannel channel;

  private PacketType<?> packetType;

  <T extends SimplePacketBase> AllPackets(Class<T> type,
                                          Function<FriendlyByteBuf, T> factory,
                                          NetworkDirection direction) {
    packetType = new PacketType<>(type, factory, direction);
  }

  public static void registerPackets() {
    channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
        .serverAcceptedVersions(NETWORK_VERSION_STR::equals)
        .clientAcceptedVersions(NETWORK_VERSION_STR::equals)
        .networkProtocolVersion(() -> NETWORK_VERSION_STR)
        .simpleChannel();

    for (AllPackets packet : values())
      packet.packetType.register();
  }

  public static SimpleChannel getChannel() {
    return channel;
  }

  public static void sendToNear(Level world, BlockPos pos, int range, Object message) {
    getChannel().send(
        PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), range, world.dimension())),
        message);
  }

  private static class PacketType<T extends SimplePacketBase> {
    private static int index = 0;

    private BiConsumer<T, FriendlyByteBuf> encoder;
    private Function<FriendlyByteBuf, T> decoder;
    private BiConsumer<T, Supplier<Context>> handler;
    private Class<T> type;
    private NetworkDirection direction;

    private PacketType(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
      encoder = T::write;
      decoder = factory;
      handler = (packet, contextSupplier) -> {
        Context context = contextSupplier.get();
        if (packet.handle(context)) {
          context.setPacketHandled(true);
        }
      };
      this.type = type;
      this.direction = direction;
    }

    private void register() {
      getChannel().messageBuilder(type, index++, direction)
          .encoder(encoder)
          .decoder(decoder)
          .consumerNetworkThread(handler)
          .add();
    }
  }
}
