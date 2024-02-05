package com.luncert.vibotech.content;

import com.luncert.vibotech.index.AllBlocks;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionType;
import java.util.Queue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

public class TransportMachineContraption extends Contraption {

  private static final Logger LOGGER = LogUtils.getLogger();

  public static final ContraptionType TRANSPORT_MACHINE = ContraptionType.register(
      "transport_machine", TransportMachineContraption::new);

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
  public boolean canBeStabilized(Direction direction, BlockPos blockPos) {
    return false;
  }

  @Override
  protected Pair<StructureBlockInfo, BlockEntity> capture(Level world, BlockPos pos) {
    Pair<StructureBlockInfo, BlockEntity> pair = super.capture(world, pos);
    StructureBlockInfo capture = pair.getKey();
    if (!AllBlocks.ASSEMBLE_STATION.has(capture.state())) {
      return pair;
    }

    // replace assemble station with anchor block
    return Pair.of(new StructureBlockInfo(pos, AssembleStationBlock.createAnchor(capture.state()), null), pair.getValue());
  }
}
