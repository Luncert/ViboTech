package com.luncert.vibotech.compat.create;

import com.luncert.vibotech.content2.transportmachinecore.TransportMachineCoreEntity;
import com.luncert.vibotech.index.AllEntityTypes;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class TransportMachineContraptionEntity extends OrientedContraptionEntity {

  private static final Logger LOGGER = LogUtils.getLogger();

  public TransportMachineContraptionEntity(EntityType<?> type, Level world) {
    super(type, world);
  }

  public static TransportMachineContraptionEntity create(Level world, Contraption contraption, Direction initialOrientation) {
    TransportMachineContraptionEntity entity = new TransportMachineContraptionEntity(AllEntityTypes.TRANSPORT_MACHINE_CONTRAPTION.get(), world);
    entity.setContraption(contraption);
    entity.setInitialOrientation(initialOrientation);
    entity.startAtInitialYaw();
    return entity;
  }

  @Override
  protected void tickContraption() {
    TransportMachineCoreEntity vehicle = (TransportMachineCoreEntity) getVehicle();
    if (vehicle == null)
      return;

    boolean pauseWhileRotating = false;
    boolean wasStalled = isStalled();
    if (contraption instanceof TransportMachineContraption c) {
      pauseWhileRotating = c.rotationMode == EContraptionMovementMode.ROTATE_PAUSED;
    }

    boolean rotating = updateOrientation(false, wasStalled, vehicle, false);
    if (!rotating || !pauseWhileRotating) {
      // will update isStalled
      tickActors();
    }

    Entity riding = getVehicle();
    boolean isStalled = this.isStalled();
    if (isStalled) {
      if (!wasStalled) {
        this.motionBeforeStall = riding.getDeltaMovement();
      }

      // block movement
      riding.setDeltaMovement(Vec3.ZERO);
      riding.setPos(riding.xOld, riding.yOld, riding.zOld);
    } else if (wasStalled) {
      riding.setDeltaMovement(this.motionBeforeStall);
      this.motionBeforeStall = Vec3.ZERO;
    }
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
}
