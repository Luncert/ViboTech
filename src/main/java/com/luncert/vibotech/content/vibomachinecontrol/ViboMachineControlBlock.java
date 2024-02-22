package com.luncert.vibotech.content.vibomachinecontrol;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class ViboMachineControlBlock extends Block implements IWrenchable {

  public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

  public ViboMachineControlBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(POWERED);
    super.createBlockStateDefinition(builder);
  }
}
