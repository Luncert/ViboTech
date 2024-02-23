package com.luncert.vibotech.index;

import static com.luncert.vibotech.ViboTechMod.REGISTRATE;
import static com.simibubi.create.AllInteractionBehaviours.interactionBehaviour;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

import com.luncert.vibotech.content.assemblestation.AssembleStationBlock;
import com.luncert.vibotech.content.camera.CameraBlock;
import com.luncert.vibotech.content.gastank.GasTankBlock;
import com.luncert.vibotech.content.geoscanner.GeoScannerBlock;
import com.luncert.vibotech.content.photovoltaic.PhotovoltaicPanelBlock;
import com.luncert.vibotech.content.portableaccumulator.PortableAccumulatorBlock;
import com.luncert.vibotech.content.thruster.ThrusterBlock;
import com.luncert.vibotech.content.controlseat.ControlSeatBlock;
import com.luncert.vibotech.content.vibomachinecore.ViboMachineCoreBlock;
import com.luncert.vibotech.content.vibomachinecore.ViboMachineCoreInteractionBehaviour;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
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
          .register();

  public static final BlockEntry<ViboMachineCoreBlock> VIBO_MACHINE_CORE =
      REGISTRATE.block("vibo_machine_core", ViboMachineCoreBlock::new)
          .initialProperties(SharedProperties::stone)
          .onRegister(interactionBehaviour(new ViboMachineCoreInteractionBehaviour()))
          .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
              .requiresCorrectToolForDrops())
          .properties(BlockBehaviour.Properties::noOcclusion)
          .transform(pickaxeOnly())
          .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
          .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
              .getExistingFile(ctx.getId()), 0))
          .simpleItem()
          .register();

  public static final BlockEntry<ControlSeatBlock> CONTROL_SEAT =
      REGISTRATE.block("control_seat", ControlSeatBlock::new)
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

  public static final BlockEntry<CameraBlock> CAMERA =
      REGISTRATE.block("camera", CameraBlock::new)
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

  public static final BlockEntry<GeoScannerBlock> GEO_SCANNER =
      REGISTRATE.block("geo_scanner", GeoScannerBlock::new)
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

  public static final BlockEntry<GasTankBlock> GAS_TANK =
      REGISTRATE.block("gas_tank", GasTankBlock::new)
          .initialProperties(SharedProperties::softMetal)
          .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
              .requiresCorrectToolForDrops())
          .addLayer(() -> RenderType::translucent)
          .properties(BlockBehaviour.Properties::noOcclusion)
          .transform(BlockStressDefaults.setCapacity(64d))
          .transform(axeOrPickaxe())
          .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
          .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
              .getExistingFile(ctx.getId()), 0))
          .simpleItem()
          .register();

  public static final BlockEntry<PortableAccumulatorBlock> PORTABLE_ACCUMULATOR =
      REGISTRATE.block("portable_accumulator", PortableAccumulatorBlock::new)
          .initialProperties(SharedProperties::stone)
          .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
              .requiresCorrectToolForDrops())
          .properties(BlockBehaviour.Properties::noOcclusion)
          .transform(pickaxeOnly())
          .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
          .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
              .getExistingFile(ctx.getId()), 0))
          .register();

  public static final BlockEntry<ThrusterBlock> THRUSTER =
      REGISTRATE.block("thruster", ThrusterBlock::new)
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

  public static final BlockEntry<PhotovoltaicPanelBlock> PHOTOVOLTAIC_PANEL =
      REGISTRATE.block("photovoltaic_panel", PhotovoltaicPanelBlock::new)
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

  public static void register() {
  }
}
