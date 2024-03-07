package com.luncert.vibotech.index;

import static com.luncert.vibotech.ViboTechMod.REGISTRATE;
import static com.simibubi.create.AllInteractionBehaviours.interactionBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.luncert.vibotech.content.aircompressor.AirCompressorBlock;
import com.luncert.vibotech.content.airpipe.AirPipeBlock;
import com.luncert.vibotech.content.airpipe.PipeAttachmentModel;
import com.luncert.vibotech.content.assemblestation.AssembleStationBlock;
import com.luncert.vibotech.content.camera.CameraBlock;
import com.luncert.vibotech.content.controlseat.ControlSeatBlock;
import com.luncert.vibotech.content.entitydetector.EntityDetectorBlock;
import com.luncert.vibotech.content.gastank.GasTankBlock;
import com.luncert.vibotech.content.geoscanner.GeoScannerBlock;
import com.luncert.vibotech.content.photovoltaic.PhotovoltaicPanelBlock;
import com.luncert.vibotech.content.portableaccumulator.PortableAccumulatorBlock;
import com.luncert.vibotech.content.portableaccumulator.PortableAccumulatorMovement;
import com.luncert.vibotech.content.thruster.ThrusterBlock;
import com.luncert.vibotech.content.vibomachinecore.ViboMachineCoreBlock;
import com.luncert.vibotech.content.vibomachinecore.ViboMachineCoreInteractionBehaviour;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Pointing;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import org.apache.commons.lang3.tuple.Pair;

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
          // .onRegister(interactionBehaviour(new CameraInteractionBehaviour()))
          .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
              .requiresCorrectToolForDrops())
          .properties(BlockBehaviour.Properties::noOcclusion)
          .transform(pickaxeOnly())
          .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
          .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
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
          .onRegister(AllMovementBehaviours.movementBehaviour(new PortableAccumulatorMovement()))
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

  public static final BlockEntry<EntityDetectorBlock> ENTITY_DETECTOR =
      REGISTRATE.block("entity_detector", EntityDetectorBlock::new)
          .initialProperties(SharedProperties::stone)
          .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
              .requiresCorrectToolForDrops())
          .properties(BlockBehaviour.Properties::noOcclusion)
          .addLayer(() -> RenderType::translucent)
          .transform(pickaxeOnly())
          .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
          .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
              .getExistingFile(ctx.getId()), 0))
          .simpleItem()
          .register();

  public static final BlockEntry<AirCompressorBlock> AIR_COMPRESSOR =
      REGISTRATE.block("air_compressor", AirCompressorBlock::new)
          .initialProperties(SharedProperties::stone)
          .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
              .requiresCorrectToolForDrops())
          .properties(BlockBehaviour.Properties::noOcclusion)
          .addLayer(() -> RenderType::translucent)
          .transform(pickaxeOnly())
          .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
          .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
              .getExistingFile(ctx.getId()), 0))
          .simpleItem()
          .register();

  public static final BlockEntry<AirPipeBlock> AIR_PIPE =
      REGISTRATE.block("air_pipe", AirPipeBlock::new)
          .initialProperties(SharedProperties::copperMetal)
          .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
              .forceSolidOn()
              .requiresCorrectToolForDrops())
          .transform(pickaxeOnly())
          .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
          .blockstate(pipe())
          .onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::new))
          .transform(BlockStressDefaults.setImpact(4.0))
          .item()
          .transform(customItemModel())
          .register();

  public static void register() {
  }

  public static <P extends AirPipeBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> pipe() {
    return (c, p) -> {
      String path = "block/" + c.getName();

      String LU = "lu";
      String RU = "ru";
      String LD = "ld";
      String RD = "rd";
      String LR = "lr";
      String UD = "ud";
      String U = "u";
      String D = "d";
      String L = "l";
      String R = "r";

      List<String> orientations = ImmutableList.of(LU, RU, LD, RD, LR, UD, U, D, L, R);
      Map<String, Pair<Integer, Integer>> uvs = ImmutableMap.<String, Pair<Integer, Integer>>builder()
          .put(LU, Pair.of(12, 4))
          .put(RU, Pair.of(8, 4))
          .put(LD, Pair.of(12, 0))
          .put(RD, Pair.of(8, 0))
          .put(LR, Pair.of(4, 8))
          .put(UD, Pair.of(0, 8))
          .put(U, Pair.of(4, 4))
          .put(D, Pair.of(0, 0))
          .put(L, Pair.of(4, 0))
          .put(R, Pair.of(0, 4))
          .build();

      Map<Axis, ResourceLocation> coreTemplates = new IdentityHashMap<>();
      Map<Pair<String, Axis>, ModelFile> coreModels = new HashMap<>();

      for (Axis axis : Iterate.axes)
        coreTemplates.put(axis, p.modLoc(path + "/core_" + axis.getSerializedName()));

      for (Axis axis : Iterate.axes) {
        ResourceLocation parent = coreTemplates.get(axis);
        for (String s : orientations) {
          Pair<String, Axis> key = Pair.of(s, axis);
          String modelName = path + "/" + s + "_" + axis.getSerializedName();
          coreModels.put(key, p.models()
              .withExistingParent(modelName, parent)
              .element()
              .from(4, 4, 4)
              .to(12, 12, 12)
              .face(Direction.get(AxisDirection.POSITIVE, axis))
              .end()
              .face(Direction.get(AxisDirection.NEGATIVE, axis))
              .end()
              .faces((d, builder) -> {
                Pair<Integer, Integer> pair = uvs.get(s);
                float u = pair.getKey();
                float v = pair.getValue();
                if (d == Direction.UP)
                  builder.uvs(u + 4, v + 4, u, v);
                if (d == Direction.DOWN)
                  builder.uvs(u + 4, v, u, v + 4);
                if (d == Direction.NORTH)
                  builder.uvs(u, v, u + 4, v + 4);
                if (d == Direction.SOUTH)
                  builder.uvs(u + 4, v, u, v + 4);
                if (d == Direction.EAST)
                  builder.uvs(u, v, u + 4, v + 4);
                if (d == Direction.WEST)
                  builder.uvs(u + 4, v, u, v + 4);
                builder.texture("#0");
              })
              .end());
        }
      }

      MultiPartBlockStateBuilder builder = p.getMultipartBuilder(c.get());
      for (Axis axis : Iterate.axes) {
        putPart(coreModels, builder, axis, LU, true, false, true, false);
        putPart(coreModels, builder, axis, RU, true, false, false, true);
        putPart(coreModels, builder, axis, LD, false, true, true, false);
        putPart(coreModels, builder, axis, RD, false, true, false, true);
        putPart(coreModels, builder, axis, UD, true, true, false, false);
        putPart(coreModels, builder, axis, U, true, false, false, false);
        putPart(coreModels, builder, axis, D, false, true, false, false);
        putPart(coreModels, builder, axis, LR, false, false, true, true);
        putPart(coreModels, builder, axis, L, false, false, true, false);
        putPart(coreModels, builder, axis, R, false, false, false, true);
      }
    };
  }

  private static void putPart(Map<Pair<String, Axis>, ModelFile> coreModels, MultiPartBlockStateBuilder builder,
                              Axis axis, String s, boolean up, boolean down, boolean left, boolean right) {
    Direction positiveAxis = Direction.get(AxisDirection.POSITIVE, axis);
    Map<Direction, BooleanProperty> propertyMap = FluidPipeBlock.PROPERTY_BY_DIRECTION;

    Direction upD = Pointing.UP.getCombinedDirection(positiveAxis);
    Direction leftD = Pointing.LEFT.getCombinedDirection(positiveAxis);
    Direction rightD = Pointing.RIGHT.getCombinedDirection(positiveAxis);
    Direction downD = Pointing.DOWN.getCombinedDirection(positiveAxis);

    if (axis == Axis.Y || axis == Axis.X) {
      leftD = leftD.getOpposite();
      rightD = rightD.getOpposite();
    }

    builder.part()
        .modelFile(coreModels.get(Pair.of(s, axis)))
        .addModel()
        .condition(propertyMap.get(upD), up)
        .condition(propertyMap.get(leftD), left)
        .condition(propertyMap.get(rightD), right)
        .condition(propertyMap.get(downD), down)
        .end();
  }
}
