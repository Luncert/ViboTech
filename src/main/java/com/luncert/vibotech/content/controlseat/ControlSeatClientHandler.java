package com.luncert.vibotech.content.controlseat;

import static com.mojang.blaze3d.platform.InputConstants.*;

import com.luncert.vibotech.index.AllPackets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.utility.ControlsUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import org.slf4j.Logger;

public class ControlSeatClientHandler {

  private static final Logger LOGGER = LogUtils.getLogger();
  private static final Field FIELD_MAP;

  static {
    try {
      FIELD_MAP = Type.class.getDeclaredField("map");
      FIELD_MAP.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  public static int PACKET_RATE = 5;

  public static Collection<Key> currentlyPressed = new HashSet<>();

  private static int packetCooldown;

  public static void tick() {
    if (packetCooldown > 0) {
      --packetCooldown;
    }

    Minecraft mc = Minecraft.getInstance();
    LocalPlayer player = mc.player;

    if (player == null || !(player.getVehicle() instanceof ControlSeatEntity)) {
      return;
    }

    Collection<Key> pressedKeys = new HashSet<>(getInputs());
    Collection<Key> newKeys = new HashSet<>(pressedKeys);
    Collection<Key> releasedKeys = currentlyPressed;
    newKeys.removeAll(releasedKeys);
    releasedKeys.removeAll(pressedKeys);


    if (!releasedKeys.isEmpty()) {
      AllPackets.getChannel().sendToServer(new ControlSeatInputPacket(releasedKeys, false));
    }
    if (!newKeys.isEmpty()) {
      AllPackets.getChannel().sendToServer(new ControlSeatInputPacket(newKeys, true));
      packetCooldown = PACKET_RATE;
    }
    if (packetCooldown == 0 && !pressedKeys.isEmpty()) {
      AllPackets.getChannel().sendToServer(new ControlSeatInputPacket(pressedKeys, true));
      packetCooldown = PACKET_RATE;
    }

    currentlyPressed = pressedKeys;
    // controls.forEach((kb) -> {
    //   kb.setDown(false);
    // });
  }

  @SuppressWarnings("unchecked")
  private static List<Key> getInputs() {
    List<Key> keys = new ArrayList<>();
    try {
      ((Int2ObjectMap<InputConstants.Key>) FIELD_MAP.get(Type.KEYSYM))
          .values()
          .stream().filter(k -> k.getValue() > -1 && isActuallyPressed(k))
          .forEach(keys::add);
      ((Int2ObjectMap<InputConstants.Key>) FIELD_MAP.get(Type.MOUSE))
          .values()
          .stream().filter(k -> k.getValue() > -1 && isActuallyPressed(k))
          .forEach(keys::add);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    return keys;
  }

  public static boolean isActuallyPressed(Key key) {
    return key.getType() == Type.MOUSE ? AllKeys.isMouseButtonDown(key.getValue()) : AllKeys.isKeyDown(key.getValue());
  }
}
