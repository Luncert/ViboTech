package com.luncert.vibotech.index;

import static com.luncert.vibotech.ViboTechMod.REGISTRATE;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

import com.luncert.vibotech.content.AssembleStationBlock;
import com.luncert.vibotech.content.AssembleStationBlockEntity;
import com.luncert.vibotech.content.AssembleStationItem;
import com.luncert.vibotech.content.TransportMachineAnchorBlock;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class AllBlocks {

  static {
    REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_TAB);
  }

  // public static final BlockEntry<AssembleStationBlock> ASSEMBLE_STATION =
  //     REGISTRATE.block("assemble_station", AssembleStationBlock::new)
  //         .initialProperties(SharedProperties::stone)
  //         .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
  //             .requiresCorrectToolForDrops())
  //         .properties(BlockBehaviour.Properties::noOcclusion)
  //         .transform(pickaxeOnly())
  //         .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
  //         .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
  //             .getExistingFile(ctx.getId()), 0))
  //         .item(AssembleStationItem::new)
  //         .build()
  //         .register();
  //
  // public static final BlockEntry<TransportMachineAnchorBlock> TRANSPORT_MACHINE_ANCHOR =
  //     REGISTRATE.block("transport_machine_anchor", TransportMachineAnchorBlock::new)
  //         .initialProperties(SharedProperties::stone)
  //         .blockstate((c, p) -> p.simpleBlock(c.get(), p.models()
  //             .getExistingFile(p.modLoc("block/vibotech/" + c.getName()))))
  //         .register();

  public static void register() {
  }
}
