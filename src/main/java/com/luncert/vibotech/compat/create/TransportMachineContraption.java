package com.luncert.vibotech.compat.create;

import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.IViboComponent;
import com.luncert.vibotech.compat.vibotech.TickOrder;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.luncert.vibotech.compat.vibotech.ViboContraptionAccessor;
import com.luncert.vibotech.content.AssembleStationBlock;
import com.luncert.vibotech.content.AssembleStationBlockEntity;
import com.luncert.vibotech.content.TransportMachineComponent;
import com.luncert.vibotech.content.TransportMachineEntity;
import com.luncert.vibotech.index.AllBlocks;
import com.luncert.vibotech.index.AllCapabilities;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionType;
import com.simibubi.create.content.contraptions.render.ContraptionLighter;
import com.simibubi.create.content.contraptions.render.NonStationaryLighter;
import com.simibubi.create.foundation.utility.NBTHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

// FIXME: Caused by: java.lang.RuntimeException: com.luncert.vibotech.exception.TransportMachineAssemblyException: Unmovable Block (Piston) at [-10,-59,3]
public class TransportMachineContraption extends Contraption {

  private static final Logger LOGGER = LogUtils.getLogger();

  public static final ContraptionType TRANSPORT_MACHINE_CONTRAPTION = ContraptionType.register(
      "transport_machine_contraption", TransportMachineContraption::new);

  private TransportMachineEntity transportMachine;
  // block name to vibo component
  private final Map<String, List<IViboComponent>> components = new HashMap<>();
  // block name to block info
  private final Map<String, StructureBlockInfo> componentBlockInfoMap = new HashMap<>();
  private List<List<String>> componentTickOrders;
  private ViboContraptionAccessor accessor;
  public EContraptionMovementMode rotationMode;

  public TransportMachineContraption() {
    this(EContraptionMovementMode.ROTATE, null);
  }

  public TransportMachineContraption(EContraptionMovementMode mode, TransportMachineEntity transportMachine) {
    this.rotationMode = mode;
    this.transportMachine = transportMachine;
  }

  // api

  public Map<String, List<IViboComponent>> getComponents() {
    return components;
  }

  public List<List<IViboComponent>> getOrderedComponents() {
    return componentTickOrders.stream().map(componentTypes -> {
      List<IViboComponent> c = new ArrayList<>();
      for (String componentType : componentTypes) {
        c.addAll(components.get(componentType));
      }
      return c;
    }).collect(Collectors.toList());
  }

  public StructureBlockInfo getComponentBlockInfo(String name) {
    return componentBlockInfoMap.get(name);
  }

  public BlockPos getWorldPos(BlockPos pos) {
    return transportMachine.blockPosition().offset(pos.getX(), pos.getY(), pos.getZ());
  }

  // impl

  @Override
  public ContraptionType getType() {
    return TRANSPORT_MACHINE_CONTRAPTION;
  }

  @Override
  public boolean assemble(Level level, BlockPos pos) throws AssemblyException {
    if (!searchMovedStructure(level, pos, null))
      return false;

    addBlock(pos, Pair.of(new StructureBlockInfo(
        pos, AllBlocks.TRANSPORT_MACHINE_ANCHOR.getDefaultState(), null), null));

    if (blocks.size() != 1) {
      initComponents(level, transportMachine);
      return true;
    }
    return false;
  }

  public void initComponents(Level level, TransportMachineEntity transportMachine) {
    if (accessor == null) {
      this.transportMachine = transportMachine;
      components.put(ViboComponentType.TRANSPORT_MACHINE.getName(), List.of(new TransportMachineComponent()));

      AssembleStationBlockEntity station = (AssembleStationBlockEntity) level.getBlockEntity(transportMachine.getStationPosition());
      accessor = new ViboContraptionAccessor(level, station.getPeripheral(), station, transportMachine, this);

      Map<Integer, List<String>> tickOrders = new HashMap<>();

      // resolve tick orders

      for (Map.Entry<String, List<IViboComponent>> entry : components.entrySet()) {
        List<IViboComponent> components = entry.getValue();
        for (int i = 0; i < components.size(); i++) {
          IViboComponent c = components.get(i);
          String name = c.getComponentType().getName() + "-" + i;
          c.init(accessor, name);
        }

        Class<? extends IViboComponent> type = components.get(0).getClass();
        int order = 0;
        if (type.isAnnotationPresent(TickOrder.class)) {
          TickOrder tickOrder = type.getAnnotation(TickOrder.class);
          order = tickOrder.value();
        }

        tickOrders.compute(order, (k, v) -> {
          if (v == null) {
            v = new LinkedList<>();
          }
          v.add(entry.getKey());
          return v;
        });
      }

      componentTickOrders = tickOrders.entrySet().stream()
          .sorted(Map.Entry.comparingByKey())
          .map(Map.Entry::getValue)
          .collect(Collectors.toList());
      LOGGER.info("components {}", components);
      LOGGER.info("components order {}", componentTickOrders);
    }

    accessor.resources.clear();
  }

  @Override
  protected boolean addToInitialFrontier(Level world, BlockPos pos, Direction direction, Queue<BlockPos> frontier) {
    frontier.clear();
    frontier.add(pos.above());
    return true;
  }

  @Override
  protected Pair<StructureBlockInfo, BlockEntity> capture(Level world, BlockPos pos) {
    Pair<StructureBlockInfo, BlockEntity> pair = super.capture(world, pos);
    StructureBlockInfo capture = pair.getKey();
    if (!AllBlocks.ASSEMBLE_STATION.has(capture.state())) {
      return pair;
    }

    // replace assemble station with anchor block
    return Pair.of(
        new StructureBlockInfo(pos, AssembleStationBlock.createAnchor(capture.state()), null),
        pair.getValue());
  }

  @Override
  protected void addBlock(BlockPos pos, Pair<StructureBlockInfo, BlockEntity> pair) {
    super.addBlock(pos, pair);

    if (pair.getValue() == null) {
      return;
    }

    // records component blocks
    BlockPos localPos = pos.subtract(anchor);
    LazyOptional<IViboComponent> opt = pair.getValue().getCapability(AllCapabilities.CAPABILITY_VIBO_COMPONENT);
    opt.ifPresent(c ->
        components.compute(c.getComponentType().getName(), (k, v) -> {
          if (v == null) {
            v = new LinkedList<>();
          }

          componentBlockInfoMap.put(c.getComponentType().getName() + "-" + v.size(), blocks.get(localPos));

          v.add(c);
          return v;
        }));
  }

  @Override
  protected boolean customBlockPlacement(LevelAccessor world, BlockPos pos, BlockState state) {
    return AllBlocks.TRANSPORT_MACHINE_ANCHOR.has(state);
  }

  @Override
  protected boolean customBlockRemoval(LevelAccessor world, BlockPos pos, BlockState state) {
    return AllBlocks.TRANSPORT_MACHINE_ANCHOR.has(state);
  }

  @Override
  public boolean canBeStabilized(Direction direction, BlockPos blockPos) {
    return false;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public ContraptionLighter<?> makeLighter() {
    return new NonStationaryLighter<>(this);
  }

  @Override
  public CompoundTag writeNBT(boolean spawnPacket) {
    CompoundTag tag = super.writeNBT(spawnPacket);

    NBTHelper.writeEnum(tag, "RotationMode", rotationMode);

    // write components
    ListTag componentList = new ListTag();
    for (Map.Entry<String, List<IViboComponent>> entry : components.entrySet()) {
      List<IViboComponent> components = entry.getValue();
      for (int i = 0; i < components.size(); i++) {
        CompoundTag item = new CompoundTag();
        item.putString("name", entry.getKey() + "-" + i);

        IViboComponent component = components.get(i);
        Tag c = component.writeNBT();
        if (c != null) {
          item.put("component", c);
        }

        componentList.add(item);
      }
    }

    ListTag componentInfoList = new ListTag();
    for (Map.Entry<String, StructureBlockInfo> entry : componentBlockInfoMap.entrySet()) {
      CompoundTag item = new CompoundTag();
      item.putString("name", entry.getKey());
      item.putLong("pos", entry.getValue().pos().asLong());
      componentInfoList.add(item);
    }

    tag.put("components", componentList);
    tag.put("componentInfoMappings", componentInfoList);

    // System.out.println("write -" + tag);
    return tag;
  }

  @Override
  public void readNBT(Level world, CompoundTag nbt, boolean spawnData) {
    super.readNBT(world, nbt, spawnData);
    // System.out.println("read - " + nbt);

    rotationMode = NBTHelper.readEnum(nbt, "RotationMode", EContraptionMovementMode.class);

    // read components
    this.components.clear();
    ListTag componentList = nbt.getList("components", 10);
    for (Tag tag : componentList) {
      CompoundTag componentNbt = (CompoundTag) tag;
      Pair<String, Integer> name = BaseViboComponent.parseName(componentNbt.getString("name"));
      String componentType = name.getKey();
      int componentId = name.getValue();
      this.components.compute(componentType, (k, v) -> {
        if (v == null) {
          v = new ArrayList<>();
        }
        for (int n = v.size(); n <= componentId; n++) {
          v.add(null);
        }
        IViboComponent component = ViboComponentType.createComponent(componentType);
        component.readNBT(world, componentNbt.get("component"));

        v.set(componentId, component);
        return v;
      });
    }

    this.componentBlockInfoMap.clear();
    ListTag componentInfoList = nbt.getList("componentInfoMappings", CompoundTag.TAG_COMPOUND);
    for (Tag tag : componentInfoList) {
      CompoundTag componentNbt = (CompoundTag) tag;
      String name = componentNbt.getString("name");
      this.componentBlockInfoMap.put(name, blocks.get(BlockPos.of(componentNbt.getLong("pos"))));
    }
  }
}
