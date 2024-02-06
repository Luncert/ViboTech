package com.luncert.vibotech.content;

import static com.luncert.vibotech.compat.vibotech.ViboActionEvent.EVENT_CONTRAPTION_MOVEMENT_DONE;

import com.google.common.collect.ImmutableMap;
import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.TickOrder;
import com.luncert.vibotech.compat.vibotech.ViboApiCallback;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.luncert.vibotech.exception.TransportMachineMovementException;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@TickOrder(2)
public class TransportMachineComponent extends BaseViboComponent {

  private static final int MAX_SPEED = 256;
  private int speed = 0;
  private int executionId;

  @Override
  public void tickComponent() {
    // TODO
    // double thrust = accessor.resources.getResource("thrust", 0d);
    // maxSpeed = (int) (Mth.clamp(thrust / accessor.contraption.getBlocks().size(), 0, 1) * 256);
    // if (speed > maxSpeed) {
    //     speed = maxSpeed;
    //     accessor.transportMachine.setKineticSpeed(speed);
    // }
  }

  @Override
  public Tag writeNBT() {
    return IntTag.valueOf(speed);
  }

  @Override
  public void readNBT(Level world, Tag tag) {
    speed = ((IntTag) tag).getAsInt();
  }

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.TRANSPORT_MACHINE;
  }

  // @LuaFunction
  // public final void turnOnGenerators() {
  //     for (IViboComponent component : accessor.findAll("fuel-engine")) {
  //         ((FuelEngineComponent) component).turnOn();
  //     }
  // }

  // @LuaFunction
  // public final void turnOffGenerators() {
  //     for (IViboComponent component : accessor.findAll("fuel-engine")) {
  //         ((FuelEngineComponent) component).turnOff();
  //     }
  // }

  @LuaFunction
  public final MethodResult up(int n) throws LuaException {
    if (n <= 0) {
      throw new LuaException("n must be positive");
    }

    int executionId = this.executionId++;
    try {
      accessor.transportMachine.up(n, data -> accessor.queueEvent(EVENT_CONTRAPTION_MOVEMENT_DONE, executionId, data));
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
      accessor.transportMachine.down(n, data -> accessor.queueEvent(EVENT_CONTRAPTION_MOVEMENT_DONE, executionId, data));
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
      accessor.transportMachine.forward(n, data -> accessor.queueEvent(EVENT_CONTRAPTION_MOVEMENT_DONE, executionId, data));
    } catch (TransportMachineMovementException e) {
      throw new LuaException(e.getMessage());
    }

    return ViboApiCallback.hook(executionId, EVENT_CONTRAPTION_MOVEMENT_DONE);
  }

  @LuaFunction
  public final MethodResult turnLeft() throws LuaException {
    int executionId = this.executionId++;
    try {
      accessor.transportMachine.turnLeft(data -> accessor.queueEvent(EVENT_CONTRAPTION_MOVEMENT_DONE, executionId, data));
    } catch (TransportMachineMovementException e) {
      throw new LuaException(e.getMessage());
    }

    return ViboApiCallback.hook(executionId, EVENT_CONTRAPTION_MOVEMENT_DONE);
  }

  @LuaFunction
  public final MethodResult turnRight() throws LuaException {
    int executionId = this.executionId++;
    try {
      accessor.transportMachine.turnRight(data -> accessor.queueEvent(EVENT_CONTRAPTION_MOVEMENT_DONE, executionId, data));
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
    accessor.transportMachine.setKineticSpeed(speed);
  }

  @LuaFunction
  public final int getSpeed() {
    return speed;
  }

  @LuaFunction
  public final Map<String, Double> getContraptionPosition() {
    Vec3 pos = accessor.transportMachine.getContraptionPosition();
    return ImmutableMap.of(
        "x", pos.x,
        "y", pos.y,
        "z", pos.z
    );
  }

  @LuaFunction
  public final Map<String, Double> getStationPosition() {
    Vec3 pos = accessor.station.getStationPosition();
    return ImmutableMap.of(
        "x", pos.x,
        "y", pos.y,
        "z", pos.z
    );
  }

  @LuaFunction
  public final Map<String, Object> getContraptionFacing() {
    Direction direction = accessor.transportMachine.getContraptionFacing();
    Direction.AxisDirection axisDirection = direction.getAxisDirection();
    return ImmutableMap.of(
        "axis", direction.getAxis().getName(),
        "step", axisDirection.getStep()
    );
  }

  @LuaFunction
  public final Map<String, Object> getStationFacing() {
    Direction direction = accessor.station.getStationFacing();
    Direction.AxisDirection axisDirection = direction.getAxisDirection();
    return ImmutableMap.of(
        "axis", direction.getAxis().getName(),
        "step", axisDirection.getStep()
    );
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

    Direction facingDirection = accessor.transportMachine.getContraptionFacing();
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
