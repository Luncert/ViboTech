package com.luncert.vibotech.content2.transportmachinecore;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import org.slf4j.Logger;

public class TransportMachineCoreMovementBehaviour implements MovementBehaviour {

  private static final Logger LOGGER = LogUtils.getLogger();

  @Override
  public void tick(MovementContext context) {
    LOGGER.info("{}", context.blockEntityData);
  }
}
