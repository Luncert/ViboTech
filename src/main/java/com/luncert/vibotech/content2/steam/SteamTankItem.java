package com.luncert.vibotech.content2.steam;

import com.luncert.vibotech.index.AllBlocks;
import net.minecraft.world.item.BlockItem;

public class SteamTankItem extends BlockItem {

  public SteamTankItem(Properties properties) {
    super(AllBlocks.GAS_TANK.get(), properties);
  }
}
