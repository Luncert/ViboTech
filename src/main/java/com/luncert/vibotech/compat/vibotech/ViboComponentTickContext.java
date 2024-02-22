package com.luncert.vibotech.compat.vibotech;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class ViboComponentTickContext {

  private boolean powerOn;
  private int speed;
  private final Map<String, Object> resourceMap = new HashMap<>();

  public void setPowerOn(boolean powerOn) {
    this.powerOn = powerOn;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
  }

  public boolean isPowerOn() {
    return powerOn;
  }

  public int getSpeed() {
    return speed;
  }

  /**
   * Resource will be refreshed before each tick.
   */
  public <T extends IViboMachineResource> void updateResource(String name, Function<T, T> updater) {
    resourceMap.compute(name, (k, v) -> updater.apply((T) v));
  }

  @SuppressWarnings("unchecked")
  public <T extends IViboMachineResource> Optional<T> getResource(String name) {
    return Optional.ofNullable((T) resourceMap.get(name));
  }

  public void reset() {
    resourceMap.clear();
  }

  public Tag writeNBT() {
    CompoundTag tag = new CompoundTag();
    tag.putBoolean("powerOn", powerOn);
    tag.putInt("speed", speed);
    return tag;
  }

  public void readNBT(Tag tag) {
    CompoundTag compoundTag = (CompoundTag) tag;
    powerOn = compoundTag.getBoolean("powerOn");
    speed = compoundTag.getInt("speed");
  }
}
