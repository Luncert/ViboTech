package com.luncert.vibotech.compat.computercraft;

import com.luncert.vibotech.content.controlseat.ControlSeatBlockEntity;
import com.luncert.vibotech.content.controlseat.ControlSeatComponent;
import com.luncert.vibotech.index.AllCapabilities;
import com.mojang.blaze3d.platform.InputConstants;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.Collection;
import org.jetbrains.annotations.Nullable;

public class ControlSeatPeripheral implements IPeripheral {

  protected final String type;
  private final ControlSeatComponent component;

  public ControlSeatPeripheral(String type, ControlSeatBlockEntity be) {
    this.type = type;
    this.component = be.getCapability(AllCapabilities.CAPABILITY_VIBO_COMPONENT)
        .<ControlSeatComponent>cast()
        .orElse(null);
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
  public boolean isKeyPressed(String name) {
    return component.isKeyPressed(name);
  }

  @LuaFunction
  public final Collection<String> getInputs() {
    return component.getInputs().stream().map(InputConstants.Key::getName).toList();
  }
}
