package com.luncert.vibotech.content;

import static com.luncert.vibotech.content.TransportMachineMovement.MOVEMENT_SERIALIZER;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

import com.luncert.vibotech.common.ActionCallback;
import com.luncert.vibotech.common.Signal;
import com.luncert.vibotech.common.Utils;
import com.luncert.vibotech.compat.create.TransportMachineContraption;
import com.luncert.vibotech.compat.create.TransportMachineContraptionEntity;
import com.luncert.vibotech.exception.TransportMachineAssemblyException;
import com.luncert.vibotech.exception.TransportMachineMovementException;
import com.luncert.vibotech.index.AllBlocks;
import com.luncert.vibotech.index.AllEntityTypes;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.AssemblyException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

/**
 * Carry {@link TransportMachineContraptionEntity} which is controlled by player or cc.
 */
public class TransportMachineEntity extends Entity {

  private static final Logger LOGGER = LogUtils.getLogger();

  private static final double MIN_MOVE_LENGTH = 0.001;

  private static final EntityDataAccessor<Integer> SPEED =
      SynchedEntityData.defineId(TransportMachineEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Float> TARGET_Y_ROT =
      SynchedEntityData.defineId(TransportMachineEntity.class, EntityDataSerializers.FLOAT);
  private static final EntityDataAccessor<Optional<TransportMachineMovement>> TARGET_MOVEMENT =
      SynchedEntityData.defineId(TransportMachineEntity.class, MOVEMENT_SERIALIZER);

  private AssembleStationBlockEntity station;
  private BlockState stationBlockState = AllBlocks.ASSEMBLE_STATION.get().defaultBlockState();
  private int ttlAfterLostBinding = Integer.MIN_VALUE;

  private final Queue<ActionCallback> asyncCallbacks = new ArrayDeque<>();
  private final Signal signal = new Signal();
  private int actionCoolDown; // see lerp
  public boolean isMoving;

  private int lerpSteps;
  private double lerpX;
  private double lerpY;
  private double lerpZ;
  private double lerpYRot;
  private double lerpXRot;


  public TransportMachineEntity(EntityType<?> pEntityType, Level pLevel) {
    super(pEntityType, pLevel);
  }

  public TransportMachineEntity(Level world, BlockPos stationPos, BlockState stationBlockState) {
    super(AllEntityTypes.TRANSPORT_MACHINE_VEHICLE.get(), world);
    this.station = (AssembleStationBlockEntity) world.getBlockEntity(stationPos);
    this.stationBlockState = stationBlockState;
    // following data will be synced automatically
    setPos(stationPos.getX() + .5f, stationPos.getY(), stationPos.getZ() + .5f);
    this.noPhysics = true;
    Direction blockDirection = stationBlockState.getValue(HORIZONTAL_FACING);
    setYRot(blockDirection.toYRot());
    setTargetYRot(getYRot());

    setDeltaMovement(Vec3.ZERO);
  }

  public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
    @SuppressWarnings("unchecked")
    EntityType.Builder<TransportMachineEntity> entityBuilder = (EntityType.Builder<TransportMachineEntity>) builder;
    return entityBuilder.sized(0.1f, 0.1f);
  }

  public void bindStation(AssembleStationBlockEntity station) {
    this.station = station;
  }

  public boolean assemble(BlockPos pos) throws TransportMachineAssemblyException {
    Level world = level();
    TransportMachineContraption contraption = new TransportMachineContraption();
    try {
      if (!contraption.assemble(world, pos))
        return false;
    } catch (AssemblyException e) {
      throw new TransportMachineAssemblyException(e);
    }

    contraption.removeBlocksFromWorld(world, BlockPos.ZERO);
    contraption.startMoving(world);
    contraption.expandBoundsAroundAxis(Direction.Axis.Y);

    Direction initialOrientation = stationBlockState.getValue(HORIZONTAL_FACING);
    TransportMachineContraptionEntity entity = TransportMachineContraptionEntity.create(world, contraption, initialOrientation);

    entity.setPos(pos.getX() + .5f, pos.getY(), pos.getZ() + .5f);
    world.addFreshEntity(entity);
    entity.startRiding(this);

    return true;
  }

  public void dissemble() {
    ejectPassengers();
    discard();
  }

  // action api

  public void up(int n, ActionCallback callback) throws TransportMachineMovementException {
    checkSpeed();
    checkMotion();
    checkSignal();
    setTargetMovement(new TransportMachineMovement(Direction.Axis.Y, true,
        (float) position().get(Direction.Axis.Y) + n));
    isMoving = true;
    asyncCallbacks.add(callback);
  }

  public void down(int n, ActionCallback callback) throws TransportMachineMovementException {
    checkSpeed();
    checkMotion();
    checkSignal();
    setTargetMovement(new TransportMachineMovement(Direction.Axis.Y, false,
        (float) position().get(Direction.Axis.Y) - n));
    isMoving = true;
    asyncCallbacks.add(callback);
  }

  public void forward(int n, ActionCallback callback) throws TransportMachineMovementException {
    checkSpeed();
    checkMotion();
    checkSignal();
    moveForward(n, callback);
  }

  public void turnLeft(ActionCallback callback) throws TransportMachineMovementException {
    checkSpeed();
    checkMotion();
    checkSignal();
    rotate(-90, callback);
  }

  public void turnRight(ActionCallback callback) throws TransportMachineMovementException {
    checkSpeed();
    checkMotion();
    checkSignal();
    rotate(90, callback);
  }

  private void checkSignal() {
    if (actionCoolDown > 0) {
      signal.get();
    }
  }

  private void rotate(int degree, ActionCallback callback) {
    float targetYRot = getYRot() + degree;
    if (targetYRot < 0) {
      targetYRot += 360;
    } else if (targetYRot > 360) {
      targetYRot -= 360;
    }
    setTargetYRot(targetYRot);
    moveForward(1, callback);
  }

  private void moveForward(int n, ActionCallback callback) {
    Direction direction = Direction.fromYRot(getTargetYRot());
    Direction.Axis axis = direction.getAxis();
    Direction.AxisDirection axisDirection = direction.getAxisDirection();
    int posDelta = axisDirection.getStep() * n;

    setTargetMovement(new TransportMachineMovement(axis, axisDirection.equals(Direction.AxisDirection.POSITIVE),
        (float) position().get(axis) + posDelta));
    isMoving = true;
    asyncCallbacks.add(callback);
  }

  private void checkSpeed() throws TransportMachineMovementException {
    if (getKineticSpeed() == 0) {
      throw new TransportMachineMovementException("speed_is_zero");
    }
  }

  private void checkMotion() throws TransportMachineMovementException {
    if (isMoving) {
      throw new TransportMachineMovementException("cannot_update_moving_aircraft");
    }
  }

  // info api

  public Vec3 getAircraftPosition() {
    BlockPos blockPos = blockPosition();
    // aircraft entity is invisible, so aircraft's position should be above the entity position
    return new Vec3(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
  }

  public Direction getAircraftFacing() {
    return Direction.fromYRot(getTargetYRot());
  }

  // impl

  public Optional<TransportMachineContraption> getContraption() {
    return getContraptionEntity().map(e -> (TransportMachineContraption) e.getContraption());
  }

  private Optional<TransportMachineContraptionEntity> getContraptionEntity() {
    List<Entity> passengers = getPassengers();
    if (!passengers.isEmpty()) {
      for (Entity passenger : passengers) {
        if (passenger instanceof TransportMachineContraptionEntity contraptionEntity) {
          return Optional.of(contraptionEntity);
        }
      }
    }

    return Optional.empty();
  }

  @OnlyIn(Dist.CLIENT)
  public void lerpTo(double lerpX, double lerpY, double lerpZ, float lerpYRot, float lerpXRot,
                     int p_180426_9_, boolean p_180426_10_) {
    this.lerpX = lerpX;
    this.lerpY = lerpY;
    this.lerpZ = lerpZ;
    this.lerpYRot = lerpYRot;
    this.lerpXRot = lerpXRot;
    this.lerpSteps = 10;
  }

  @Override
  public void tick() {
    if (this.stationBlockState.isAir()) {
      // invalid block state
      discard();
      return;
    }

    if (!level().isClientSide && station == null) {
      // if lost binding to station, dissemble
      if (ttlAfterLostBinding == Integer.MIN_VALUE) {
        ttlAfterLostBinding = 10;
      } else if (--ttlAfterLostBinding < 0) {
        dissemble();
      }
      return;
    }

    super.tick();
    tickLerp();
    tickCollide();
    tickComponents();
    tickMotion();
  }

  private void tickLerp() {
    // isControlledByLocalInstance always return true in server side
    if (this.isControlledByLocalInstance()) {
      this.lerpSteps = 0;
      this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
      return;
    }

    // client only
    if (this.lerpSteps > 0) {
      double d0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
      double d1 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
      double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
      this.setPos(d0, d1, d2);

      double d3 = Mth.wrapDegrees(this.lerpYRot - getYRot());
      setYRot((float)((double)getYRot() + d3 / (double)this.lerpSteps));
      setXRot((float)((double)getXRot() + (this.lerpXRot - (double)getXRot()) / (double)this.lerpSteps));
      this.setRot(getYRot(), getXRot());

      --this.lerpSteps;
    }
  }

  private void tickCollide() {
    getTargetMovement().ifPresent(movement -> {
      // Direction direction = Direction.fromYRot(getTargetYRot());
      // Axis axis = direction.getAxis();
      // Direction.AxisDirection axisDirection = direction.getAxisDirection();
      //
      // BlockPos pos = blockPosition();
      // BlockPos targetPos = pos.relative(axis, axisDirection.getStep());
      // double dist = targetPos.get(axis) - pos.get(axis);
      if (Direction.Axis.Y.equals(movement.axis) && !movement.positive) {
        BlockPos pos = blockPosition();
        BlockPos targetPos = pos.below();
        double dist = targetPos.getY() - pos.getY();
        if (dist < MIN_MOVE_LENGTH && !isFree(level().getBlockState(targetPos))) {
          setPos(Vec3.atBottomCenterOf(pos));
          clearMotion();
        }
      }
    });
  }

  private boolean isFree(BlockState blockState) {
    return !blockState.is(Blocks.BEDROCK);
  }

  private void tickMotion() {
    if (actionCoolDown > 0) {
      actionCoolDown--;
      if (actionCoolDown == 0 && !level().isClientSide) {
        signal.set();
      }
      return;
    }

    boolean isStalled = getContraptionEntity().map(TransportMachineContraptionEntity::isStalled).orElse(false);
    if (isStalled) {
      return;
    }

    updateMotion().ifPresent(motion -> {
      setDeltaMovement(motion);
      setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
    });
  }

  private void tickComponents() {
    if (level().isClientSide) {
      return;
    }

    // getContraption().ifPresent(contraption -> {
    //   contraption.initComponents(level(), this);
    //   for (List<IAircraftComponent> value : contraption.getOrderedComponents()) {
    //     for (IAircraftComponent component : value) {
    //       component.tickComponent();
    //     }
    //   }
    // });
  }

  private Optional<Vec3> updateMotion() {
    Optional<TransportMachineMovement> opt = getTargetMovement();
    if (opt.isPresent()) {
      TransportMachineMovement movement = opt.get();
      double v = position().get(movement.axis);
      double absDist = Math.abs(movement.expectedPos - v);
      if (absDist > 0) {
        return Optional.ofNullable(updateMotion(absDist, movement));
      }
      setYRot(getTargetYRot());
      setTargetMovement(null);
    }

    clearMotion();
    return Optional.empty();
  }

  private Vec3 updateMotion(double absDistance, TransportMachineMovement movement) {
    if (absDistance < MIN_MOVE_LENGTH) {
      setPos(Utils.set(position(), movement.axis, movement.expectedPos));
      return null;
    }

    float speed;
    if (getYRot() != getTargetYRot() || movement.axis.equals(Direction.Axis.Y)) {
      // if entity is rotating, set speed to 32
      speed = getMovementSpeed(32);
    } else {
      speed = getMovementSpeed();
    }

    double linearMotion = Math.min(speed, absDistance);
    if (!movement.positive) {
      linearMotion = -linearMotion;
    }

    return Utils.linear(movement.axis, linearMotion);
  }

  private void clearMotion() {
    setTargetMovement(null);
    setDeltaMovement(Vec3.ZERO);
    if (isMoving && !level().isClientSide) {
      asyncCallbacks.remove().accept(true);
    }
    actionCoolDown = 11;
    isMoving = false;
  }

  public float getMovementSpeed() {
    return getKineticSpeed() / 512f * 1.5f;
  }

  private float getMovementSpeed(int speed) {
    return speed / 512f * 1.5f;
  }

  public void setKineticSpeed(int speed) {
    entityData.set(SPEED, Mth.clamp(Math.abs(speed), 0, 255));
  }

  public int getKineticSpeed() {
    return entityData.get(SPEED);
  }

  public void setTargetYRot(float yRot) {
    entityData.set(TARGET_Y_ROT, yRot % 360);
  }

  public float getTargetYRot() {
    return entityData.get(TARGET_Y_ROT);
  }

  public void setTargetMovement(@Nullable TransportMachineMovement movement) {
    entityData.set(TARGET_MOVEMENT, Optional.ofNullable(movement));
  }

  private Optional<TransportMachineMovement> getTargetMovement() {
    return entityData.get(TARGET_MOVEMENT);
  }

  // data exchange

  @Override
  protected void defineSynchedData() {
    // entityData.clearDirty();
    entityData.packDirty();
    entityData.define(SPEED, 0);
    entityData.define(TARGET_Y_ROT, 0f);
    entityData.define(TARGET_MOVEMENT, Optional.empty());
  }

  @Override
  protected void readAdditionalSaveData(CompoundTag root) {
    if (root.isEmpty())
      return;

    entityData.set(SPEED, root.getInt("speed"));
    entityData.set(TARGET_Y_ROT, root.getFloat("targetYRot"));

    if (root.getBoolean("hasTargetMovement")) {
      CompoundTag targetMovement = root.getCompound("targetMovement");
      setTargetMovement(new TransportMachineMovement(
          Direction.Axis.values()[targetMovement.getInt("axis")],
          targetMovement.getBoolean("positive"),
          targetMovement.getFloat("expectedPos")));
    }
  }

  @Override
  protected void addAdditionalSaveData(CompoundTag root) {
    root.putInt("speed", getKineticSpeed());
    root.putFloat("targetYRot", getTargetYRot());

    Optional<TransportMachineMovement> opt = getTargetMovement();
    root.putBoolean("hasTargetMovement", opt.isPresent());
    opt.ifPresent(movement -> {
      CompoundTag targetMovement = new CompoundTag();
      targetMovement.putInt("axis", movement.axis.ordinal());
      targetMovement.putBoolean("positive", movement.positive);
      targetMovement.putFloat("expectedPos", movement.expectedPos);
      root.put("targetMovement", targetMovement);
    });
  }

  @Override
  public Packet<ClientGamePacketListener> getAddEntityPacket() {
    return new ClientboundAddEntityPacket(this);
  }

  @Override
  public double getPassengersRidingOffset() {
    return 0;
  }
}
