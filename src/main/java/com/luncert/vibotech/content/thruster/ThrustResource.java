package com.luncert.vibotech.content.thruster;

import com.luncert.vibotech.compat.vibotech.IViboMachineResource;

public class ThrustResource implements IViboMachineResource {

  public static final String ID = "thrust";

  private double power;

  public ThrustResource(double power) {
    this.power = power;
  }

  public void setPower(double power) {
    this.power = power;
  }

  public double getPower() {
    return power;
  }

  @Override
  public String getId() {
    return ID;
  }
}
