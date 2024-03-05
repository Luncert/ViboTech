package com.luncert.vibotech.index;

import static com.luncert.vibotech.ViboTechMod.REGISTRATE;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.tterrag.registrate.util.entry.FluidEntry;

public class AllFluids {

  public static final FluidEntry<VirtualFluid> AIR = REGISTRATE.virtualFluid("air")
      .lang("Air")
      .tag(AllTags.forgeFluidTag("air"))
      .register();

  //public static final FluidEntry<ForgeFlowingFluid.Flowing> AIR =
  //    REGISTRATE.standardFluid("air", (properties, s, f) -> new AirFluidType(properties))
  //        .lang("Air")
  //        .tag(AllTags.forgeFluidTag("air"))
  //        .properties(b -> b
  //            .viscosity(1500)
  //            .density(1400))
  //        .fluidProperties(p -> p.levelDecreasePerBlock(2)
  //            .tickRate(25)
  //            .slopeFindDistance(3)
  //            .explosionResistance(100f))
  //        .register();

  public static void register() {}

}
