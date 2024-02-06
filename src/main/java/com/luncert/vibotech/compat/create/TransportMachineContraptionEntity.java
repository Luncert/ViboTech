package com.luncert.vibotech.compat.create;

import com.luncert.vibotech.content.TransportMachineEntity;
import com.luncert.vibotech.index.AllEntityTypes;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.StabilizedContraption;
import com.simibubi.create.content.contraptions.minecart.MinecartSim2020;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class TransportMachineContraptionEntity extends OrientedContraptionEntity {

  private static final Logger LOGGER = LogUtils.getLogger();

  public TransportMachineContraptionEntity(EntityType<?> type, Level world) {
    super(type, world);
  }

  public static TransportMachineContraptionEntity create(Level world, Contraption contraption, Direction initialOrientation) {
    TransportMachineContraptionEntity entity = new TransportMachineContraptionEntity(AllEntityTypes.TRANSPORT_MACHINE.get(), world);
    entity.setContraption(contraption);
    entity.setInitialOrientation(initialOrientation);
    entity.startAtInitialYaw();
    return entity;
  }

  @Override
  protected void tickContraption() {
    TransportMachineEntity vehicle = (TransportMachineEntity) getVehicle();
    if (vehicle == null)
      return;

    boolean pauseWhileRotating = false;
    boolean wasStalled = isStalled();
    if (contraption instanceof TransportMachineContraption c) {
      pauseWhileRotating = c.rotationMode == EContraptionMovementMode.ROTATE_PAUSED;
    }

    boolean rotating = updateOrientation(false, wasStalled, vehicle, false);
    if (!rotating || !pauseWhileRotating)
      tickActors();
  }

  @Override
  protected boolean updateOrientation(boolean rotationLock, boolean wasStalled, Entity riding, boolean isOnCoupling) {
    Vec3 movementVector;
    Vec3 locationDiff;
    this.prevYaw = this.yaw;
    boolean rotating = false;
    movementVector = riding.getDeltaMovement();
    locationDiff = riding.position().subtract(riding.xo, riding.yo, riding.zo);

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
      float maxApproachSpeed = (float) (motion.length() * 4 / Math.max(1.0, this.getBoundingBox().getXsize() / 6.0));
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
