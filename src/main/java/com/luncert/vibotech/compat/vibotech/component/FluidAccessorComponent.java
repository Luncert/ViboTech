package com.luncert.vibotech.compat.vibotech.component;

import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidAccessorComponent extends BaseViboComponent {
  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.FLUID_ACCESSOR;
  }

  @LuaFunction
  public final int getTanks() {
    return getFluidAccessor().map(IFluidHandler::getTanks).orElse(0);
  }

  @LuaFunction
  public final FluidStack getFluidInTank(int i) {
    return getFluidAccessor().map(fluidHandler -> fluidHandler.getFluidInTank(i)).orElse(FluidStack.EMPTY);
  }

  @LuaFunction
  public final int getTankCapacity(int i) {
    return getFluidAccessor().map(fluidHandler -> fluidHandler.getTankCapacity(i)).orElse(0);
  }

  @LuaFunction
  public final int getCapacity(String fluidId) throws LuaException {
    Fluid targetFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidId));
    if (targetFluid == null) {
      throw new LuaException("Invalid argument");
    }

    return getFluidAccessor().map(fluidHandler -> {
      int capacity = 0;
      for (int i = 0, limit = fluidHandler.getTanks(); i < limit; i++) {
        FluidStack fluidStack = fluidHandler.getFluidInTank(i);
        if (fluidStack.getFluid().isSame(targetFluid)) {
          capacity += fluidHandler.getTankCapacity(i) - fluidStack.getAmount();
        } else if (fluidStack.isEmpty()) {
          capacity += fluidHandler.getTankCapacity(i);
        }
      }
      return capacity;
    }).orElse(0);
  }

  @LuaFunction
  public final int getAmount(String fluidId) throws LuaException {
    Fluid targetFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidId));
    if (targetFluid == null) {
      throw new LuaException("Invalid argument");
    }

    return getFluidAccessor().map(fluidHandler -> {
      int amount = 0;
      for (int i = 0, limit = fluidHandler.getTanks(); i < limit; i++) {
        FluidStack fluidStack = fluidHandler.getFluidInTank(i);
        if (fluidStack.getFluid().isSame(targetFluid)) {
          amount += fluidStack.getAmount();
        }
      }
      return amount;
    }).orElse(0);
  }
}
