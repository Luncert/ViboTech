package com.luncert.vibotech.index;

import static com.luncert.vibotech.ViboTechMod.REGISTRATE;

import com.luncert.vibotech.content.AssembleStationBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class AllBlockEntityTypes {

  public static final BlockEntityEntry<AssembleStationBlockEntity> ASSEMBLE_STATION = REGISTRATE
      .blockEntity("assemble_station", AssembleStationBlockEntity::new)
      // .instance(() -> SchematicannonInstance::new)
      .validBlocks(AllBlocks.ASSEMBLE_STATION)
      // .renderer(() -> SchematicannonRenderer::new)
      .register();

  public static void register() {}
}
