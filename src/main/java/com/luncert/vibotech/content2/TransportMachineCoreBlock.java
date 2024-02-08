package com.luncert.vibotech.content2;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class TransportMachineCoreBlock extends Block implements IBE<TransportMachineCoreBlockEntity> {

  public TransportMachineCoreBlock(Properties pProperties) {
    super(pProperties);
  }

  @Override
  public Class<TransportMachineCoreBlockEntity> getBlockEntityClass() {
    return TransportMachineCoreBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends TransportMachineCoreBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.TRANSPORT_MACHINE_CORE.get();
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
