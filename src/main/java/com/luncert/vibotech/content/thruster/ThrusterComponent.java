package com.luncert.vibotech.content.thruster;

import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentTickContext;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import net.minecraft.util.Mth;

public class ThrusterComponent extends BaseViboComponent {

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.THRUSTER;
  }

  @Override
  public void tickComponent(ViboComponentTickContext context) {
    if (context.isPowerOn()) {
      int affectedBlockAmount = Math.max(16, accessor.contraption.getBlocks().size());
      int cost = affectedBlockAmount * Mth.clamp(context.getSpeed() / 16, 1, 10);
      getEnergyAccessor().ifPresent(energyStorage -> {
        if (energyStorage.extractEnergy(cost, true) == cost) {
          energyStorage.extractEnergy(cost, false);
          context.<ThrustResource>updateResource(ThrustResource.ID, r -> {
            if (r == null) {
              r = new ThrustResource(affectedBlockAmount);
            } else {
              r.setPower(r.getPower() + affectedBlockAmount);
            }
            return r;
          });
        }
      });
    }
  }
}
