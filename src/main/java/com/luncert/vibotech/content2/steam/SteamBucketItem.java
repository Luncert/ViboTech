package com.luncert.vibotech.content2.steam;

import com.luncert.vibotech.index.AllFluids;
import net.minecraft.world.item.BucketItem;

public class SteamBucketItem extends BucketItem {

  public SteamBucketItem(Properties properties) {
    super(AllFluids.STEAM, properties);
  }
}
