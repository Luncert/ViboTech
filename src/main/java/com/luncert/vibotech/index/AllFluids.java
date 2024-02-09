package com.luncert.vibotech.index;

import static com.luncert.vibotech.ViboTechMod.REGISTRATE;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.tterrag.registrate.util.entry.FluidEntry;

public class AllFluids {

  public static final FluidEntry<VirtualFluid> STEAM = REGISTRATE.virtualFluid("steam")
      .lang("Steam")
      .tag(AllTags.forgeFluidTag("steam"))
      .register();

  public static void register() {}
}
