package com.luncert.vibotech.content.gastank;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class GasTankBlock extends Block implements IBE<GasTankBlockEntity>, IWrenchable {

  public GasTankBlock(Properties properties) {
    super(properties);
  }

  @Override
  public Class<GasTankBlockEntity> getBlockEntityClass() {
    return GasTankBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends GasTankBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.GAS_TANK.get();
  }
}
