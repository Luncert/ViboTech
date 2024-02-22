package com.luncert.vibotech.content.photovoltaic;

import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentTickContext;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;

public class PhotovoltaicPanelComponent extends BaseViboComponent {

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.PHOTOVOLTAIC_PANEL;
  }

  @Override
  public void tickComponent(ViboComponentTickContext context) {
    if (accessor.world.dimensionType().hasSkyLight()) {
      getEnergyAccessor().ifPresent(energyStorage -> {
        energyStorage.receiveEnergy(10, false);
      });
    }
  }
}
