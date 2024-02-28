package com.luncert.vibotech.compat.vibotech.component;

import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyAccessorComponent extends BaseViboComponent {

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.ENERGY_ACCESSOR;
  }

  @LuaFunction
  public final int getEnergy() {
    return getEnergyAccessor().map(IEnergyStorage::getEnergyStored).orElse(0);
  }

  @LuaFunction
  public final int getCapacity() {
    return getEnergyAccessor().map(IEnergyStorage::getMaxEnergyStored).orElse(0);
  }

  @LuaFunction
  public final boolean canExtract() {
    return getEnergyAccessor().map(IEnergyStorage::canExtract).orElse(false);
  }

  @LuaFunction
  public final boolean canReceive() {
    return getEnergyAccessor().map(IEnergyStorage::canReceive).orElse(false);
  }

  @LuaFunction
  public final float getPercent() {
    return getEnergyAccessor().map(energy ->
        energy.getEnergyStored() / (float) energy.getMaxEnergyStored()).orElse(0f);
  }
}
