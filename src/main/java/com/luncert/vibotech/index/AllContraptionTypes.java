package com.luncert.vibotech.index;

import com.luncert.vibotech.compat.create.ViboMachineContraption;
import com.simibubi.create.content.contraptions.ContraptionType;

public class AllContraptionTypes {

  public static final ContraptionType VIBO_MACHINE_CONTRAPTION = ContraptionType.register(
      "vibo_machine_contraption", ViboMachineContraption::new);

  public static void register() {
  }
}
