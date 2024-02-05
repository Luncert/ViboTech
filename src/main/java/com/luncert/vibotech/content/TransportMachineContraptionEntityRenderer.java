package com.luncert.vibotech.content;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.slf4j.Logger;

public class TransportMachineContraptionEntityRenderer extends ContraptionEntityRenderer<TransportMachineContraptionEntity> {

  private static final Logger LOGGER = LogUtils.getLogger();

  public TransportMachineContraptionEntityRenderer(EntityRendererProvider.Context context) {
    super(context);
  }

  @Override
  public boolean shouldRender(TransportMachineContraptionEntity entity, Frustum clippingHelper, double cameraX, double cameraY, double cameraZ) {
    if (!super.shouldRender(entity, clippingHelper, cameraX, cameraY, cameraZ))
      return false;
    return entity.getVehicle() != null;
  }
}
