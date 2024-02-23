package com.luncert.vibotech.content.controlseat;

import com.luncert.vibotech.index.AllPackets;
import com.simibubi.create.foundation.utility.ControlsUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class ControlSeatClientHandler {

  public static int PACKET_RATE = 5;

  public static Collection<Integer> currentlyPressed = new HashSet<>();

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

    Vector<KeyMapping> controls = ControlsUtil.getControls();
    Collection<Integer> pressedKeys = new HashSet<>();
    for(int i = 0; i < controls.size(); ++i) {
      if (ControlsUtil.isActuallyPressed(controls.get(i))) {
        pressedKeys.add(i);
      }
    }

    Collection<Integer> newKeys = new HashSet<>(pressedKeys);
    Collection<Integer> releasedKeys = currentlyPressed;
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
    controls.forEach((kb) -> {
      kb.setDown(false);
    });
  }
}
