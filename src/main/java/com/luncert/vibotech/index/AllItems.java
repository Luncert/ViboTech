package com.luncert.vibotech.index;

import static com.luncert.vibotech.ViboTechMod.REGISTRATE;

import com.luncert.vibotech.compat.create.ViboMachineContraptionItem;
import com.luncert.vibotech.content.assemblestation.AssembleStationItem;
import com.luncert.vibotech.content.portableaccumulator.PortableAccumulatorItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

public class AllItems {

  static {
    REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_TAB);
  }

  public static final ItemEntry<ViboMachineContraptionItem> VIBO_MACHINE_CONTRAPTION =
      REGISTRATE.item("vibo_machine_contraption", ViboMachineContraptionItem::new)
          .register();

  public static final ItemEntry<AssembleStationItem> ASSEMBLE_STATION = REGISTRATE
      .item("assemble_station", AssembleStationItem::empty)
      .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("block/assemble_station")))
      .register();

  public static final ItemEntry<AssembleStationItem> ASSEMBLED_ASSEMBLE_STATION = REGISTRATE
      .item("assembled_assemble_station", AssembleStationItem::active)
      .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("block/assemble_station")))
      .register();

  public static final ItemEntry<PortableAccumulatorItem> PORTABLE_ACCUMULATOR = REGISTRATE
      .item("portable_accumulator", PortableAccumulatorItem::create)
      .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("block/portable_accumulator")))
      .register();

   // public static final ItemEntry<BucketItem> STEAM_BUCKET = REGISTRATE
   //     .item("steam_bucket", (p) -> new BucketItem(AllFluids.STEAM, p))
   //     .register();

  private static ItemEntry<Item> ingredient(String name) {
    return REGISTRATE.item(name, Item::new)
        .register();
  }

  private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
    return REGISTRATE.item(name, SequencedAssemblyItem::new)
        .register();
  }

  // Load this class

  public static void register() {}
}
