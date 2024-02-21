package com.luncert.vibotech.compat.vibotech;

import dan200.computercraft.api.lua.LuaFunction;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyAccessorComponent extends BaseViboComponent {

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.ENERGY_ACCESSOR;
  }

  @LuaFunction
  public int getEnergy() {
    return getEnergyAccessor().map(IEnergyStorage::getEnergyStored).orElse(0);
  }

  @LuaFunction
  public int getCapacity() {
    return getEnergyAccessor().map(IEnergyStorage::getMaxEnergyStored).orElse(0);
  }

  @LuaFunction
  public boolean canExtract() {
    return getEnergyAccessor().map(IEnergyStorage::canExtract).orElse(false);
  }

  @LuaFunction
  public boolean canReceive() {
    return getEnergyAccessor().map(IEnergyStorage::canReceive).orElse(false);
  }
}
