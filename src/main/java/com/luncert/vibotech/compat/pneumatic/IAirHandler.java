package com.luncert.vibotech.compat.pneumatic;

import it.unimi.dsi.fastutil.floats.FloatPredicate;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IAirHandler {

  float getDangerPressure();

  float getCriticalPressure();

  void setPressure(float newPressure);

  void enableSafetyVenting(FloatPredicate pressureCheck, Direction dir);

  void disableSafetyVenting();

  void tick(BlockEntity be);

  void setSideLeaking(@Nullable Direction dir);

  @Nullable
  Direction getSideLeaking();

  List<IAirHandler.Connection> getConnectedAirHandlers(BlockEntity be);

  void setConnectedFaces(List<Direction> sides);

  interface Connection {

    IAirHandler getAirHandler();

    @Nullable
    Direction getDirection();

    int getMaxDispersion();

    void setMaxDispersion(int maxDispersion);

    int getDispersedAir();

    void setAirToDisperse(int toDisperse);
  }
}
