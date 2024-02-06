package com.luncert.vibotech.exception;

import com.luncert.vibotech.common.Lang;

public class TransportMachineMovementException extends Exception {

  public TransportMachineMovementException(String langKey, Object... objects) {
    super(Lang.translateDirect("gui.movement.exception." + langKey, objects).getString());
  }
}
