package com.luncert.vibotech.content.transportmachinecore;

import static com.luncert.vibotech.compat.vibotech.ViboActionEvent.EVENT_CONTRAPTION_MOVEMENT_DONE;

import com.google.common.collect.ImmutableMap;
import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboApiCallback;
import com.luncert.vibotech.compat.vibotech.ViboComponentTickContext;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.luncert.vibotech.compat.vibotech.annotation.TickOrder;
import com.luncert.vibotech.exception.TransportMachineMovementException;
import com.mojang.logging.LogUtils;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

@TickOrder(0)
public class ViboMachineCoreComponent extends BaseViboComponent {

  private static final Logger LOGGER = LogUtils.getLogger();

  private static final int MAX_SPEED = 256;
  private boolean power = false;
  private int speed = 0;
  private int executionId;

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.CORE;
  }

  @Override
  public void tickComponent(ViboComponentTickContext context) {
    context.setPowerOn(power);
    context.setSpeed(speed);
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

}
