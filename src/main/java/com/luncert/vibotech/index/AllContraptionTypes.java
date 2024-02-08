package com.luncert.vibotech.index;

import com.luncert.vibotech.compat.create.TransportMachineContraption;
import com.simibubi.create.content.contraptions.ContraptionType;

public class AllContraptionTypes {

  public static final ContraptionType TRANSPORT_MACHINE_CONTRAPTION = ContraptionType.register(
      "transport_machine_contraption", TransportMachineContraption::new);

  public static void register() {
  }
}
