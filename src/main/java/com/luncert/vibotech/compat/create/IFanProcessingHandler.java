package com.luncert.vibotech.compat.create;

import net.minecraft.core.Direction;

public interface IFanProcessingHandler {

  void tick(Direction airCurrentDirection, Direction airFlowDirection);
}
