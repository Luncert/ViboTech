package com.luncert.vibotech.content.camera;

import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.luncert.vibotech.content.camera.packet.ClientConnectCameraPacket;
import com.luncert.vibotech.index.AllPackets;
import com.mojang.logging.LogUtils;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

public class CameraComponent extends BaseViboComponent {

  private static final Logger LOGGER = LogUtils.getLogger();

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.CAMERA;
  }

  @LuaFunction
  public void connect(String playerName) throws LuaException {
    ServerPlayer player = getPlayerByName(playerName)
        .orElseThrow(() -> new LuaException("invalid player name"));
    getCameraEntity().ifPresent(entity -> {
      AllPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> player), new ClientConnectCameraPacket(entity.getId()));
    });
    // Minecraft mc = Minecraft.getInstance();
    // mc.setCameraEntity();
    // try (NativeImage image = Screenshot.takeScreenshot(mc.getMainRenderTarget())) {
    //
    // }
  }

  private Optional<Entity> getCameraEntity() {
    StructureTemplate.StructureBlockInfo componentBlockInfo = accessor.contraption.getComponentBlockInfo(name);
    BlockPos componentPos = componentBlockInfo.pos();
    int seatIndex = accessor.contraption.getSeats().indexOf(componentPos);
    for (Map.Entry<UUID, Integer> entry : accessor.contraption.getSeatMapping().entrySet()) {
      if (entry.getValue().equals(seatIndex)) {
        return Optional.ofNullable(accessor.world.getEntity(entry.getKey()));
      }
    }
    return Optional.empty();
  }

  private Optional<ServerPlayer> getPlayerByName(String name) {
    MinecraftServer server = accessor.world.getServer();
    if (server == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(server.getPlayerList().getPlayerByName(name));
  }
}
