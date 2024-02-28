package com.luncert.vibotech.content.thruster;

import com.luncert.vibotech.content.vibomachinecore.PlasmaCurrent;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class PlasmaCurrentSource implements IAirCurrentSource {

  private final Level world;
  private final Supplier<BlockPos> posProvider;
  private final PlasmaCurrent plasmaCurrent;

  public PlasmaCurrentSource(Level world, Supplier<BlockPos> posProvider) {
    this.world = world;
    this.posProvider = posProvider;
    plasmaCurrent = new PlasmaCurrent(this);
  }
  public void tick() {
    plasmaCurrent.tick();
  }

  @Nullable
  @Override
  public AirCurrent getAirCurrent() {
    return plasmaCurrent;
  }

  @Nullable
  @Override
  public Level getAirCurrentWorld() {
    return world;
  }

  @Override
  public BlockPos getAirCurrentPos() {
    return posProvider.get();
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
