package com.luncert.vibotech.compat.create;

import com.luncert.vibotech.content.vibomachinecore.ViboMachineEntity;
import com.luncert.vibotech.index.AllEntityTypes;
import com.mojang.logging.LogUtils;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.sync.ContraptionSeatMappingPacket;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

public class ViboMachineContraptionEntity extends OrientedContraptionEntity {

  private static final Logger LOGGER = LogUtils.getLogger();

  // private static final Logger LOGGER = LogUtils.getLogger();

  public ViboMachineContraptionEntity(EntityType<?> type, Level world) {
    super(type, world);
  }

  public static ViboMachineContraptionEntity create(Level world, Contraption contraption, Direction initialOrientation) {
    ViboMachineContraptionEntity entity = new ViboMachineContraptionEntity(AllEntityTypes.VIBO_MACHINE_CONTRAPTION.get(), world);
    entity.setContraption(contraption);
    entity.setInitialOrientation(initialOrientation);
    entity.startAtInitialYaw();
    return entity;
  }

  @Override
  protected void tickContraption() {
    ViboMachineEntity vehicle = (ViboMachineEntity) getVehicle();
    if (vehicle == null)
      return;

    boolean pauseWhileRotating = false;
    boolean wasStalled = isStalled();
    if (contraption instanceof ViboMachineContraption c) {
      pauseWhileRotating = c.rotationMode == EContraptionMovementMode.ROTATE_PAUSED;
    }

    boolean rotating = updateOrientation(false, wasStalled, vehicle, false);
    if (!rotating || !pauseWhileRotating) {
      // will update isStalled
      tickActors();
      if (!level().isClientSide) {
        tickComponents();
      }
    }

    Entity riding = getVehicle();
    boolean isStalled = this.isStalled();
    if (isStalled) {
      if (!wasStalled) {
        this.motionBeforeStall = riding.getDeltaMovement();
      }

      // block entity movement
      riding.setDeltaMovement(Vec3.ZERO);
      riding.setPos(riding.xOld, riding.yOld, riding.zOld);
    } else if (wasStalled) {
      riding.setDeltaMovement(this.motionBeforeStall);
      this.motionBeforeStall = Vec3.ZERO;
    }
  }

  private void tickComponents() {
    ViboMachineContraption c = (ViboMachineContraption) contraption;
    c.initComponents(level(), (ViboMachineEntity) this.getVehicle());
    c.tickComponents();
  }

  @Override
  protected boolean updateOrientation(boolean rotationLock, boolean wasStalled, Entity riding, boolean isOnCoupling) {
    this.prevYaw = this.yaw;
    if (wasStalled) {
      return false;
    }

    boolean rotating = false;

    Vec3 movementVector = riding.getDeltaMovement();
    Vec3 locationDiff = riding.position().subtract(riding.xo, riding.yo, riding.zo);
    Vec3 motion = movementVector.normalize();
    if (!rotationLock) {
      if (motion.length() > 0.0) {
        this.targetYaw = yawFromVector(motion);
        if (this.targetYaw < 0.0F) {
          this.targetYaw += 360.0F;
        }

        if (this.yaw < 0.0F) {
          this.yaw += 360.0F;
        }
      }

      this.prevYaw = this.yaw;
      float maxApproachSpeed = (float) (motion.length() * 8 / Math.max(1.0, this.getBoundingBox().getXsize() / 6.0));
      float yawHint = AngleHelper.getShortestAngleDiff(this.yaw, yawFromVector(locationDiff));
      float approach = AngleHelper.getShortestAngleDiff(this.yaw, this.targetYaw, yawHint);
      approach = Mth.clamp(approach, -maxApproachSpeed, maxApproachSpeed);
      this.yaw += approach;
      if (Math.abs(AngleHelper.getShortestAngleDiff(this.yaw, this.targetYaw)) < 1.0F) {
        this.yaw = this.targetYaw;
      } else {
        rotating = true;
      }
    }

    return rotating;
  }

  // add camera entity to seat mapping
  @Override
  public void addSittingPassenger(Entity passenger, int seatIndex) {
    // called by onEntityInitialize
    for (Entity entity : getPassengers()) {
      BlockPos seatOf = contraption.getSeatOf(entity.getUUID());
      if (seatOf != null && seatOf.equals(contraption.getSeats().get(seatIndex))) {
        if (entity instanceof Player) {
          return;
        }
        if (!(passenger instanceof Player)) {
          return;
        }
        entity.stopRiding();
      }
    }

    passenger.startRiding(this, true);
    if (passenger instanceof TamableAnimal ta)
      ta.setInSittingPose(true);
    if (level().isClientSide)
      return;
    contraption.getSeatMapping().put(passenger.getUUID(), seatIndex);
    AllPackets.getChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
        new ContraptionSeatMappingPacket(getId(), contraption.getSeatMapping()));
  }
}
