package com.luncert.vibotech.foundation.client;

import com.luncert.vibotech.index.AllPartialModels;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class VibotechClient {

  public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
    modEventBus.addListener(VibotechClient::clientInit);
  }

  public static void clientInit(final FMLClientSetupEvent event) {
    AllPartialModels.init();
  }
}
