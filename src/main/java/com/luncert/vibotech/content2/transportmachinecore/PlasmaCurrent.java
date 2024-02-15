package com.luncert.vibotech.content2.transportmachinecore;

import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.AirFlowParticleData;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PlasmaCurrent extends AirCurrent {

  public PlasmaCurrent(IAirCurrentSource source) {
    super(source);
  }

  @Override
  public void tick() {
    if (this.direction == null) {
      this.rebuild();
    }

    Level world = this.source.getAirCurrentWorld();
    Direction facing = this.direction;
    if (world != null && world.isClientSide) {
      Vec3 pos = VecHelper.getCenterOf(this.source.getAirCurrentPos()).add(Vec3.atLowerCornerOf(facing.getNormal()).scale(0.5d));
      world.addParticle(new AirFlowParticleData(this.source.getAirCurrentPos()), pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
    }
  }
}
