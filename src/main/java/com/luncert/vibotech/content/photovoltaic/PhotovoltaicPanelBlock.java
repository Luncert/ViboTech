package com.luncert.vibotech.content.photovoltaic;

import com.google.common.collect.ImmutableMap;
import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.luncert.vibotech.index.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PhotovoltaicPanelBlock extends Block implements IWrenchable, IBE<PhotovoltaicPanelBlockEntity> {

  public static final VoxelShape SHAPE = AllShapes
      .shape(0, 0, 0, 16, 3, 16)
      .build();

  public PhotovoltaicPanelBlock(Properties properties) {
    super(properties);
  }

  @Override
  public Class<PhotovoltaicPanelBlockEntity> getBlockEntityClass() {
    return PhotovoltaicPanelBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends PhotovoltaicPanelBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.PHOTOVOLTAIC_PANEL.get();
  }

  @Override
  public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    return SHAPE;
  }
}
