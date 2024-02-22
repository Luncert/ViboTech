package com.luncert.vibotech.content.photovoltaic;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class PhotovoltaicPanelBlock extends Block implements IWrenchable, IBE<PhotovoltaicPanelBlockEntity> {

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
}
