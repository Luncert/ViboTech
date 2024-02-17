package com.luncert.vibotech.compat.computercraft;

import com.luncert.vibotech.content2.camera.CameraBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;

public class CameraPeripheral implements IPeripheral {

  protected String type;
  protected CameraBlockEntity cameraBlockEntity;

  public CameraPeripheral(String type, CameraBlockEntity blockEntity) {
    this.type = type;
    this.cameraBlockEntity = blockEntity;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public boolean equals(@Nullable IPeripheral iPeripheral) {
    return iPeripheral == this;
  }

  // api

  @LuaFunction
  public final void capture() {
    // TODO:
  }
}
