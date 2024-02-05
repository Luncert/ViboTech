package com.luncert.vibotech.index;

import static com.luncert.vibotech.ViboTechMod.REGISTRATE;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

import com.luncert.vibotech.content.AssembleStationBlock;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;

public class AllBlocks {

  static {
    REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_TAB);
  }

  public static final BlockEntry<AssembleStationBlock> ASSEMBLE_STATION =
      REGISTRATE.block("assemblestation", AssembleStationBlock::new)
          .initialProperties(SharedProperties::softMetal)
          .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
          .transform(pickaxeOnly())
          .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
              .getExistingFile(ctx.getId()), 0))
          .simpleItem()
          .register();

  public static void register() {

  }
}
