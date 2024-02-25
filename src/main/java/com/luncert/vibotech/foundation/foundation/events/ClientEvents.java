package com.luncert.vibotech.foundation.foundation.events;

import static com.jozufozu.flywheel.backend.Backend.isGameActive;

import com.luncert.vibotech.content.camera.CameraClientHandler;
import com.luncert.vibotech.content.controlseat.ControlSeatClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

  @SubscribeEvent
  public static void onTick(TickEvent.ClientTickEvent event) {
    if (!isGameActive())
      return;

    Level world = Minecraft.getInstance().level;
    if (event.phase == TickEvent.Phase.START) {
      ControlSeatClientHandler.tick();
      CameraClientHandler.tick();
    }
  }
}
