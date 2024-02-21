package com.luncert.vibotech.content.thruster;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ThrusterBlock extends Block implements IWrenchable, IBE<ThrusterBlockEntity> {

  public ThrusterBlock(Properties properties) {
    super(properties);
  }

  @Override
  public Class<ThrusterBlockEntity> getBlockEntityClass() {
    return ThrusterBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends ThrusterBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.THRUSTER.get();
  }
}
