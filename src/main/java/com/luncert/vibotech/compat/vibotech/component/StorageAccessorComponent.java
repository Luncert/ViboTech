package com.luncert.vibotech.compat.vibotech.component;

import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class StorageAccessorComponent extends BaseViboComponent {

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.STORAGE_ACCESSOR;
  }

  @LuaFunction
  public int getSlots() {
    return getStorageAccessor().map(IItemHandler::getSlots).orElse(0);
  }

  @LuaFunction
  public int getSlotLimit(int i) {
    return getStorageAccessor().map(itemHandler -> itemHandler.getSlotLimit(i)).orElse(0);
  }

  @LuaFunction
  public ItemStack getItemStackInSlot(int i) {
    return getStorageAccessor().map(itemHandler -> itemHandler.getStackInSlot(i)).orElse(ItemStack.EMPTY);
  }

  /**
   * Get capacity of target item in mounted storage.
   * @param itemId item id like minecraft:cobble_stone
   * @return capacity
   * @throws LuaException if itemId is invalid
   */
  @LuaFunction
  public int getCapacity(String itemId) throws LuaException {
    Item targetItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
    if (targetItem == null) {
      throw new LuaException("Invalid argument");
    }

    return getStorageAccessor().map(itemHandler -> {
      int capacity = 0;
      for (int i = 0, limit = itemHandler.getSlots(); i < limit; i++) {
        ItemStack stack = itemHandler.getStackInSlot(i);
        if (stack.is(targetItem)) {
          capacity += stack.getMaxStackSize() - stack.getCount();
        } else if (stack.isEmpty()) {
          capacity += targetItem.getMaxStackSize(stack);
        }
      }
      return capacity;
    }).orElse(0);
  }

  /**
   * Get count of target item in mounted storage.
   * @param itemId item id like minecraft:cobble_stone
   * @return count
   * @throws LuaException if itemId is invalid
   */
  @LuaFunction
  public int getCount(String itemId) throws LuaException {
    Item targetItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
    if (targetItem == null) {
      throw new LuaException("Invalid argument");
    }

    return getStorageAccessor().map(itemHandler -> {
      int count = 0;
      for (int i = 0, limit = itemHandler.getSlots(); i < limit; i++) {
        ItemStack stack = itemHandler.getStackInSlot(i);
        if (stack.is(targetItem)) {
          count += stack.getCount();
        }
      }
      return count;
    }).orElse(0);
  }
}
