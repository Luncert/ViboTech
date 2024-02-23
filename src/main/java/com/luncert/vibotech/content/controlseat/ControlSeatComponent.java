package com.luncert.vibotech.content.controlseat;

import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.utility.WorldAttached;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.level.LevelAccessor;
import org.slf4j.Logger;

public class ControlSeatComponent extends BaseViboComponent {

  private static final Logger LOGGER = LogUtils.getLogger();
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
  public Collection<InputConstants.Key> getInputs() {
    return receivedInputs;
  }

  public static void receivePressed(LevelAccessor world,
                                    UUID uniqueID,
                                    Collection<InputConstants.Key> activatedButtons,
                                    boolean pressed) {
    LOGGER.info("{}", activatedButtons);
    if (pressed) {
      receivedInputs = new HashSet<>(activatedButtons);
    } else {
      for (InputConstants.Key activated : activatedButtons) {
        receivedInputs.remove(activated);
      }
    }
  }
}
