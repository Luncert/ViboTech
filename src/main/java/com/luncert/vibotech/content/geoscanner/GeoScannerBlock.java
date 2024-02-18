package com.luncert.vibotech.content.geoscanner;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class GeoScannerBlock extends Block implements IBE<GeoScannerBlockEntity> {

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
}
