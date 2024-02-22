package com.luncert.vibotech.compat.create;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.slf4j.Logger;

public class ViboMachineContraptionEntityRenderer extends ContraptionEntityRenderer<ViboMachineContraptionEntity> {

  private static final Logger LOGGER = LogUtils.getLogger();

  public ViboMachineContraptionEntityRenderer(EntityRendererProvider.Context context) {
    super(context);
  }

  @Override
  public boolean shouldRender(ViboMachineContraptionEntity entity, Frustum clippingHelper, double cameraX, double cameraY, double cameraZ) {
    if (!super.shouldRender(entity, clippingHelper, cameraX, cameraY, cameraZ))
      return false;
    return entity.getVehicle() != null;
  }
}
