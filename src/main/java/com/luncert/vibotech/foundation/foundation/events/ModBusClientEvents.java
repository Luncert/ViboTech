package com.luncert.vibotech.foundation.foundation.events;

import com.luncert.vibotech.ViboTechMod;
import com.luncert.vibotech.index.AllOverlays;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ViboTechMod.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusClientEvents {

  private static final Logger LOGGER = LogUtils.getLogger();

  @SubscribeEvent
  public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
    // VanillaGuiOverlay
    event.registerAboveAll("camera_overlay", AllOverlays.CAMERA);
  }
}
