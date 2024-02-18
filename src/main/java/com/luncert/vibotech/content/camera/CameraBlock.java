package com.luncert.vibotech.content.camera;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class CameraBlock extends Block implements IBE<CameraBlockEntity>, IWrenchable {

  public CameraBlock(Properties properties) {
    super(properties);
  }

  @Override
  public Class<CameraBlockEntity> getBlockEntityClass() {
    return CameraBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends CameraBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.CAMERA.get();
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
