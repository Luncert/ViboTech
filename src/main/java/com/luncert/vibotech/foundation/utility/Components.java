package com.luncert.vibotech.foundation.utility;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class Components {

  public static MutableComponent translatable(String key) {
    return Component.translatable(key);
  }

  public static MutableComponent translatable(String key, Object... args) {
    return Component.translatable(key, args);
  }
}
