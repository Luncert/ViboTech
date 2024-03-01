package com.luncert.vibotech.content.controlseat;

import static com.mojang.blaze3d.platform.InputConstants.Key;
import static com.mojang.blaze3d.platform.InputConstants.Type;
import static com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM;
import static com.mojang.blaze3d.platform.InputConstants.Type.MOUSE;

import com.luncert.vibotech.compat.create.ViboMachineContraption;
import com.luncert.vibotech.compat.create.ViboMachineContraptionEntity;
import com.luncert.vibotech.index.AllBlocks;
import com.luncert.vibotech.index.AllKeys;
import com.luncert.vibotech.index.AllPackets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import org.antlr.v4.runtime.misc.Triple;
import org.slf4j.Logger;

public class ControlSeatClientHandler {

  private static final Logger LOGGER = LogUtils.getLogger();

  public static int PACKET_RATE = 5;

  public static Collection<Key> currentlyPressed = new HashSet<>();

  private static int packetCooldown;

  public static void tick() {
    if (packetCooldown > 0) {
      --packetCooldown;
    }

    Minecraft mc = Minecraft.getInstance();
    LocalPlayer player = mc.player;
    if (player == null) {
      return;
    }
    if (player.getVehicle() instanceof ControlSeatEntity) {
      LOGGER.info("x1");
      detectKeys();
    } else if (player.getVehicle() instanceof ViboMachineContraptionEntity contraptionEntity) {
      LOGGER.info("x2");
      ViboMachineContraption contraption = (ViboMachineContraption) contraptionEntity.getContraption();
      BlockPos seatPos = contraption.getSeatOf(player.getUUID());
      contraption.getBlocks().computeIfPresent(seatPos, (key, blockInfo) -> {
        if (blockInfo.state().is(AllBlocks.CONTROL_SEAT.get())) {
          detectKeys();
        }
        return blockInfo;
      });
    } else {
      AllPackets.getChannel().sendToServer(new ControlSeatInputPacket(currentlyPressed, false));
      currentlyPressed.clear();
    }
  }

  private static void detectKeys() {
    Collection<Key> pressedKeys = new HashSet<>(getInputs());
    Collection<Key> newKeys = new HashSet<>(pressedKeys);
    Collection<Key> releasedKeys = currentlyPressed;
    newKeys.removeAll(releasedKeys);
    releasedKeys.removeAll(pressedKeys);

    // Released Keys
    if (!releasedKeys.isEmpty()) {
      AllPackets.getChannel().sendToServer(new ControlSeatInputPacket(releasedKeys, false));
    }
    // Newly Pressed Keys
    if (!newKeys.isEmpty()) {
      AllPackets.getChannel().sendToServer(new ControlSeatInputPacket(newKeys, true));
      packetCooldown = PACKET_RATE;
    }
    // Keep alive Pressed Keys
    if (packetCooldown == 0 && !pressedKeys.isEmpty()) {
      AllPackets.getChannel().sendToServer(new ControlSeatInputPacket(pressedKeys, true));
      packetCooldown = PACKET_RATE;
    }

    currentlyPressed = pressedKeys;
  }

  private static List<Key> getInputs() {
    List<Key> keys = new ArrayList<>();
    for (Triple<Integer, Type, String> triple : KEYS) {
      var key = triple.b.getOrCreate(triple.a);
      if (AllKeys.isActuallyPressed(key)) {
        keys.add(key);
      }
    }
    return keys;
  }

  private static void addKey(InputConstants.Type pType, String pName, int pKeyCode) {
    KEYS.add(new Triple<>(pKeyCode, pType, pName));
  }

  private static final List<Triple<Integer, Type, String>> KEYS = new ArrayList<>();

  static {
    addKey(MOUSE, "key.mouse.left", 0);
    addKey(MOUSE, "key.mouse.right", 1);
    addKey(MOUSE, "key.mouse.middle", 2);
    addKey(MOUSE, "key.mouse.4", 3);
    addKey(MOUSE, "key.mouse.5", 4);
    addKey(MOUSE, "key.mouse.6", 5);
    addKey(MOUSE, "key.mouse.7", 6);
    addKey(MOUSE, "key.mouse.8", 7);
    addKey(KEYSYM, "key.keyboard.0", 48);
    addKey(KEYSYM, "key.keyboard.1", 49);
    addKey(KEYSYM, "key.keyboard.2", 50);
    addKey(KEYSYM, "key.keyboard.3", 51);
    addKey(KEYSYM, "key.keyboard.4", 52);
    addKey(KEYSYM, "key.keyboard.5", 53);
    addKey(KEYSYM, "key.keyboard.6", 54);
    addKey(KEYSYM, "key.keyboard.7", 55);
    addKey(KEYSYM, "key.keyboard.8", 56);
    addKey(KEYSYM, "key.keyboard.9", 57);
    addKey(KEYSYM, "key.keyboard.a", 65);
    addKey(KEYSYM, "key.keyboard.b", 66);
    addKey(KEYSYM, "key.keyboard.c", 67);
    addKey(KEYSYM, "key.keyboard.d", 68);
    addKey(KEYSYM, "key.keyboard.e", 69);
    addKey(KEYSYM, "key.keyboard.f", 70);
    addKey(KEYSYM, "key.keyboard.g", 71);
    addKey(KEYSYM, "key.keyboard.h", 72);
    addKey(KEYSYM, "key.keyboard.i", 73);
    addKey(KEYSYM, "key.keyboard.j", 74);
    addKey(KEYSYM, "key.keyboard.k", 75);
    addKey(KEYSYM, "key.keyboard.l", 76);
    addKey(KEYSYM, "key.keyboard.m", 77);
    addKey(KEYSYM, "key.keyboard.n", 78);
    addKey(KEYSYM, "key.keyboard.o", 79);
    addKey(KEYSYM, "key.keyboard.p", 80);
    addKey(KEYSYM, "key.keyboard.q", 81);
    addKey(KEYSYM, "key.keyboard.r", 82);
    addKey(KEYSYM, "key.keyboard.s", 83);
    addKey(KEYSYM, "key.keyboard.t", 84);
    addKey(KEYSYM, "key.keyboard.u", 85);
    addKey(KEYSYM, "key.keyboard.v", 86);
    addKey(KEYSYM, "key.keyboard.w", 87);
    addKey(KEYSYM, "key.keyboard.x", 88);
    addKey(KEYSYM, "key.keyboard.y", 89);
    addKey(KEYSYM, "key.keyboard.z", 90);
    addKey(KEYSYM, "key.keyboard.f1", 290);
    addKey(KEYSYM, "key.keyboard.f2", 291);
    addKey(KEYSYM, "key.keyboard.f3", 292);
    addKey(KEYSYM, "key.keyboard.f4", 293);
    addKey(KEYSYM, "key.keyboard.f5", 294);
    addKey(KEYSYM, "key.keyboard.f6", 295);
    addKey(KEYSYM, "key.keyboard.f7", 296);
    addKey(KEYSYM, "key.keyboard.f8", 297);
    addKey(KEYSYM, "key.keyboard.f9", 298);
    addKey(KEYSYM, "key.keyboard.f10", 299);
    addKey(KEYSYM, "key.keyboard.f11", 300);
    addKey(KEYSYM, "key.keyboard.f12", 301);
    addKey(KEYSYM, "key.keyboard.f13", 302);
    addKey(KEYSYM, "key.keyboard.f14", 303);
    addKey(KEYSYM, "key.keyboard.f15", 304);
    addKey(KEYSYM, "key.keyboard.f16", 305);
    addKey(KEYSYM, "key.keyboard.f17", 306);
    addKey(KEYSYM, "key.keyboard.f18", 307);
    addKey(KEYSYM, "key.keyboard.f19", 308);
    addKey(KEYSYM, "key.keyboard.f20", 309);
    addKey(KEYSYM, "key.keyboard.f21", 310);
    addKey(KEYSYM, "key.keyboard.f22", 311);
    addKey(KEYSYM, "key.keyboard.f23", 312);
    addKey(KEYSYM, "key.keyboard.f24", 313);
    addKey(KEYSYM, "key.keyboard.f25", 314);
    addKey(KEYSYM, "key.keyboard.num.lock", 282);
    addKey(KEYSYM, "key.keyboard.keypad.0", 320);
    addKey(KEYSYM, "key.keyboard.keypad.1", 321);
    addKey(KEYSYM, "key.keyboard.keypad.2", 322);
    addKey(KEYSYM, "key.keyboard.keypad.3", 323);
    addKey(KEYSYM, "key.keyboard.keypad.4", 324);
    addKey(KEYSYM, "key.keyboard.keypad.5", 325);
    addKey(KEYSYM, "key.keyboard.keypad.6", 326);
    addKey(KEYSYM, "key.keyboard.keypad.7", 327);
    addKey(KEYSYM, "key.keyboard.keypad.8", 328);
    addKey(KEYSYM, "key.keyboard.keypad.9", 329);
    addKey(KEYSYM, "key.keyboard.keypad.add", 334);
    addKey(KEYSYM, "key.keyboard.keypad.decimal", 330);
    addKey(KEYSYM, "key.keyboard.keypad.enter", 335);
    addKey(KEYSYM, "key.keyboard.keypad.equal", 336);
    addKey(KEYSYM, "key.keyboard.keypad.multiply", 332);
    addKey(KEYSYM, "key.keyboard.keypad.divide", 331);
    addKey(KEYSYM, "key.keyboard.keypad.subtract", 333);
    addKey(KEYSYM, "key.keyboard.down", 264);
    addKey(KEYSYM, "key.keyboard.left", 263);
    addKey(KEYSYM, "key.keyboard.right", 262);
    addKey(KEYSYM, "key.keyboard.up", 265);
    addKey(KEYSYM, "key.keyboard.apostrophe", 39);
    addKey(KEYSYM, "key.keyboard.backslash", 92);
    addKey(KEYSYM, "key.keyboard.comma", 44);
    addKey(KEYSYM, "key.keyboard.equal", 61);
    addKey(KEYSYM, "key.keyboard.grave.accent", 96);
    addKey(KEYSYM, "key.keyboard.left.bracket", 91);
    addKey(KEYSYM, "key.keyboard.minus", 45);
    addKey(KEYSYM, "key.keyboard.period", 46);
    addKey(KEYSYM, "key.keyboard.right.bracket", 93);
    addKey(KEYSYM, "key.keyboard.semicolon", 59);
    addKey(KEYSYM, "key.keyboard.slash", 47);
    addKey(KEYSYM, "key.keyboard.space", 32);
    addKey(KEYSYM, "key.keyboard.tab", 258);
    addKey(KEYSYM, "key.keyboard.left.alt", 342);
    addKey(KEYSYM, "key.keyboard.left.control", 341);
    addKey(KEYSYM, "key.keyboard.left.shift", 340);
    addKey(KEYSYM, "key.keyboard.left.win", 343);
    addKey(KEYSYM, "key.keyboard.right.alt", 346);
    addKey(KEYSYM, "key.keyboard.right.control", 345);
    addKey(KEYSYM, "key.keyboard.right.shift", 344);
    addKey(KEYSYM, "key.keyboard.right.win", 347);
    addKey(KEYSYM, "key.keyboard.enter", 257);
    addKey(KEYSYM, "key.keyboard.escape", 256);
    addKey(KEYSYM, "key.keyboard.backspace", 259);
    addKey(KEYSYM, "key.keyboard.delete", 261);
    addKey(KEYSYM, "key.keyboard.end", 269);
    addKey(KEYSYM, "key.keyboard.home", 268);
    addKey(KEYSYM, "key.keyboard.insert", 260);
    addKey(KEYSYM, "key.keyboard.page.down", 267);
    addKey(KEYSYM, "key.keyboard.page.up", 266);
    addKey(KEYSYM, "key.keyboard.caps.lock", 280);
    addKey(KEYSYM, "key.keyboard.pause", 284);
    addKey(KEYSYM, "key.keyboard.scroll.lock", 281);
    addKey(KEYSYM, "key.keyboard.menu", 348);
    addKey(KEYSYM, "key.keyboard.print.screen", 283);
    addKey(KEYSYM, "key.keyboard.world.1", 161);
    addKey(KEYSYM, "key.keyboard.world.2", 162);
  }
}
