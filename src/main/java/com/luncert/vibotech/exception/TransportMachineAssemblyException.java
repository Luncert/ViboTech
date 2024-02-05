package com.luncert.vibotech.exception;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.foundation.utility.Lang;

public class TransportMachineAssemblyException extends AssemblyException {

  public TransportMachineAssemblyException(AssemblyException e) {
    super(e.component);
  }

  public TransportMachineAssemblyException(String langKey, Object... objects) {
    super(Lang.translateDirect("gui.assembly.exception." + langKey, objects));
  }

  @Override
  public String getMessage() {
    return component.getString();
  }
}
