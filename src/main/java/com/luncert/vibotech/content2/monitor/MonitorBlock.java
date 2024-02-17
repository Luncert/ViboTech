package com.luncert.vibotech.content2.monitor;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class MonitorBlock extends Block implements IBE<MonitorBlockEntity> {

  public MonitorBlock(Properties properties) {
    super(properties);
  }

  @Override
  public Class<MonitorBlockEntity> getBlockEntityClass() {
    return MonitorBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends MonitorBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.MONITOR.get();
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
    super.createBlockStateDefinition(builder);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
  }
}
