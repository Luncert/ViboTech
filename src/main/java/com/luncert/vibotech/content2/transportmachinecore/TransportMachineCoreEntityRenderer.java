package com.luncert.vibotech.content2.transportmachinecore;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class TransportMachineCoreEntityRenderer extends EntityRenderer<TransportMachineCoreEntity> {

  public TransportMachineCoreEntityRenderer(EntityRendererProvider.Context pContext) {
    super(pContext);
  }

  @Override
  public ResourceLocation getTextureLocation(TransportMachineCoreEntity transportMachineEntity) {
    return null;
  }

  @Override
  public boolean shouldRender(TransportMachineCoreEntity entity, Frustum p_114492_, double cameraX, double cameraY, double cameraZ) {
    return false;
  }
}
