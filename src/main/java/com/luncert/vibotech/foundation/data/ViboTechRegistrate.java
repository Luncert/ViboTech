package com.luncert.vibotech.foundation.data;

import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.AbstractRegistrate;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class ViboTechRegistrate extends AbstractRegistrate<ViboTechRegistrate> {

  @Nullable
  protected Function<Item, TooltipModifier> currentTooltipModifierFactory;
  @Nullable
  protected RegistryObject<CreativeModeTab> currentTab;

  protected ViboTechRegistrate(String modid) {
    super(modid);
  }

  public static ViboTechRegistrate create(String modid) {
    return new ViboTechRegistrate(modid);
  }

  public ViboTechRegistrate setTooltipModifierFactory(@Nullable Function<Item, TooltipModifier> factory) {
    currentTooltipModifierFactory = factory;
    return self();
  }

  @Nullable
  public ViboTechRegistrate setCreativeTab(RegistryObject<CreativeModeTab> tab) {
    currentTab = tab;
    return self();
  }
}
