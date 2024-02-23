package com.luncert.vibotech.content.vibomachinecontrolseat;

import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.simibubi.create.foundation.utility.WorldAttached;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.level.LevelAccessor;

public class ControlSeatComponent extends BaseViboComponent {

  public static WorldAttached<Map<UUID, Set<Integer>>> receivedInputs =
      new WorldAttached<>(($) -> new HashMap<>());
  static final int TIMEOUT = 30;

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.CONTROL_SEAT;
  }

  @LuaFunction
  public Collection<Integer> getInput() {
    // return receivedInputs.get(accessor.world)
    return null;
  }

  public static void receivePressed(LevelAccessor world,
                                    UUID uniqueID,
                                    Collection<Integer> activatedButtons,
                                    boolean pressed) {
    receivedInputs.get(world).compute(uniqueID, (k, v) -> {
      if (pressed) {
        return new HashSet<>(activatedButtons);
      }

      if (v == null) {
        return new HashSet<>();
      }

      for (Integer activated : activatedButtons) {
        v.remove(activated);
      }
      return v;
    });
  }
}
