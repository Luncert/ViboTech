package com.luncert.vibotech.content.camera;

import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.mojang.blaze3d.platform.NativeImage;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;

public class CameraComponent extends BaseViboComponent {

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.CAMERA;
  }

  @LuaFunction
  public void connect() {
    // Minecraft mc = Minecraft.getInstance();
    // mc.setCameraEntity();
    // try (NativeImage image = Screenshot.takeScreenshot(mc.getMainRenderTarget())) {
    //
    // }
  }
}
