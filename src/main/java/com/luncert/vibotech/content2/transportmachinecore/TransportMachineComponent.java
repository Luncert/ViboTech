package com.luncert.vibotech.content2.transportmachinecore;

import static com.luncert.vibotech.compat.vibotech.ViboActionEvent.EVENT_CONTRAPTION_MOVEMENT_DONE;

import com.google.common.collect.ImmutableMap;
import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.TickOrder;
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
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@TickOrder(2)
public class TransportMachineComponent extends BaseViboComponent {

  private static final Logger LOGGER = LogUtils.getLogger();

  private static final int MAX_SPEED = 256;
  // TODO
  private boolean power = false;
  private int speed = 0;
  private int executionId;
  private final PlasmaCurrentSource plasmaCurrentSource = new PlasmaCurrentSource();

  @Override
  public void tickComponent() {
    int cost = accessor.contraption.getBlocks().size();
    getEnergyStorage().ifPresent(energyStorage -> {
      if (energyStorage.extractEnergy(cost, true) == cost) {
        energyStorage.extractEnergy(cost, false);
      }
    });

    if (accessor.world.isClientSide) {
      plasmaCurrentSource.plasmaCurrent.tick();
    }
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

  @LuaFunction
  public final void powerOn() {
    power = true;
  }

  @LuaFunction
  public final void powerOff() {
    power = false;
  }

  @LuaFunction
  public final MethodResult up(int n) throws LuaException {
    if (n <= 0) {
      throw new LuaException("n must be positive");
    }

    int executionId = this.executionId++;
    try {
      accessor.transportMachineCoreEntity.up(n, data -> accessor.queueEvent(EVENT_CONTRAPTION_MOVEMENT_DONE, executionId, data));
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
      accessor.transportMachineCoreEntity.down(n, data -> accessor.queueEvent(EVENT_CONTRAPTION_MOVEMENT_DONE, executionId, data));
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
      accessor.transportMachineCoreEntity.forward(n, data -> accessor.queueEvent(EVENT_CONTRAPTION_MOVEMENT_DONE, executionId, data));
    } catch (TransportMachineMovementException e) {
      throw new LuaException(e.getMessage());
    }

    return ViboApiCallback.hook(executionId, EVENT_CONTRAPTION_MOVEMENT_DONE);
  }

  @LuaFunction
  public final MethodResult turnLeft() throws LuaException {
    int executionId = this.executionId++;
    try {
      accessor.transportMachineCoreEntity.turnLeft(data -> accessor.queueEvent(EVENT_CONTRAPTION_MOVEMENT_DONE, executionId, data));
    } catch (TransportMachineMovementException e) {
      throw new LuaException(e.getMessage());
    }

    return ViboApiCallback.hook(executionId, EVENT_CONTRAPTION_MOVEMENT_DONE);
  }

  @LuaFunction
  public final MethodResult turnRight() throws LuaException {
    int executionId = this.executionId++;
    try {
      accessor.transportMachineCoreEntity.turnRight(data -> accessor.queueEvent(EVENT_CONTRAPTION_MOVEMENT_DONE, executionId, data));
    } catch (TransportMachineMovementException e) {
      throw new LuaException(e.getMessage());
    }
    return ViboApiCallback.hook(executionId, EVENT_CONTRAPTION_MOVEMENT_DONE);
  }

  @LuaFunction
  public final void setKineticSpeed(int speed) throws LuaException {
    if (speed > MAX_SPEED) {
      throw new LuaException("max speed is " + MAX_SPEED);
    }

    this.speed = speed;
    accessor.transportMachineCoreEntity.setKineticSpeed(speed);
  }

  @LuaFunction
  public final int getKineticSpeed() {
    return speed;
  }

  @LuaFunction
  public final Map<String, Double> getContraptionPosition() {
    Vec3 pos = accessor.transportMachineCoreEntity.getContraptionPosition();
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
    Direction direction = accessor.transportMachineCoreEntity.getContraptionFacing();
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

    Direction facingDirection = accessor.transportMachineCoreEntity.getContraptionFacing();
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
      return accessor.transportMachineCoreEntity.blockPosition();
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
