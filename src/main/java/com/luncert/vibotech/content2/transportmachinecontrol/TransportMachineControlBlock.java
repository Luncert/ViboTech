package com.luncert.vibotech.content2.transportmachinecontrol;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class TransportMachineControlBlock extends Block {

  public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

  public TransportMachineControlBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(POWERED);
    super.createBlockStateDefinition(builder);
  }
}
