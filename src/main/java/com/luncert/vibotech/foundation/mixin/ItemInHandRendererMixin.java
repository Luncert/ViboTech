package com.luncert.vibotech.foundation.mixin;

import com.luncert.vibotech.content.camera.CameraData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

  @Inject(method = "renderHandsWithItems", at = @At("HEAD"), cancellable = true)
  private void preventRenderHandsWithItems(float pPartialTicks, PoseStack pPoseStack,
                                           MultiBufferSource.BufferSource pBuffer,
                                           LocalPlayer pPlayerEntity,
                                           int pCombinedLight, CallbackInfo ci) {
    if (CameraData.isEnabled()) {
      ci.cancel();
    }
  }
}
