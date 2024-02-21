package com.luncert.vibotech.content.transportmachinecore;

import static com.luncert.vibotech.compat.vibotech.ViboActionEvent.EVENT_CONTRAPTION_MOVEMENT_DONE;

import com.google.common.collect.ImmutableMap;
import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ComponentTickContext;
import com.luncert.vibotech.compat.vibotech.ViboApiCallback;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.luncert.vibotech.exception.TransportMachineMovementException;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ViboMachineCoreComponent extends BaseViboComponent {

  private static final Logger LOGGER = LogUtils.getLogger();

  private static final int MAX_SPEED = 256;
  private boolean power = false;
  private int speed = 0;
  private int executionId;
  private final PlasmaCurrentSource plasmaCurrentSource = new PlasmaCurrentSource();

  @Override
  public void tickComponent(ComponentTickContext context) {
    if (power) {
      int cost = accessor.contraption.getBlocks().size() * Mth.clamp(speed / 16, 1, 10);
      getEnergyAccessor().ifPresent(energyStorage -> {
        if (energyStorage.extractEnergy(cost, true) == cost) {
          energyStorage.extractEnergy(cost, false);
          accessor.viboMachineEntity.setPower(true);
        } else {
          accessor.viboMachineEntity.setPower(false);
        }
      });

      // TODO: create particle only if power on
      if (accessor.world.isClientSide) {
        plasmaCurrentSource.plasmaCurrent.tick();
      }
    } else {
      accessor.viboMachineEntity.setPower(false);
    }
  }

  @Override
  public Tag writeNBT() {
    CompoundTag tag = new CompoundTag();
    tag.putBoolean("power", power);
    tag.putInt("speed", speed);
    return tag;
  }

  @Override
  public void readNBT(Level world, Tag tag) {
    CompoundTag compoundTag = (CompoundTag) tag;
    power = compoundTag.getBoolean("power");
    speed = compoundTag.getInt("speed");
  }

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.CORE;
  }

  @LuaFunction
  public final void power(boolean on) {
    power = on;
  }

  @LuaFunction
  public final boolean hasPower() {
    return accessor.viboMachineEntity.getPower();
  }

  @LuaFunction
  public final MethodResult up(int n) throws LuaException {
    if (n <= 0) {
      throw new LuaException("n must be positive");
    }

    int executionId = this.executionId++;
    try {
      accessor.viboMachineEntity.up(n, data -> accessor.queueEvent(EVENT_CONTRAPTION_MOVEMENT_DONE, executionId, data));
    } catch (TransportMachineMovementException e) {
      throw new LuaException(e.getMessage());
    }

    return ViboApiCallback.hook(executionId, EVENT_CONTRAPTION_MOVEMENT_DONE);
  }

  @LuaFunction
  public final MethodResult down(int n) throws LuaException {
    if (n <= 0) {
      throw new LuaException("n must be positive");
    }

    int executionId = this.executionId++;
    try {
      accessor.viboMachineEntity.down(n, data -> accessor.queueEvent(EVENT_CONTRAPTION_MOVEMENT_DONE, executionId, data));
    } catch (TransportMachineMovementException e) {
      throw new LuaException(e.getMessage());
    }

    return ViboApiCallback.hook(executionId, EVENT_CONTRAPTION_MOVEMENT_DONE);
  }

  @LuaFunction
  public final MethodResult forward(int n) throws LuaException {
    if (n <= 0) {
      throw new LuaException("n must be positive");
    }

    int executionId = this.executionId++;
    try {
      accessor.viboMachineEntity.forward(n, data -> accessor.queueEvent(EVENT_CONTRAPTION_MOVEMENT_DONE, executionId, data));
    } catch (TransportMachineMovementException e) {
      throw new LuaException(e.getMessage());
    }

    return ViboApiCallback.hook(executionId, EVENT_CONTRAPTION_MOVEMENT_DONE);
  }

  @LuaFunction
  public final MethodResult turnLeft() throws LuaException {
    int executionId = this.executionId++;
    try {
      accessor.viboMachineEntity.turnLeft(data -> accessor.queueEvent(EVENT_CONTRAPTION_MOVEMENT_DONE, executionId, data));
    } catch (TransportMachineMovementException e) {
      throw new LuaException(e.getMessage());
    }

    return ViboApiCallback.hook(executionId, EVENT_CONTRAPTION_MOVEMENT_DONE);
  }

  @LuaFunction
  public final MethodResult turnRight() throws LuaException {
    int executionId = this.executionId++;
    try {
      accessor.viboMachineEntity.turnRight(data -> accessor.queueEvent(EVENT_CONTRAPTION_MOVEMENT_DONE, executionId, data));
    } catch (TransportMachineMovementException e) {
      throw new LuaException(e.getMessage());
    }
    return ViboApiCallback.hook(executionId, EVENT_CONTRAPTION_MOVEMENT_DONE);
  }

  @LuaFunction
  public final void setSpeed(int speed) throws LuaException {
    if (speed > MAX_SPEED) {
      throw new LuaException("max speed is " + MAX_SPEED);
    }

    this.speed = speed;
    accessor.viboMachineEntity.setKineticSpeed(speed);
  }

  @LuaFunction
  public final int getSpeed() {
    return speed;
  }

  @LuaFunction
  public final Map<String, Double> getContraptionPosition() {
    Vec3 pos = accessor.viboMachineEntity.getContraptionPosition();
    return ImmutableMap.of(
        "x", pos.x,
        "y", pos.y,
        "z", pos.z
    );
  }

  @LuaFunction
  public final Map<String, Integer> getAssembleStationPosition() {
    return accessor.getAssembleStationPosition().<Map<String, Integer>>map(pos ->
        ImmutableMap.of(
            "x", pos.getX(),
            "y", pos.getY(),
            "z", pos.getZ()
        )).orElse(Collections.emptyMap());
  }

  @LuaFunction
  public final Map<String, Object> getContraptionFacing() {
    Direction direction = accessor.viboMachineEntity.getContraptionFacing();
    Direction.AxisDirection axisDirection = direction.getAxisDirection();
    return ImmutableMap.of(
        "axis", direction.getAxis().getName(),
        "step", axisDirection.getStep()
    );
  }

  @LuaFunction
  public final Map<String, Object> getAssembleStationFacing() {
    return accessor.getAssembleStationFacing().<Map<String, Object>>map(direction -> {
      Direction.AxisDirection axisDirection = direction.getAxisDirection();
      return ImmutableMap.of(
          "axis", direction.getAxis().getName(),
          "step", axisDirection.getStep()
      );
    }).orElse(Collections.emptyMap());
  }

  @LuaFunction
  public final int calcRotationTo(String a, int step) throws LuaException {
    Direction.Axis axis;
    try {
      axis = Direction.Axis.valueOf(a.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new LuaException("Invalid argument, must be one of " + Arrays.toString(Direction.Axis.values()));
    }

    if (Direction.Axis.Y.equals(axis)) {
      return 0;
    }

    return getRotateStep(step, axis);
  }

  private int getRotateStep(int step, Direction.Axis axis) {
    int rotateStep = 0;

    Direction facingDirection = accessor.viboMachineEntity.getContraptionFacing();
    Direction.AxisDirection axisDirection = facingDirection.getAxisDirection();
    if (!axis.equals(facingDirection.getAxis())) {
      rotateStep++;
      axisDirection = Direction.fromYRot(facingDirection.toYRot() + 90).getAxisDirection();
    }

    if (axisDirection.getStep() != step) {
      rotateStep += 2;
    }

    if (rotateStep > 2) {
      rotateStep -= 4;
    }
    return rotateStep;
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
