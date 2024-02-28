package com.luncert.vibotech.compat.vibotech.component;

import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentTickContext;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.luncert.vibotech.compat.vibotech.annotation.TickAfter;
import com.luncert.vibotech.content.thruster.ThrustResource;

@TickAfter("thruster")
public class FinalizeComponent extends BaseViboComponent {

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.FINALIZE;
  }

  @Override
  public void tickComponent(ViboComponentTickContext context) {
    if (context.isPowerOn()) {
      boolean gotEnoughPower = context.<ThrustResource>getResource(ThrustResource.ID)
          .map(r -> r.getPower() >= accessor.contraption.getBlocks().size())
          .orElse(false);
      accessor.viboMachineEntity.setPower(gotEnoughPower);
    } else {
      accessor.viboMachineEntity.setPower(false);
    }
  }
}
