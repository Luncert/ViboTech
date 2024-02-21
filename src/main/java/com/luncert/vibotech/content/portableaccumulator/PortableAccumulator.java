package com.luncert.vibotech.content.portableaccumulator;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class PortableAccumulator extends Block implements IBE<PortableAccumulatorBlockEntity>, IWrenchable {

  public PortableAccumulator(Properties properties) {
    super(properties);
  }

  @Override
  public Class<PortableAccumulatorBlockEntity> getBlockEntityClass() {
    return PortableAccumulatorBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends PortableAccumulatorBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.PORTABLE_ACCUMULATOR.get();
  }
}
