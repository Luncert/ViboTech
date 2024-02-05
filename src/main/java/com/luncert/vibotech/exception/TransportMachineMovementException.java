package com.luncert.vibotech.exception;

import com.simibubi.create.foundation.utility.Lang;

public class TransportMachineMovementException extends Exception {

  public TransportMachineMovementException(String langKey, Object... objects) {
    super(Lang.translateDirect("gui.movement.exception." + langKey, objects).getString());
  }
}
