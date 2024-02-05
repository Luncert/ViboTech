package com.luncert.vibotech.compat.create;

import com.luncert.vibotech.compat.vibotech.IViboComponent;
import com.luncert.vibotech.content.AssembleStationBlock;
import com.luncert.vibotech.content.TransportMachineEntity;
import com.luncert.vibotech.index.AllBlocks;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionType;
import com.simibubi.create.content.contraptions.render.ContraptionLighter;
import com.simibubi.create.content.contraptions.render.NonStationaryLighter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

// FIXME: Caused by: java.lang.RuntimeException: com.luncert.vibotech.exception.TransportMachineAssemblyException: Unmovable Block (Piston) at [-10,-59,3]
public class TransportMachineContraption extends Contraption {

  private static final Logger LOGGER = LogUtils.getLogger();

  public static final ContraptionType TRANSPORT_MACHINE = ContraptionType.register(
      "transport_machine", TransportMachineContraption::new);

  // TODO
  private final Map<String, List<IViboComponent>> components = new HashMap<>();
  private final Map<String, StructureBlockInfo> componentBlockInfoMap = new HashMap<>();

  private TransportMachineEntity transportMachine;

  public EContraptionMovementMode rotationMode;

  public TransportMachineContraption() {
    this(EContraptionMovementMode.ROTATE, null);
  }

  public TransportMachineContraption(EContraptionMovementMode mode, TransportMachineEntity transportMachine) {
    this.rotationMode = mode;
    this.transportMachine = transportMachine;
  }

  public Map<String, List<IViboComponent>> getComponents() {
    return components;
  }

  public StructureBlockInfo getComponentBlockInfo(String name) {
    return componentBlockInfoMap.get(name);
  }

  public BlockPos getWorldPos(BlockPos pos) {
    return transportMachine.blockPosition().offset(pos.getX(), pos.getY(), pos.getZ());
  }

  @Override
  public ContraptionType getType() {
    return TRANSPORT_MACHINE;
  }

  @Override
  public boolean assemble(Level level, BlockPos pos) throws AssemblyException {
    if (!searchMovedStructure(level, pos, null))
      return false;

    addBlock(pos, Pair.of(new StructureBlockInfo(
        pos, AllBlocks.TRANSPORT_MACHINE_ANCHOR.getDefaultState(), null), null));

    if (blocks.size() != 1) {
      // initComponents(world);
      return true;
    }
    return false;
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
}
