package com.luncert.vibotech.content2.steam;

import com.luncert.vibotech.index.AllBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SteamTankItem extends BlockItem {

  public SteamTankItem(Properties properties) {
    super(AllBlocks.GAS_TANK.get(), properties);
  }

  @Override
  protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
    boolean ok = super.placeBlock(context, state);
    if (ok) {
      BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
    }
    return ok;
  }
}
