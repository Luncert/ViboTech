package com.luncert.vibotech.compat.vibotech.component;

import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentTickContext;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.luncert.vibotech.compat.vibotech.annotation.TickAfter;
import com.luncert.vibotech.content.thruster.ThrustResource;
import com.luncert.vibotech.content.transportmachinecore.PlasmaCurrent;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@TickAfter("thruster")
public class FinalizeComponent extends BaseViboComponent {

  private final PlasmaCurrentSource plasmaCurrentSource = new PlasmaCurrentSource();

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.FINALIZE;
  }

  @Override
  public void tickComponent(ViboComponentTickContext context) {
    if (context.isPowerOn()) {
      boolean gotEnoughPower = context.<ThrustResource>getResource(ThrustResource.ID)
          .map(r -> r.getPower() >= accessor.contraption.getBlocks().size())
          .orElse(false);
      accessor.viboMachineEntity.setPower(gotEnoughPower);

      // TODO: create particle only if power on
      //if (accessor.world.isClientSide) {
      //  plasmaCurrentSource.plasmaCurrent.tick();
      //}
    } else {
      accessor.viboMachineEntity.setPower(false);
    }
  }

  private class PlasmaCurrentSource implements IAirCurrentSource {

    private final PlasmaCurrent plasmaCurrent;

    public PlasmaCurrentSource() {
      plasmaCurrent = new PlasmaCurrent(this);
    }

    @Nullable
    @Override
    public AirCurrent getAirCurrent() {
      return plasmaCurrent;
    }

    @Nullable
    @Override
    public Level getAirCurrentWorld() {
      return accessor.world;
    }

    @Override
    public BlockPos getAirCurrentPos() {
      return accessor.viboMachineEntity.blockPosition();
    }

    @Override
    public float getSpeed() {
      return 1;
    }

    @Override
    public Direction getAirflowOriginSide() {
      return Direction.DOWN;
    }

    @Nullable
    @Override
    public Direction getAirFlowDirection() {
      return Direction.DOWN;
    }

    // TODO
    @Override
    public boolean isSourceRemoved() {
      return false;
    }

    @Override
    public float getMaxDistance() {
      return 30;
    }
  }
}
