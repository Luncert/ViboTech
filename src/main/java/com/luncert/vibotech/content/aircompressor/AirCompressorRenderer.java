package com.luncert.vibotech.content.aircompressor;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

public class AirCompressorRenderer extends KineticBlockEntityRenderer<AirCompressorBlockEntity> {

  private static final Logger LOGGER = LogUtils.getLogger();

  public AirCompressorRenderer(BlockEntityRendererProvider.Context context) {
    super(context);
  }

  @Override
  protected void renderSafe(AirCompressorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
    if (Backend.canUseInstancing(be.getLevel())) return;
    LOGGER.info("xxx");

    Direction direction = be.getBlockState().getValue(HORIZONTAL_FACING);
    VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

    int lightBehind = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(direction.getOpposite()));
    int lightInFront = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(direction));

    SuperByteBuffer shaftHalf =
        CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState(), direction);
    SuperByteBuffer fanInner =
        CachedBufferer.partialFacing(AllPartialModels.ENCASED_FAN_INNER, be.getBlockState(), direction);

    float time = AnimationTickHolder.getRenderTime();
    float speed = be.getSpeed() * 5;
    if (speed > 0)
      speed = Mth.clamp(speed, 80, 64 * 20);
    if (speed < 0)
      speed = Mth.clamp(speed, -64 * 20, -80);
    float angle = (time * speed * 3 / 10f) % 360;
    angle = angle / 180f * (float) Math.PI;

    standardKineticRotationTransform(shaftHalf, be, lightBehind).renderInto(ms, vb);
    kineticRotationTransform(fanInner, be, direction.getAxis(), angle, lightInFront).renderInto(ms, vb);
  }
}
