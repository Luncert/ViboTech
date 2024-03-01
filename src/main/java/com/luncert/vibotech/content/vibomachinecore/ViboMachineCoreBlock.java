package com.luncert.vibotech.content.vibomachinecore;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class ViboMachineCoreBlock extends Block implements IBE<ViboMachineCoreBlockEntity>, IWrenchable {

  public ViboMachineCoreBlock(Properties pProperties) {
    super(pProperties);
  }

  @Override
  public Class<ViboMachineCoreBlockEntity> getBlockEntityClass() {
    return ViboMachineCoreBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends ViboMachineCoreBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.VIBO_MACHINE_CORE.get();
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(HORIZONTAL_FACING);
    super.createBlockStateDefinition(builder);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
  }
}
