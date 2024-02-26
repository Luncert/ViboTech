package com.luncert.vibotech.content.camera;

import com.luncert.vibotech.compat.computercraft.Peripherals;
import com.luncert.vibotech.compat.vibotech.IViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.luncert.vibotech.content.camera.packet.ClientConnectCameraPacket;
import com.luncert.vibotech.content.camera.packet.ClientDisconnectCameraPacket;
import com.luncert.vibotech.content.camera.packet.ServerCreateCameraPacket;
import com.luncert.vibotech.content.camera.packet.ServerDisconnectCameraPacket;
import com.luncert.vibotech.index.AllCapabilities;
import com.luncert.vibotech.index.AllPackets;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class CameraBlockEntity extends SmartBlockEntity {

  private static final Logger LOGGER = LogUtils.getLogger();
  private final IPeripheral peripheral = Peripherals.createCameraPeripheral(this);
  private final IViboComponent component = new CameraComponent();

  public CameraBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> list) {
  }

  @Override
  public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    if (Peripherals.isPeripheral(cap)) {
      return LazyOptional.of(() -> peripheral).cast();
    }
    if (AllCapabilities.isViboComponent(cap)) {
      return LazyOptional.of(() -> component).cast();
    }
    return super.getCapability(cap, side);
  }

  // api

  public Optional<ServerPlayer> getPlayerByName(String name) {
    if (level == null) {
      return Optional.empty();
    }
    MinecraftServer server = level.getServer();
    if (server == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(server.getPlayerList().getPlayerByName(name));
  }

  public void connectFromServer(ServerPlayer player) {
    CameraEntity entity = CameraData.getOrCreateCameraEntity(level, worldPosition);
    AllPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> player), new ClientConnectCameraPacket(entity.getId()));
  }

  public void connectFromClient(Player player) {
    AllPackets.getChannel().sendToServer(new ServerCreateCameraPacket(worldPosition));
  }

  public void disconnectFromServer(ServerPlayer player) {
    AllPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> player), new ClientDisconnectCameraPacket());
  }

  public void disconnectFromClient(Player player) {
    AllPackets.getChannel().sendToServer(new ServerDisconnectCameraPacket());
  }
}
