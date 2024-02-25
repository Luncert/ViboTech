package com.luncert.vibotech.index;

import com.luncert.vibotech.ViboTechMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum AllKeys {

  BASE("base", GLFW.GLFW_KEY_ESCAPE),
  ;

  private KeyMapping keybind;
  private String description;
  private int key;
  private boolean modifiable;

  AllKeys(String description, int defaultKey) {
    this.description = ViboTechMod.ID + ".keyinfo." + description;
    this.key = defaultKey;
    this.modifiable = !description.isEmpty();
  }

  @SubscribeEvent
  public static void register(RegisterKeyMappingsEvent event) {
    for (AllKeys key : values()) {
      key.keybind = new KeyMapping(key.description, key.key, ViboTechMod.NAME);
      if (!key.modifiable)
        continue;

      event.register(key.keybind);
    }
  }

  public static boolean isActuallyPressed(InputConstants.Key key) {
    return key.getType() == InputConstants.Type.MOUSE
        ? com.simibubi.create.AllKeys.isMouseButtonDown(key.getValue())
        : com.simibubi.create.AllKeys.isKeyDown(key.getValue());
  }
}
