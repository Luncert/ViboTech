package com.luncert.vibotech.content.camera;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CameraData {

  public final Vec3 position;
  public final float pitch;
  public final float yaw;
  public final String dimension;
  public CameraData(Level world, Vec3 position, float pitch, float yaw) {
    this.dimension = world.dimension().toString();
    this.position = position;
    this.pitch = pitch;
    this.yaw = yaw;
  }
}
