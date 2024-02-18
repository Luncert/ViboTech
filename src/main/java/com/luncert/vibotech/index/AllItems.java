package com.luncert.vibotech.index;

import static com.luncert.vibotech.ViboTechMod.REGISTRATE;

import com.luncert.vibotech.compat.create.TransportMachineContraptionItem;
import com.luncert.vibotech.content.assemblestation.AssembleStationItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

public class AllItems {

  static {
    REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_TAB);
  }

  public static final ItemEntry<TransportMachineContraptionItem> TRANSPORT_MACHINE_CONTRAPTION =
      REGISTRATE.item("transport_machine_contraption", TransportMachineContraptionItem::new)
          .register();

  public static final ItemEntry<AssembleStationItem> ASSEMBLE_STATION = REGISTRATE
      .item("assemble_station", AssembleStationItem::empty)
      .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("block/assemble_station")))
      .register();

  public static final ItemEntry<AssembleStationItem> ASSEMBLED_ASSEMBLE_STATION = REGISTRATE
      .item("assembled_assemble_station", AssembleStationItem::active)
      .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("block/assemble_station")))
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
