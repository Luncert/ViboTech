package com.luncert.vibotech.content.vibomachinecontrolseat;

import com.luncert.vibotech.index.AllEntityTypes;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ControlSeatEntity extends SeatEntity {

  public ControlSeatEntity(EntityType<?> entityType, Level world) {
    super(entityType, world);
  }

  public ControlSeatEntity(Level world, BlockPos pos) {
    super(AllEntityTypes.CONTROL_SEAT.get(), world);
    this.noPhysics = true;
  }

  @Override
  public void tick() {
    if (!this.level().isClientSide) {
      boolean blockPresent = this.level().getBlockState(this.blockPosition()).getBlock() instanceof ControlSeatBlock;
      if (!this.isVehicle() || !blockPresent) {
        this.discard();
      }
    }
  }

  //@Override
  //public double getPassengersRidingOffset() {
  //  return -1;
  //}

  public static class Render extends EntityRenderer<ControlSeatEntity> {
    public Render(EntityRendererProvider.Context context) {
      super(context);
    }

    public boolean shouldRender(ControlSeatEntity p_225626_1_, Frustum p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
      return false;
    }

    public ResourceLocation getTextureLocation(ControlSeatEntity p_110775_1_) {
      return null;
    }
  }
}
