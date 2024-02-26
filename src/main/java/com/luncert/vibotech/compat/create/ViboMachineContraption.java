package com.luncert.vibotech.compat.create;

import static com.luncert.vibotech.index.AllContraptionTypes.VIBO_MACHINE_CONTRAPTION;

import com.luncert.vibotech.common.TreeNode;
import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.annotation.TickOrder;
import com.luncert.vibotech.compat.vibotech.component.FinalizeComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentTickContext;
import com.luncert.vibotech.compat.vibotech.component.EnergyAccessorComponent;
import com.luncert.vibotech.compat.vibotech.component.FluidAccessorComponent;
import com.luncert.vibotech.compat.vibotech.IViboComponent;
import com.luncert.vibotech.compat.vibotech.component.StorageAccessorComponent;
import com.luncert.vibotech.compat.vibotech.annotation.TickAfter;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.luncert.vibotech.compat.vibotech.ViboContraptionAccessor;
import com.luncert.vibotech.content.camera.CameraBlock;
import com.luncert.vibotech.content.camera.CameraEntity;
import com.luncert.vibotech.content.controlseat.ControlSeatBlock;
import com.luncert.vibotech.content.controlseat.ControlSeatEntity;
import com.luncert.vibotech.content.vibomachinecore.ViboMachineCoreComponent;
import com.luncert.vibotech.content.vibomachinecore.ViboMachineEntity;
import com.luncert.vibotech.index.AllBlocks;
import com.luncert.vibotech.index.AllCapabilities;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.BlockMovementChecks;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionType;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import com.simibubi.create.content.contraptions.render.ContraptionLighter;
import com.simibubi.create.content.contraptions.render.NonStationaryLighter;
import com.simibubi.create.foundation.utility.NBTHelper;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

// FIXME: Caused by: java.lang.RuntimeException: com.luncert.vibotech.exception.ViboMachineAssemblyException: Unmovable Block (Piston) at [-10,-59,3]
public class ViboMachineContraption extends Contraption {

  private static final Logger LOGGER = LogUtils.getLogger();

  private static final Field fieldInitialPassengers;

  static {
    try {
      fieldInitialPassengers = Contraption.class.getDeclaredField("initialPassengers");
      fieldInitialPassengers.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  private ViboMachineEntity viboMachine;
  // component type to vibo component
  private final Map<ViboComponentType, List<IViboComponent>> components = new HashMap<>();
  // component name to block info
  private final Map<String, StructureBlockInfo> componentBlockInfoMap = new HashMap<>();
  // tick order of component types
  private List<TreeNode<ViboComponentType>> componentTickOrders;
  private ViboContraptionAccessor accessor;
  public EContraptionMovementMode rotationMode;

  protected List<BlockPos> cameras = new ArrayList();
  private final ViboComponentTickContext context = new ViboComponentTickContext();

  public ViboMachineContraption() {
    this(EContraptionMovementMode.ROTATE, null);
  }

  public ViboMachineContraption(EContraptionMovementMode mode, ViboMachineEntity viboMachine) {
    this.rotationMode = mode;
    this.viboMachine = viboMachine;
  }

  // api

  public Map<ViboComponentType, List<IViboComponent>> getComponents() {
    return components;
  }

  public void tickComponents() {
    for (TreeNode<ViboComponentType> node : componentTickOrders) {
      tickComponents(node);
    }
    context.reset();
  }

  private void tickComponents(TreeNode<ViboComponentType> node) {
    components.get(node.getData()).forEach(component -> component.tickComponent(context));
    node.getChildren().forEach(this::tickComponents);
  }

  public StructureBlockInfo getComponentBlockInfo(String name) {
    return componentBlockInfoMap.get(name);
  }

  public BlockPos getWorldPos(BlockPos pos) {
    return viboMachine.blockPosition().offset(pos.getX(), pos.getY(), pos.getZ());
  }

  // impl

  @Override
  public ContraptionType getType() {
    return VIBO_MACHINE_CONTRAPTION;
  }

  @Override
  public boolean assemble(Level level, BlockPos pos) throws AssemblyException {
    if (!searchMovedStructure(level, pos, null))
      return false;

    addBlock(pos, Pair.of(new StructureBlockInfo(
        pos, AllBlocks.VIBO_MACHINE_CORE.getDefaultState(), null), null));

    initComponents(level, viboMachine);
    return true;
  }

  public void initComponents(Level level, ViboMachineEntity viboMachineEntity) {
    if (!level.isClientSide && accessor == null) {
      this.viboMachine = viboMachineEntity;
      components.put(ViboComponentType.CORE, List.of(new ViboMachineCoreComponent()));
      components.put(ViboComponentType.ENERGY_ACCESSOR, List.of(new EnergyAccessorComponent()));
      components.put(ViboComponentType.STORAGE_ACCESSOR, List.of(new StorageAccessorComponent()));
      components.put(ViboComponentType.FLUID_ACCESSOR, List.of(new FluidAccessorComponent()));
      components.put(ViboComponentType.FINALIZE, List.of(new FinalizeComponent()));

      accessor = new ViboContraptionAccessor((ServerLevel) level, viboMachineEntity, this);

      Map<ViboComponentType, TreeNode<ViboComponentType>> roots = new HashMap<>();
      Map<ViboComponentType, TreeNode<ViboComponentType>> nodes = new HashMap<>();
      for (Map.Entry<ViboComponentType, List<IViboComponent>> entry : components.entrySet()) {
        ViboComponentType key = entry.getKey();
        List<IViboComponent> components = entry.getValue();

        // call init
        for (int i = 0; i < components.size(); i++) {
          IViboComponent c = components.get(i);
          String name = c.getComponentType().isSingleton()
              ? c.getComponentType().getName()
              : c.getComponentType().getName() + "-" + i;
          c.init(accessor, name);
        }

        // resolve tick orders
        TreeNode<ViboComponentType> current = nodes.computeIfAbsent(key, TreeNode::new);
        Class<? extends IViboComponent> type = key.getType();
        if (type.isAnnotationPresent(TickAfter.class)) {
          ViboComponentType target = ViboComponentType.valueOf(type.getAnnotation(TickAfter.class).value());
          TreeNode<ViboComponentType> parent = nodes.computeIfAbsent(target, TreeNode::new);
          parent.addChild(current);
        } else {
          roots.put(key, current);
        }
      }

      componentTickOrders = roots.entrySet().stream()
          .sorted(Comparator.comparing(a -> {
            TickOrder tickOrder = a.getKey().getType().getAnnotation(TickOrder.class);
            return tickOrder == null ? Integer.MAX_VALUE : tickOrder.value();
          }))
          .map(Map.Entry::getValue)
          .collect(Collectors.toList());

      LOGGER.info("components {}", components);
      LOGGER.info("components order {}", componentTickOrders);
    }
  }

  @Override
  protected boolean addToInitialFrontier(Level world, BlockPos pos, Direction direction, Queue<BlockPos> frontier) {
    frontier.clear();
    frontier.add(pos.above());
    return true;
  }

  // @Override
  // protected Pair<StructureBlockInfo, BlockEntity> capture(Level world, BlockPos pos) {
  //   Pair<StructureBlockInfo, BlockEntity> pair = super.capture(world, pos);
  //   StructureBlockInfo capture = pair.getKey();
  //   if (!AllBlocks.ASSEMBLE_STATION.has(capture.state())) {
  //     return pair;
  //   }
  //
  //   // replace assemble station with anchor block
  //   return Pair.of(
  //       new StructureBlockInfo(pos, AssembleStationBlock.createAnchor(capture.state()), null),
  //       pair.getValue());
  // }

  @Override
  protected void addBlock(BlockPos pos, Pair<StructureBlockInfo, BlockEntity> pair) {
    super.addBlock(pos, pair);

    if (pair.getValue() == null) {
      return;
    }

    // records component blocks
    BlockPos localPos = pos.subtract(anchor);

    pair.getValue().getCapability(AllCapabilities.CAPABILITY_VIBO_COMPONENT).ifPresent(c ->
        components.compute(c.getComponentType(), (k, v) -> {
          if (v == null) {
            v = new LinkedList<>();
          }

          componentBlockInfoMap.put(c.getComponentType().getName() + "-" + v.size(), blocks.get(localPos));

          v.add(c);
          return v;
        }));
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
  protected boolean moveBlock(Level world, @Nullable Direction forcedDirection, Queue<BlockPos> frontier, Set<BlockPos> visited) throws AssemblyException {
    BlockPos pos = frontier.peek();
    boolean ok = super.moveBlock(world, forcedDirection, frontier, visited);
    if (!ok) {
      return false;
    }

    BlockState state = world.getBlockState(pos);
    if (!world.isOutsideBuildHeight(pos)
        && world.isLoaded(pos)
        && !this.isAnchoringBlockAt(pos)
        && BlockMovementChecks.isMovementNecessary(state, world, pos)
        && this.movementAllowed(state, world, pos)
    ) {
      if (state.getBlock() instanceof ControlSeatBlock) {
        moveControlSeat(world, pos);
      }
      if (state.getBlock() instanceof CameraBlock) {
        moveCamera(world, pos);
      }
    }

    return true;
  }

  private void moveControlSeat(Level world, BlockPos pos) {
    BlockPos local = this.toLocalPos(pos);
    this.getSeats().add(local);
    List<ControlSeatEntity> seatsEntities = world.getEntitiesOfClass(ControlSeatEntity.class, new AABB(pos));
    if (!seatsEntities.isEmpty()) {
      SeatEntity seat = seatsEntities.get(0);
      List<Entity> passengers = seat.getPassengers();
      if (!passengers.isEmpty()) {
        // see onEntityInitialize -> addSittingPassenger
        getInitialPassengers().put(local, passengers.get(0));
      }
    }
  }

  private void moveCamera(Level world, BlockPos pos) {
    BlockPos local = this.toLocalPos(pos);
    this.cameras.add(local);
    List<CameraEntity> entities = world.getEntitiesOfClass(CameraEntity.class, new AABB(pos));
    if (!entities.isEmpty()) {
      CameraEntity camera = entities.get(0);
      // let camera entity rides contraption entity
      getInitialPassengers().put(local, camera);
    }
  }

  @SuppressWarnings("unchecked")
  private Map<BlockPos, Entity> getInitialPassengers() {
    try {
      return (Map<BlockPos, Entity>) fieldInitialPassengers.get(this);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void addPassengersToWorld(Level world, StructureTransform transform, List<Entity> passengers) {
    if (getSeatMapping().isEmpty()) {
      return;
    }
    for (Entity passenger : passengers) {
      Integer seatIndex = getSeatMapping().get(passenger.getUUID());
      if (seatIndex == null)
        continue;

      if (passenger instanceof CameraEntity) {
        BlockPos seatPos = cameras.get(seatIndex);
        seatPos = transform.apply(seatPos);
        // set camera entity pos
        passenger.setPos(seatPos.getX() + 0.5, seatPos.getY() + 0.5, seatPos.getZ() + 0.5);
        // if (!(world.getBlockState(seatPos).getBlock() instanceof CameraBlock)) {
        //   continue;
        // }
      } else {
        BlockPos seatPos = getSeats().get(seatIndex);
        seatPos = transform.apply(seatPos);

        if (passenger instanceof ControlSeatEntity) {
          if (!(world.getBlockState(seatPos).getBlock() instanceof ControlSeatBlock)
              || ControlSeatBlock.isSeatOccupied(world, seatPos)) {
            continue;
          }
          ControlSeatBlock.sitDown(world, seatPos, passenger);
        } else if (passenger instanceof SeatEntity) {
          if (!(world.getBlockState(seatPos).getBlock() instanceof SeatBlock)
              || SeatBlock.isSeatOccupied(world, seatPos)) {
            continue;
          }
          SeatBlock.sitDown(world, seatPos, passenger);
        }
      }
    }
  }

  public List<BlockPos> getCameras() {
    return cameras;
  }

  @Override
  public CompoundTag writeNBT(boolean spawnPacket) {
    CompoundTag tag = super.writeNBT(spawnPacket);

    NBTHelper.writeEnum(tag, "RotationMode", rotationMode);

    // write context
    tag.put("context", context.writeNBT());

    // write components
    ListTag componentList = new ListTag();
    for (Map.Entry<ViboComponentType, List<IViboComponent>> entry : components.entrySet()) {
      List<IViboComponent> components = entry.getValue();
      for (int i = 0; i < components.size(); i++) {
        CompoundTag item = new CompoundTag();
        item.putString("name", entry.getKey().getName() + (entry.getKey().isSingleton() ? "" : "-" + i));

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

    tag.put("cameras", NBTHelper.writeCompoundList(cameras, NbtUtils::writeBlockPos));
    // System.out.println("write -" + tag);
    return tag;
  }

  @Override
  public void readNBT(Level world, CompoundTag nbt, boolean spawnData) {
    super.readNBT(world, nbt, spawnData);
    // System.out.println("read - " + nbt);

    rotationMode = NBTHelper.readEnum(nbt, "RotationMode", EContraptionMovementMode.class);

    // read context
    context.readNBT(nbt.getCompound("context"));

    // read components
    this.components.clear();
    ListTag componentList = nbt.getList("components", 10);
    for (Tag tag : componentList) {
      CompoundTag componentNbt = (CompoundTag) tag;
      Pair<String, Integer> name = BaseViboComponent.parseName(componentNbt.getString("name"));
      ViboComponentType componentType = ViboComponentType.valueOf(name.getKey());
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

    // read cameras
    cameras.clear();
    NBTHelper.iterateCompoundList(nbt.getList("cameras", 10), (c) -> {
      cameras.add(NbtUtils.readBlockPos(c));
    });
  }
}
