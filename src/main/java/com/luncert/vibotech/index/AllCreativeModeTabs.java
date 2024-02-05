package com.luncert.vibotech.index;

import static com.luncert.vibotech.ViboTechMod.REGISTRATE;

import com.luncert.vibotech.ViboTechMod;
import com.luncert.vibotech.foundation.utility.Components;
import com.mojang.logging.LogUtils;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AllCreativeModeTabs {

  private static final Logger LOGGER = LogUtils.getLogger();

  private static final DeferredRegister<CreativeModeTab> REGISTER =
      DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ViboTechMod.ID);

  public static final RegistryObject<CreativeModeTab> BASE_TAB = REGISTER.register("base",
      () -> CreativeModeTab.builder()
          .title(Components.translatable("itemGroup.vibotech.base"))
          .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
          .icon(AllBlocks.COGWHEEL::asStack)
          .displayItems(new RegistrateDisplayItemsGenerator())
          .build());

  public static void register(IEventBus modEventBus) {
    REGISTER.register(modEventBus);
  }

  private static class RegistrateDisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {

    private List<Item> collectBlocks(RegistryObject<CreativeModeTab> tab, Predicate<Item> exclusionPredicate) {
      List<Item> items = new ReferenceArrayList<>();
      for (RegistryEntry<Block> entry : REGISTRATE.getAll(Registries.BLOCK)) {
        if (!CreateRegistrate.isInCreativeTab(entry, tab))
          continue;
        Item item = entry.get()
            .asItem();
        if (item == Items.AIR)
          continue;
        if (!exclusionPredicate.test(item))
          items.add(item);
      }
      items = new ReferenceArrayList<>(new ReferenceLinkedOpenHashSet<>(items));
      return items;
    }

    private List<Item> collectItems(RegistryObject<CreativeModeTab> tab, Predicate<Item> exclusionPredicate) {
      List<Item> items = new ReferenceArrayList<>();


      for (RegistryEntry<Item> entry : REGISTRATE.getAll(Registries.ITEM)) {
        if (!CreateRegistrate.isInCreativeTab(entry, tab))
          continue;
        Item item = entry.get();
        if (item instanceof BlockItem)
          continue;
        if (!exclusionPredicate.test(item))
          items.add(item);
      }
      return items;
    }

    private static void outputAll(CreativeModeTab.Output output, List<Item> items) {
      for (Item item : items) {
        output.accept(item);
      }
    }

    List<Item> exclude = List.of();

    @Override
    public void accept(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
      List<Item> items = new LinkedList<>();
      items.addAll(collectBlocks(BASE_TAB, (item) -> {
        return false;
      }));
      items.addAll(collectItems(BASE_TAB, (item) -> exclude.contains(item)));

      outputAll(output, items);
    }
  }
}
