package com.luncert.vibotech.index;

import static com.luncert.vibotech.ViboTechMod.REGISTRATE;

import com.luncert.vibotech.content.aircompressor.AirCompressorBlockEntity;
import com.luncert.vibotech.content.aircompressor.AirCompressorInstance;
import com.luncert.vibotech.content.aircompressor.AirCompressorRenderer;
import com.luncert.vibotech.content.assemblestation.AssembleStationBlockEntity;
import com.luncert.vibotech.content.camera.CameraBlockEntity;
import com.luncert.vibotech.content.entitydetector.EntityDetectorBlock;
import com.luncert.vibotech.content.entitydetector.EntityDetectorBlockEntity;
import com.luncert.vibotech.content.gastank.GasTankBlockEntity;
import com.luncert.vibotech.content.gastank.GasTankBlockEntityRenderer;
import com.luncert.vibotech.content.geoscanner.GeoScannerBlockEntity;
import com.luncert.vibotech.content.photovoltaic.PhotovoltaicPanelBlockEntity;
import com.luncert.vibotech.content.portableaccumulator.PortableAccumulatorBlockEntity;
import com.luncert.vibotech.content.thruster.ThrusterBlockEntity;
import com.luncert.vibotech.content.controlseat.ControlSeatBlockEntity;
import com.luncert.vibotech.content.vibomachinecore.ViboMachineCoreBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class AllBlockEntityTypes {

  public static final BlockEntityEntry<AssembleStationBlockEntity> ASSEMBLE_STATION = REGISTRATE
      .blockEntity("assemble_station", AssembleStationBlockEntity::new)
      // .instance(() -> SchematicannonInstance::new)
      .validBlocks(AllBlocks.ASSEMBLE_STATION)
      // .renderer(() -> SchematicannonRenderer::new)
      .register();

  public static final BlockEntityEntry<ViboMachineCoreBlockEntity> VIBO_MACHINE_CORE = REGISTRATE
      .blockEntity("vibo_machine_core", ViboMachineCoreBlockEntity::new)
      .validBlocks(AllBlocks.ASSEMBLE_STATION)
      .register();

  public static final BlockEntityEntry<CameraBlockEntity> CAMERA = REGISTRATE
      .blockEntity("camera", CameraBlockEntity::new)
      .validBlocks(AllBlocks.CAMERA)
      .register();

  public static final BlockEntityEntry<GeoScannerBlockEntity> GEO_SCANNER = REGISTRATE
      .blockEntity("geo_scanner", GeoScannerBlockEntity::new)
      .validBlocks(AllBlocks.GEO_SCANNER)
      .register();

  public static final BlockEntityEntry<GasTankBlockEntity> GAS_TANK = REGISTRATE
      .blockEntity("gas_tank", GasTankBlockEntity::new)
      .validBlocks(AllBlocks.GAS_TANK)
      .renderer(() -> GasTankBlockEntityRenderer::new)
      .register();

  public static final BlockEntityEntry<PortableAccumulatorBlockEntity> PORTABLE_ACCUMULATOR = REGISTRATE
      .blockEntity("portable_accumulator", PortableAccumulatorBlockEntity::new)
      .validBlocks(AllBlocks.PORTABLE_ACCUMULATOR)
      .register();

  public static final BlockEntityEntry<ThrusterBlockEntity> THRUSTER = REGISTRATE
      .blockEntity("thruster", ThrusterBlockEntity::new)
      .validBlocks(AllBlocks.THRUSTER)
      .register();

  public static final BlockEntityEntry<PhotovoltaicPanelBlockEntity> PHOTOVOLTAIC_PANEL = REGISTRATE
      .blockEntity("photovoltaic_panel", PhotovoltaicPanelBlockEntity::new)
      .validBlocks(AllBlocks.PHOTOVOLTAIC_PANEL)
      .register();

  public static final BlockEntityEntry<ControlSeatBlockEntity> CONTROL_SEAT = REGISTRATE
      .blockEntity("control_seat", ControlSeatBlockEntity::new)
      .validBlocks(AllBlocks.CONTROL_SEAT)
      .register();

  public static final BlockEntityEntry<EntityDetectorBlockEntity> ENTITY_DETECTOR = REGISTRATE
      .blockEntity("entity_detector", EntityDetectorBlockEntity::new)
      .validBlocks(AllBlocks.ENTITY_DETECTOR)
      .register();

  public static final BlockEntityEntry<AirCompressorBlockEntity> AIR_COMPRESSOR = REGISTRATE
      .blockEntity("air_compressor", AirCompressorBlockEntity::new)
      .instance(() -> AirCompressorInstance::new)
      .validBlocks(AllBlocks.AIR_COMPRESSOR)
      .renderer(() -> AirCompressorRenderer::new)
      .register();

  public static void register() {}
}
