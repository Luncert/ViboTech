package com.luncert.vibotech;

import java.nio.file.Path;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ViboTechMod.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

  public static final ForgeConfigSpec.IntValue PORTABLE_ACCUMULATOR_CAPACITY = BUILDER
      .comment("Portable Accumulator internal capacity per block in FE.")
      .defineInRange("portable_accumulator_capacity", 500000, 0, Integer.MAX_VALUE);

  public static final ForgeConfigSpec.IntValue PORTABLE_ACCUMULATOR_MAX_INPUT = BUILDER
      .comment("Portable Accumulator max input in FE/t (Energy transfer).")
      .defineInRange("portable_accumulator_max_input", 1000, 0, Integer.MAX_VALUE);
  public static final ForgeConfigSpec.IntValue PORTABLE_ACCUMULATOR_MAX_OUTPUT = BUILDER
      .comment("Portable Accumulator max output in FE/t (Energy transfer).")
      .defineInRange("portable_accumulator_max_output", 1000, 0, Integer.MAX_VALUE);

  static final ForgeConfigSpec SPEC = BUILDER.build();

  public static void loadConfig(ForgeConfigSpec spec, Path path) {
    // see create addition
  }

  @SubscribeEvent
  public static void onLoad(ModConfigEvent.Loading event) {
    // TODO:
  }

  @SubscribeEvent
  public static void onReload(ModConfigEvent.Reloading event) {
    // TODO:
  }
}
