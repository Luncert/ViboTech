package com.luncert.vibotech.content2.gastank;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class GasTankBlockEntityRenderer extends SafeBlockEntityRenderer<GasTankBlockEntity> {

  public GasTankBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
  }

  @Override
  protected void renderSafe(GasTankBlockEntity be, float partialTicks,
                            PoseStack ms, MultiBufferSource buffer,
                            int light, int overlay) {
    LerpedFloat fluidLevel = be.getFluidLevel();
    if (fluidLevel != null) {
      float minCapHeight = 0.25F;
      float maxCapHeight = 0.15F;
      float tankHullWidth = 0.0703125F;
      float totalHeight = 1 - minCapHeight - maxCapHeight;
      float level = fluidLevel.getValue(partialTicks);

      if (!(level < 1.0F / (512.0F * totalHeight))) {
        float clampedLevel = Mth.clamp(level * totalHeight, 0.0F, totalHeight);
        FluidTank tank = (FluidTank) be.getTankInventory();
        FluidStack fluidStack = tank.getFluid();
        if (!fluidStack.isEmpty()) {
          float xMax = tankHullWidth + (float) be.getWidth() - 2.0F * tankHullWidth;
          float yMin = minCapHeight;
          float yMax = yMin + clampedLevel;


          float zMax = tankHullWidth + (float) be.getWidth() - 2.0F * tankHullWidth;
          ms.pushPose();
          ms.translate(0.0F, clampedLevel - totalHeight, 0.0F);
          FluidRenderer.renderFluidBox(fluidStack, tankHullWidth, yMin, tankHullWidth, xMax, yMax, zMax, buffer, ms, light, false);
          ms.popPose();
        }
      }
    }
  }
}
