package com.luncert.vibotech.content.vibomachinecore;

import static com.luncert.vibotech.content.vibomachinecore.EntityMovementData.MOVEMENT_SERIALIZER;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

import com.luncert.vibotech.common.ActionCallback;
import com.luncert.vibotech.common.Signal;
import com.luncert.vibotech.common.Utils;
import com.luncert.vibotech.compat.create.EContraptionMovementMode;
import com.luncert.vibotech.compat.create.ViboMachineContraption;
import com.luncert.vibotech.compat.create.ViboMachineContraptionEntity;
import com.luncert.vibotech.content.assemblestation.AssembleStationBlockEntity;
import com.luncert.vibotech.exception.ViboMachineAssemblyException;
import com.luncert.vibotech.exception.ViboMachineMovementException;
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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class ViboMachineEntity extends Entity {

  private static final Logger LOGGER = LogUtils.getLogger();
  private static final double MIN_MOVE_LENGTH = 0.001;
  private static final EntityDataAccessor<Boolean> POWER =
      SynchedEntityData.defineId(ViboMachineEntity.class, EntityDataSerializers.BOOLEAN);
  private static final EntityDataAccessor<Integer> SPEED =
      SynchedEntityData.defineId(ViboMachineEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Float> TARGET_Y_ROT =
      SynchedEntityData.defineId(ViboMachineEntity.class, EntityDataSerializers.FLOAT);
  private static final EntityDataAccessor<Optional<EntityMovementData>> TARGET_MOVEMENT =
      SynchedEntityData.defineId(ViboMachineEntity.class, MOVEMENT_SERIALIZER);

  private Direction initialOrientation;
  private AssembleStationBlockEntity assembleStationBlockEntity;

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

  public ViboMachineEntity(EntityType<?> pEntityType, Level pLevel) {
    super(pEntityType, pLevel);
  }

  public ViboMachineEntity(Level world, BlockPos stationPos, BlockState viboMachineCoreState) {
    super(AllEntityTypes.VIBO_MACHINE.get(), world);
    // following data will be synced automatically
    setPos(stationPos.getX() + .5f, stationPos.getY(), stationPos.getZ() + .5f);
    this.noPhysics = true;
    Direction blockDirection = viboMachineCoreState.getValue(HORIZONTAL_FACING).getOpposite();
    this.initialOrientation = blockDirection;
    setYRot(blockDirection.toYRot());
    setTargetYRot(getYRot());
    setDeltaMovement(Vec3.ZERO);
  }

  // component api

  public Vec3 getContraptionPosition() {
    BlockPos blockPos = blockPosition();
    // vibo machine entity is invisible, so contraption's position should be above the entity position
    return new Vec3(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
  }

  public Direction getContraptionFacing() {
    return Direction.fromYRot(getTargetYRot());
  }


  public void up(int n, ActionCallback callback) throws ViboMachineMovementException {
    checkSpeed();
    checkMotion();
    checkSignal();
    setTargetMovement(new EntityMovementData(Direction.Axis.Y, true,
        (float) position().get(Direction.Axis.Y) + n));
    isMoving = true;
    asyncCallbacks.add(callback);
  }

  public void down(int n, ActionCallback callback) throws ViboMachineMovementException {
    checkSpeed();
    checkMotion();
    checkSignal();
    setTargetMovement(new EntityMovementData(Direction.Axis.Y, false,
        (float) position().get(Direction.Axis.Y) - n));
    isMoving = true;
    asyncCallbacks.add(callback);
  }

  public void forward(int n, ActionCallback callback) throws ViboMachineMovementException {
    checkSpeed();
    checkMotion();
    checkSignal();
    moveForward(n, callback);
  }

  public void turnLeft(ActionCallback callback) throws ViboMachineMovementException {
    checkSpeed();
    checkMotion();
    checkSignal();
    rotate(-90, callback);
  }

  public void turnRight(ActionCallback callback) throws ViboMachineMovementException {
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

    setTargetMovement(new EntityMovementData(axis, axisDirection.equals(Direction.AxisDirection.POSITIVE),
        (float) position().get(axis) + posDelta));
    isMoving = true;
    asyncCallbacks.add(callback);
  }

  private void checkSpeed() throws ViboMachineMovementException {
    if (getKineticSpeed() == 0) {
      throw new ViboMachineMovementException("speed_is_zero");
    }
  }

  private void checkMotion() throws ViboMachineMovementException {
    if (!getPower()) {
      throw new ViboMachineMovementException("contraption_is_power_off");
    }
    if (isMoving) {
      throw new ViboMachineMovementException("cannot_update_moving_contraption");
    }
  }

  // api

  public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
    @SuppressWarnings("unchecked")
    EntityType.Builder<ViboMachineEntity> entityBuilder = (EntityType.Builder<ViboMachineEntity>) builder;
    return entityBuilder.sized(0.1f, 0.1f);
  }

  public void bindAssembleStation(AssembleStationBlockEntity assembleStationBlockEntity) {
    this.assembleStationBlockEntity = assembleStationBlockEntity;
  }

  public Optional<AssembleStationBlockEntity> getAssembleStationBlockEntity() {
    return Optional.ofNullable(assembleStationBlockEntity);
  }

  public Optional<ViboMachineContraption> getContraption() {
    return getContraptionEntity().map(e -> (ViboMachineContraption) e.getContraption());
  }

  private Optional<ViboMachineContraptionEntity> getContraptionEntity() {
    List<Entity> passengers = getPassengers();
    if (!passengers.isEmpty()) {
      for (Entity passenger : passengers) {
        if (passenger instanceof ViboMachineContraptionEntity contraptionEntity) {
          return Optional.of(contraptionEntity);
        }
      }
    }

    return Optional.empty();
  }

  public boolean assemble(EContraptionMovementMode mode, BlockPos pos)
      throws ViboMachineAssemblyException {
    Level world = level();

    // create contraption
    ViboMachineContraption contraption = new ViboMachineContraption(mode, this);
    try {
      if (!contraption.assemble(world, pos)) {
        return false;
      }
    } catch (AssemblyException e) {
      throw new ViboMachineAssemblyException(e);
    }
    contraption.removeBlocksFromWorld(world, BlockPos.ZERO);
    contraption.startMoving(world);
    contraption.expandBoundsAroundAxis(Direction.Axis.Y);

    // create contraption entity
    ViboMachineContraptionEntity contraptionEntity = ViboMachineContraptionEntity
        .create(world, contraption, initialOrientation);
    contraptionEntity.setPos(pos.getX() + .5f, pos.getY(), pos.getZ() + .5f);
    world.addFreshEntity(contraptionEntity);
    contraptionEntity.startRiding(this);

    return true;
  }

  public void dissemble() {
    ejectPassengers();
    discard();
  }

  // impl

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
    super.tick();
    tickLerp();
    tickCollide();
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
      double d0 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
      double d1 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
      double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
      this.setPos(d0, d1, d2);

      double d3 = Mth.wrapDegrees(this.lerpYRot - getYRot());
      setYRot((float) ((double) getYRot() + d3 / (double) this.lerpSteps));
      setXRot((float) ((double) getXRot() + (this.lerpXRot - (double) getXRot()) / (double) this.lerpSteps));
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

    updateMotion().ifPresent(motion -> {
      // if (Create.RANDOM.nextBoolean()) {
      //   motion = motion.relative(Direction.UP, Create.RANDOM.nextDouble() / 2 - 0.5);
      // }
      setDeltaMovement(motion);
      // used by ViboMachineContraptionEntity#updateOrientation
      setOldPosAndRot();
      setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
    });
  }

  private Optional<Vec3> updateMotion() {
    Optional<EntityMovementData> opt = getTargetMovement();
    if (opt.isPresent()) {
      EntityMovementData movement = opt.get();
      double v = position().get(movement.axis);
      double absDist = Math.abs(movement.expectedPos - v);
      if (absDist < MIN_MOVE_LENGTH) {
        setPos(Utils.set(position(), movement.axis, movement.expectedPos));
      } else {
        return Optional.of(updateMotion(absDist, movement));
      }
      // When linear movement done, update yrot.
      // The rotation animation is controlled by contraption entity itself,
      // Which may be out of sync with this vehicle entity.
      setYRot(getTargetYRot());
      setTargetMovement(null);
    }

    clearMotion();
    return Optional.empty();
  }

  private Vec3 updateMotion(double absDistance, EntityMovementData movement) {
    float speed;
    if (getYRot() != getTargetYRot()) {
      // set speed to 16 while rotating
      speed = getMovementSpeed(16);
    } else {
      speed = getMovementSpeed();
    }
    double linearMotion = Math.min(speed, absDistance);
    if (!movement.positive) {
      linearMotion = -linearMotion;
    }
    // LOGGER.info("{} {} {}", linearMotion, getYRot(), getTargetYRot());
    return Utils.linear(movement.axis, linearMotion);
  }

  private void clearMotion() {
    setTargetMovement(null);
    setDeltaMovement(Vec3.ZERO);
    if (isMoving && !level().isClientSide) {
      // TODO: return movement instead of boolean
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

  public void setPower(boolean power) {
    entityData.set(POWER, power);
  }

  public boolean getPower() {
    return entityData.get(POWER);
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

  public void setTargetMovement(@Nullable EntityMovementData movement) {
    entityData.set(TARGET_MOVEMENT, Optional.ofNullable(movement));
  }

  private Optional<EntityMovementData> getTargetMovement() {
    return entityData.get(TARGET_MOVEMENT);
  }

  // data exchange

  @Override
  protected void defineSynchedData() {
    // entityData.clearDirty();
    entityData.packDirty();
    entityData.define(POWER, false);
    entityData.define(SPEED, 16);
    entityData.define(TARGET_Y_ROT, 0f);
    entityData.define(TARGET_MOVEMENT, Optional.empty());
  }

  @Override
  protected void readAdditionalSaveData(CompoundTag root) {
    if (root.isEmpty())
      return;

    entityData.set(POWER, root.getBoolean("power"));
    entityData.set(SPEED, root.getInt("speed"));
    entityData.set(TARGET_Y_ROT, root.getFloat("targetYRot"));

    if (root.getBoolean("hasTargetMovement")) {
      CompoundTag targetMovement = root.getCompound("targetMovement");
      setTargetMovement(new EntityMovementData(
          Direction.Axis.values()[targetMovement.getInt("axis")],
          targetMovement.getBoolean("positive"),
          targetMovement.getFloat("expectedPos")));
    }
  }

  @Override
  protected void addAdditionalSaveData(CompoundTag root) {
    root.putBoolean("power", getPower());
    root.putInt("speed", getKineticSpeed());
    root.putFloat("targetYRot", getTargetYRot());

    Optional<EntityMovementData> opt = getTargetMovement();
    root.putBoolean("hasTargetMovement", opt.isPresent());
    opt.ifPresent(movement -> {
      CompoundTag targetMovement = new CompoundTag();
      targetMovement.putInt("axis", movement.axis.ordinal());
      targetMovement.putBoolean("positive", movement.positive);
      targetMovement.putFloat("expectedPos", movement.expectedPos);
      root.put("targetMovement", targetMovement);
    });
  }

  // @Override
  // public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
  // }

  @Override
  public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
    return new ClientboundAddEntityPacket(this);
  }

  @Override
  public double getPassengersRidingOffset() {
    return 0;
  }
}
