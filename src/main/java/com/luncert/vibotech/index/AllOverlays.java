package com.luncert.vibotech.index;

import com.luncert.vibotech.ViboTechMod;
import com.luncert.vibotech.content.camera.CameraData;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class AllOverlays {

  private static final ResourceLocation CAMERA_OVERLAY_LOCATION = ViboTechMod.asResource("textures/misc/camera.png");

  public static final IGuiOverlay CAMERA = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
    if (CameraData.isEnabled()) {

      float f = (float)Math.min(screenWidth, screenHeight);
      float f1 = Math.min((float)screenWidth / f, (float)screenHeight / f);
      int i = Mth.floor(f * f1);
      int j = Mth.floor(f * f1);
      int k = (screenWidth - i) / 2;
      int l = (screenHeight - j) / 2;
      int i1 = k + i;
      int j1 = l + j;
      guiGraphics.blit(CAMERA_OVERLAY_LOCATION, k, l, -90, 0.0F, 0.0F, i, j, i, j);
      guiGraphics.fill(RenderType.guiOverlay(), 0, j1, screenWidth, screenHeight, -90, -16777216);
      guiGraphics.fill(RenderType.guiOverlay(), 0, 0, screenWidth, l, -90, -16777216);
      guiGraphics.fill(RenderType.guiOverlay(), 0, l, k, j1, -90, -16777216);
      guiGraphics.fill(RenderType.guiOverlay(), i1, l, screenWidth, j1, -90, -16777216);

      //RenderSystem.disableDepthTest();
      //RenderSystem.depthMask(false);
      //guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      //guiGraphics.blit(CAMERA_OVERLAY_LOCATION, 0, 0, -90, 0.0F, 0.0F, screenWidth, screenHeight, screenWidth, screenHeight);
      //RenderSystem.depthMask(true);
      //RenderSystem.enableDepthTest();
      //guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
  };
}
