package com.luncert.vibotech.content.vibomachinecore;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ViboMachineEntityRenderer extends EntityRenderer<ViboMachineEntity> {

  public ViboMachineEntityRenderer(EntityRendererProvider.Context pContext) {
    super(pContext);
  }

  @Override
  public ResourceLocation getTextureLocation(ViboMachineEntity viboMachineEntity) {
    return null;
  }

  @Override
  public boolean shouldRender(ViboMachineEntity entity, Frustum p_114492_, double cameraX, double cameraY, double cameraZ) {
    return false;
  }
}
