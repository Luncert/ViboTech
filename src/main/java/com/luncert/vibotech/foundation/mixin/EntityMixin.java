package com.luncert.vibotech.foundation.mixin;

import com.luncert.vibotech.content.camera.CameraData;
import com.luncert.vibotech.content.camera.CameraEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

  // Makes mouse input rotate the FreeCamera.
  @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
  private void onChangeLookDirection(double x, double y, CallbackInfo ci) {
    Minecraft mc = Minecraft.getInstance();
    if (CameraData.isEnabled() && this.equals(mc.player) && mc.getCameraEntity() instanceof CameraEntity camera) {
      camera.turn(x, y);
      ci.cancel();
    }
  }
}
