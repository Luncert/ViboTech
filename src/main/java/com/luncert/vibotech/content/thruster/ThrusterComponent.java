package com.luncert.vibotech.content.thruster;

import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ComponentTickContext;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;

public class ThrusterComponent extends BaseViboComponent {

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.THRUSTER;
  }

  @Override
  public void tickComponent(ComponentTickContext context) {
    super.tickComponent(context);
  }
}
