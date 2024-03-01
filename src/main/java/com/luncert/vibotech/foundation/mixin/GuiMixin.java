//package com.luncert.vibotech.foundation.mixin;
//
//import com.luncert.vibotech.content.camera.CameraData;
//import com.mojang.logging.LogUtils;
//import net.minecraft.client.gui.Gui;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.resources.ResourceLocation;
//import org.slf4j.Logger;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(Gui.class)
//public abstract class GuiMixin {
//
//  private static final Logger LOGGER = LogUtils.getLogger();
//
//  private static final ResourceLocation PUMPKIN_BLUR_LOCATION = new ResourceLocation("textures/misc/pumpkinblur.png");
//
//  @Shadow
//  protected abstract void renderTextureOverlay(GuiGraphics pGuiGraphics, ResourceLocation pShaderLocation, float pAlpha);
//
//  @Inject(method = "render", at = @At("HEAD"), cancellable = true)
//  protected void renderCameraOverlay(GuiGraphics pGuiGraphics, float pPartialTick, CallbackInfo ci) {
//    LOGGER.info("xx");
//    if (CameraData.isEnabled()) {
//      this.renderTextureOverlay(pGuiGraphics, PUMPKIN_BLUR_LOCATION, 1.0F);
//      ci.cancel();
//    }
//  }
//
//  @Inject(method = "renderSpyglassOverlay", at = @At("HEAD"), cancellable = false)
//  public void renderSpyglassOverlay(GuiGraphics pGuiGraphics, float pScopeScale, CallbackInfo ci) {
//    LOGGER.info("yy");
//  }
//}
