package com.luncert.vibotech.index;

import static com.luncert.vibotech.ViboTechMod.REGISTRATE;

import com.luncert.vibotech.content2.assemblestation.AssembleStationBlockEntity;
import com.luncert.vibotech.content2.transportmachinecore.TransportMachineCoreBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class AllBlockEntityTypes {

  public static final BlockEntityEntry<AssembleStationBlockEntity> ASSEMBLE_STATION = REGISTRATE
      .blockEntity("assemble_station", AssembleStationBlockEntity::new)
      // .instance(() -> SchematicannonInstance::new)
      .validBlocks(AllBlocks.ASSEMBLE_STATION)
      // .renderer(() -> SchematicannonRenderer::new)
      .register();

  public static final BlockEntityEntry<TransportMachineCoreBlockEntity> TRANSPORT_MACHINE_CORE = REGISTRATE
      .blockEntity("transport_machine_core", TransportMachineCoreBlockEntity::new)
      .validBlocks(AllBlocks.ASSEMBLE_STATION)
      .register();

  public static void register() {}
}
