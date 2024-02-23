package com.luncert.vibotech.content.controlseat;

import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.mojang.blaze3d.platform.InputConstants;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.level.LevelAccessor;

public class ControlSeatComponent extends BaseViboComponent {

  public static Set<InputConstants.Key> receivedInputs = Collections.emptySet();
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
  public List<String> getInputs() {
    return receivedInputs.stream().map(InputConstants.Key::getName).toList();
  }

  public static void receivePressed(LevelAccessor world,
                                    UUID uniqueID,
                                    Collection<InputConstants.Key> activatedButtons,
                                    boolean pressed) {
    if (pressed) {
      receivedInputs = new HashSet<>(activatedButtons);
    } else {
      for (InputConstants.Key activated : activatedButtons) {
        receivedInputs.remove(activated);
      }
    }
  }
}
