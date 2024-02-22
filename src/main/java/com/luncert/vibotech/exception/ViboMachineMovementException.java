package com.luncert.vibotech.exception;

import com.luncert.vibotech.common.Lang;

public class ViboMachineMovementException extends Exception {

  public ViboMachineMovementException(String langKey, Object... objects) {
    super(Lang.translateDirect("gui.movement.exception." + langKey, objects).getString());
  }
}
