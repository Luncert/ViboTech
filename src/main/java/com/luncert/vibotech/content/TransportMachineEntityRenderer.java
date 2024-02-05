package com.luncert.vibotech.content;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class TransportMachineEntityRenderer extends EntityRenderer<TransportMachineEntity> {

  public TransportMachineEntityRenderer(EntityRendererProvider.Context pContext) {
    super(pContext);
  }

  @Override
  public ResourceLocation getTextureLocation(TransportMachineEntity transportMachineEntity) {
    return null;
  }

  @Override
  public boolean shouldRender(TransportMachineEntity entity, Frustum p_114492_, double cameraX, double cameraY, double cameraZ) {
    return false;
  }
}
