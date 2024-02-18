package com.luncert.vibotech.content.geoscanner;

import static net.minecraft.world.level.block.HopperBlock.FACING;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class GeoScannerBlock extends Block implements IBE<GeoScannerBlockEntity>, IWrenchable {

  public GeoScannerBlock(Properties properties) {
    super(properties);
  }

  @Override
  public Class<GeoScannerBlockEntity> getBlockEntityClass() {
    return GeoScannerBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends GeoScannerBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.GEO_SCANNER.get();
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
    super.createBlockStateDefinition(builder);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }
}
