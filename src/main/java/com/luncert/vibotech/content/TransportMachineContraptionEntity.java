package com.luncert.vibotech.content;

import com.luncert.vibotech.index.AllEntityTypes;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class TransportMachineContraptionEntity extends OrientedContraptionEntity {

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
    if (nonDamageTicks > 0)
      nonDamageTicks--;

    TransportMachineEntity vehicle = (TransportMachineEntity) getVehicle();
    if (vehicle == null)
      return;

    boolean rotationLock = false;
    boolean pauseWhileRotating = false;
    boolean wasStalled = isStalled();
    if (contraption instanceof TransportMachineContraption contraption) {
      // rotationLock = contraption.rotationMode == EContraptionMovementMode.ROTATION_LOCKED;
      // pauseWhileRotating = contraption.rotationMode == EContraptionMovementMode.ROTATE_PAUSED;
    }

    boolean rotating = updateOrientation(rotationLock, wasStalled, vehicle, false);
    if (!rotating || !pauseWhileRotating)
      tickActors();
  }
}
