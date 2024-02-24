package com.luncert.vibotech.content.camera;

import com.luncert.vibotech.index.AllEntityTypes;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class CameraEntity extends Entity {

  public CameraEntity(EntityType<?> pEntityType, Level pLevel) {
    super(pEntityType, pLevel);
  }

  public static CameraEntity create(Level level) {
    return new CameraEntity(AllEntityTypes.CAMERA.get(), level);
  }

  public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
    return builder.sized(0.25F, 0.35F);
  }

  // public CameraEntity(Level level, Vec3 pos, float yaw, float pitch) {
  //   super(AllEntityTypes.CAMERA.get(), level);
  // }

  @Override
  protected void defineSynchedData() {

  }

  @Override
  protected void readAdditionalSaveData(CompoundTag pCompound) {

  }

  @Override
  protected void addAdditionalSaveData(CompoundTag pCompound) {

  }

  public static class Render extends EntityRenderer<CameraEntity> {
    public Render(EntityRendererProvider.Context context) {
      super(context);
    }

    public boolean shouldRender(CameraEntity p_225626_1_, Frustum p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
      return false;
    }

    public ResourceLocation getTextureLocation(CameraEntity p_110775_1_) {
      return null;
    }
  }
}
