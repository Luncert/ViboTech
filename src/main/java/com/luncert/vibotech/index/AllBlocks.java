package com.luncert.vibotech.index;

import static com.luncert.vibotech.ViboTechMod.REGISTRATE;
import static com.simibubi.create.AllInteractionBehaviours.interactionBehaviour;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

import com.luncert.vibotech.content2.assemblestation.AssembleStationBlock;
import com.luncert.vibotech.content2.assemblestation.AssembleStationItem;
import com.luncert.vibotech.content2.monitor.MonitorBlock;
import com.luncert.vibotech.content2.transportmachinecontrol.TransportMachineControlBlock;
import com.luncert.vibotech.content2.transportmachinecore.TransportMachineCoreBlock;
import com.luncert.vibotech.content2.transportmachinecore.TransportMachineCoreInteractionBehaviour;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class AllBlocks {

  static {
    REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_TAB);
  }

  public static final BlockEntry<AssembleStationBlock> ASSEMBLE_STATION =
      REGISTRATE.block("assemble_station", AssembleStationBlock::new)
          .initialProperties(SharedProperties::stone)
          .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
              .requiresCorrectToolForDrops())
          .properties(BlockBehaviour.Properties::noOcclusion)
          .transform(pickaxeOnly())
          .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
          .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
              .getExistingFile(ctx.getId()), 0))
          .item(AssembleStationItem::new)
          .build()
          .register();

  public static final BlockEntry<TransportMachineCoreBlock> TRANSPORT_MACHINE_CORE =
      REGISTRATE.block("transport_machine_core", TransportMachineCoreBlock::new)
          .initialProperties(SharedProperties::stone)
          .onRegister(interactionBehaviour(new TransportMachineCoreInteractionBehaviour()))
          .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
              .requiresCorrectToolForDrops())
          .properties(BlockBehaviour.Properties::noOcclusion)
          .transform(pickaxeOnly())
          .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
          .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
              .getExistingFile(ctx.getId()), 0))
          .simpleItem()
          .register();


  public static final BlockEntry<TransportMachineControlBlock> TRANSPORT_MACHINE_CONTROL =
      REGISTRATE.block("transport_machine_control", TransportMachineControlBlock::new)
          .initialProperties(SharedProperties::stone)
          .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
              .requiresCorrectToolForDrops())
          .properties(BlockBehaviour.Properties::noOcclusion)
          .transform(pickaxeOnly())
          .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
          .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
              .getExistingFile(ctx.getId()), 0))
          .simpleItem()
          .register();

  public static final BlockEntry<MonitorBlock> MONITOR =
      REGISTRATE.block("monitor", MonitorBlock::new)
          .initialProperties(SharedProperties::stone)
          .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
              .requiresCorrectToolForDrops())
          .properties(BlockBehaviour.Properties::noOcclusion)
          .transform(pickaxeOnly())
          .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
          .blockstate((ctx, prov) -> prov.directionalBlock(ctx.getEntry(), prov.models()
              .getExistingFile(ctx.getId()), 0))
          .simpleItem()
          .register();

  // public static final BlockEntry<GasTankBlock> GAS_TANK =
  //     REGISTRATE.block("gas_tank", GasTankBlock::new)
  //         .initialProperties(SharedProperties::softMetal)
  //         .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
  //             .requiresCorrectToolForDrops())
  //         .addLayer(() -> RenderType::translucent)
  //         .properties(BlockBehaviour.Properties::noOcclusion)
  //         .transform(BlockStressDefaults.setCapacity(64d))
  //         .transform(axeOrPickaxe())
  //         .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
  //         .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
  //             .getExistingFile(ctx.getId()), 0))
  //         .simpleItem()
  //         .register();

  public static void register() {
  }
}
