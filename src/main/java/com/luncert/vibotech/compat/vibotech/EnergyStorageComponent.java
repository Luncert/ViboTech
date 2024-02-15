package com.luncert.vibotech.compat.vibotech;

import dan200.computercraft.api.lua.LuaFunction;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorageComponent extends BaseViboComponent {

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.ENERGY_STORAGE;
  }

  @LuaFunction
  public int getEnergy() {
    return getEnergyStorage().map(IEnergyStorage::getEnergyStored).orElse(0);
  }

  @LuaFunction
  public int getCapacity() {
    return getEnergyStorage().map(IEnergyStorage::getMaxEnergyStored).orElse(0);
  }

  @LuaFunction
  public boolean canExtract() {
    return getEnergyStorage().map(IEnergyStorage::canExtract).orElse(false);
  }

  @LuaFunction
  public boolean canReceive() {
    return getEnergyStorage().map(IEnergyStorage::canReceive).orElse(false);
  }
}
