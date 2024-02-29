package com.luncert.vibotech.content.portableaccumulator;

import com.mojang.logging.LogUtils;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyManager;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import org.slf4j.Logger;

public class PortableAccumulatorMovement implements MovementBehaviour {

  private static final Logger LOGGER = LogUtils.getLogger();

  public static final int TICK_DELAY = 20;

  public void tick(MovementContext context) {
    if (context.contraption.entity != null && !context.world.isClientSide) {
      if (context.temporaryData == null) {
        context.temporaryData = 0;
        PortableEnergyManager.track(context);
        return;
      }

      int tick = (int) context.temporaryData;
      if (tick >= TICK_DELAY) {
        context.temporaryData = 0;
        PortableEnergyManager.track(context);
      } else {
        context.temporaryData = ++tick;
      }
    }
  }

  public void startMoving(MovementContext context) {
    if (context.contraption.entity != null && !context.world.isClientSide) {
      if (context.blockEntityData.contains("EnergyContent")) {
        context.temporaryData = 0;
        PortableEnergyManager.track(context);
      }
    }
  }

  public void stopMoving(MovementContext context) {
    if (context.contraption.entity != null && !context.world.isClientSide) {
      PortableEnergyManager.untrack(context);
    }
  }

  public boolean renderAsNormalBlockEntity() {
    return true;
  }
}
