package com.luncert.vibotech.content.controlseat;

import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.foundation.utility.WorldAttached;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.level.LevelAccessor;

public class ControlSeatComponent extends BaseViboComponent {

  public static WorldAttached<Set<InputConstants.Key>> receivedInputs =
      new WorldAttached<>(($) -> new HashSet<>());
  static final int TIMEOUT = 30;

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.CONTROL_SEAT;
  }

  @LuaFunction
  public boolean isKeyPressed(String name) {
    return ControlSeatClientHandler.isActuallyPressed(InputConstants.getKey(name));
  }

  @LuaFunction
  public Collection<InputConstants.Key> getInputs() {
    return receivedInputs.get(accessor.world);
  }

  public static void receivePressed(LevelAccessor world,
                                    UUID uniqueID,
                                    Collection<InputConstants.Key> activatedButtons,
                                    boolean pressed) {
    if (pressed) {
      receivedInputs.put(world, new HashSet<>(activatedButtons));
    } else {
      Set<InputConstants.Key> prev = receivedInputs.get(world);
      for (InputConstants.Key activated : activatedButtons) {
        prev.remove(activated);
      }
    }
  }
}
