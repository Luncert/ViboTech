package com.luncert.vibotech.content2.steam;

import java.util.function.Supplier;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;

public class SteamTankItem extends BucketItem {

  public SteamTankItem(Supplier<? extends Fluid> supplier, Properties builder) {
    super(supplier, builder);
  }
}
