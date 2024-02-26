package com.luncert.vibotech.content.camera;

import com.mojang.logging.LogUtils;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class CameraPeripheral implements IPeripheral {

  private static final Logger LOGGER = LogUtils.getLogger();

  private final String type;
  private final CameraBlockEntity cameraBlockEntity;
  private final List<IComputerAccess> connected = new ArrayList<>();

  public CameraPeripheral(String type, CameraBlockEntity blockEntity) {
    this.type = type;
    this.cameraBlockEntity = blockEntity;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void attach(IComputerAccess computer) {
    connected.add(computer);
  }

  @Override
  public void detach(IComputerAccess computer) {
    connected.remove(computer);
  }

  @Override
  public boolean equals(@Nullable IPeripheral iPeripheral) {
    return iPeripheral == this;
  }

  // api

  @LuaFunction
  public final void connect(String playerName) throws LuaException {
    ServerPlayer player = cameraBlockEntity.getPlayerByName(playerName)
        .orElseThrow(() -> new LuaException("invalid player name"));
    cameraBlockEntity.connectFromServer(player);
  }
}
