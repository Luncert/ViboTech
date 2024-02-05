package com.luncert.vibotech.content;

import com.luncert.vibotech.index.AllBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * AssembleStationBlockEntity assemble in tick() if block is powered.
 * - AssembleStationBlockEntity#assemble: create TransportMachineEntity and call assemble
 * - TransportMachineEntity#assemble: create contraption, call assemble and start riding
 */
public class AssembleStationBlock extends Block {

  public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

  public AssembleStationBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(POWERED);
    super.createBlockStateDefinition(builder);
  }

  public enum AssembleStationAction {
    ASSEMBLE, DISASSEMBLE;

    public boolean shouldAssemble() {
      return this == ASSEMBLE;
    }

    public boolean shouldDisassemble() {
      return this == DISASSEMBLE;
    }
  }

  public static AssembleStationAction getAction(BlockState state) {
    boolean powered = state.getValue(POWERED);
    return powered ? AssembleStationAction.ASSEMBLE : AssembleStationAction.DISASSEMBLE;
  }

  public static BlockState createAnchor(BlockState state) {
    return AllBlocks.TRANSPORT_MACHINE_ANCHOR.getDefaultState();
  }
}
