package com.luncert.vibotech.foundation.foundation.events;

import static com.jozufozu.flywheel.backend.Backend.isGameActive;

import com.luncert.vibotech.ViboTechMod;
import com.luncert.vibotech.content.camera.CameraClientHandler;
import com.luncert.vibotech.content.controlseat.ControlSeatClientHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ViboTechMod.ID)
public class ForgeBusClientEvents {

  @SubscribeEvent
  public static void onTick(TickEvent.ClientTickEvent event) {
    if (!isGameActive())
      return;

    if (event.phase == TickEvent.Phase.START) {
      ControlSeatClientHandler.tick();
      CameraClientHandler.tick();
    }
  }
}
