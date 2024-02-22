package com.luncert.vibotech.content.vibomachinecontrolseat;

import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ViboMachineControlSeatEntity extends SeatEntity {

  public ViboMachineControlSeatEntity(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
    super(p_i48580_1_, p_i48580_2_);
  }

  public ViboMachineControlSeatEntity(Level world, BlockPos pos) {
    super(world, pos);
  }

  @Override
  public void tick() {
    if (!this.level().isClientSide) {
      boolean blockPresent = this.level().getBlockState(this.blockPosition()).getBlock() instanceof ViboMachineControlSeatBlock;
      if (!this.isVehicle() || !blockPresent) {
        this.discard();
      }
    }
  }

  //@Override
  //public double getPassengersRidingOffset() {
  //  return -1;
  //}

  public static class Render extends EntityRenderer<ViboMachineControlSeatEntity> {
    public Render(EntityRendererProvider.Context context) {
      super(context);
    }

    public boolean shouldRender(ViboMachineControlSeatEntity p_225626_1_, Frustum p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
      return false;
    }

    public ResourceLocation getTextureLocation(ViboMachineControlSeatEntity p_110775_1_) {
      return null;
    }
  }
}
