package com.luncert.vibotech;

import com.luncert.vibotech.foundation.utility.Components;
import com.simibubi.create.AllBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AllCreativeModeTabs {

  private static final DeferredRegister<CreativeModeTab> REGISTER =
      DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ViboTechMod.ID);

  public static final RegistryObject<CreativeModeTab> BASE_CREATIVE_TAB = REGISTER.register("base",
      () -> CreativeModeTab.builder()
          .title(Components.translatable("itemGroup.vibotech.base"))
          .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
          .icon(AllBlocks.COGWHEEL::asStack)
          .displayItems(new RegistrateDisplayItemsGenerator(true, AllCreativeModeTabs.BASE_CREATIVE_TAB))
          .build());


  private static class RegistrateDisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {

    private final boolean addItems;
    private final RegistryObject<CreativeModeTab> tabFilter;

    public RegistrateDisplayItemsGenerator(boolean addItems, RegistryObject<CreativeModeTab> tabFilter) {
      this.addItems = addItems;
      this.tabFilter = tabFilter;
    }

    @Override
    public void accept(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {

    }
  }
}
