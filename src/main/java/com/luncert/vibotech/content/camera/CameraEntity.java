package com.luncert.vibotech.content.camera;

import com.luncert.vibotech.content.vibomachinecore.ViboMachineEntity;
import com.luncert.vibotech.index.AllEntityTypes;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class CameraEntity extends Entity {

  private static final EntityDataAccessor<Float> INITIAL_ORIENTATION =
      SynchedEntityData.defineId(CameraEntity.class, EntityDataSerializers.FLOAT);

  public CameraEntity(EntityType<?> pEntityType, Level pLevel) {
    super(pEntityType, pLevel);
  }

  public static CameraEntity create(Level level, BlockPos pos, Direction orientation) {
    CameraEntity entity = new CameraEntity(AllEntityTypes.CAMERA.get(), level);
    entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    entity.setYRot(orientation.toYRot());
    entity.setInitialOrientation(orientation.toYRot());
    return entity;
  }

  public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
    return builder.sized(0.25F, 0.35F);
  }

  // @Override
  // public void tick() {
  //   if (level().isClientSide)
  //     return;
  //   boolean blockPresent = level().getBlockState(blockPosition()).getBlock() instanceof CameraBlock;
  //   if (isVehicle() && blockPresent)
  //     return;
  //   this.discard();
  // }

  @Override
  protected void defineSynchedData() {
    entityData.packDirty();
    entityData.define(INITIAL_ORIENTATION, 0f);
  }

  @Override
  protected void readAdditionalSaveData(CompoundTag root) {
    entityData.set(INITIAL_ORIENTATION, root.getFloat("initialOrientation"));
  }

  @Override
  protected void addAdditionalSaveData(CompoundTag root) {
    root.putFloat("initialOrientation", entityData.get(INITIAL_ORIENTATION));
  }

  private void setInitialOrientation(float yRot) {
    entityData.set(INITIAL_ORIENTATION, yRot);
  }

  private float getInitialOrientation() {
    return entityData.get(INITIAL_ORIENTATION);
  }

  @Override
  public void turn(double pYRot, double pXRot) {
    super.turn(pYRot * 0.2, pXRot * 0.2);
    this.setXRot(Mth.clamp(this.getXRot(), -45.0F, 45.0F));
    this.xRotO = Mth.clamp(this.xRotO, -45.0F, 45.0F);
    float initialOrientation = getInitialOrientation();
    this.setYRot(Mth.clamp(this.getYRot(), initialOrientation - 45.0F, initialOrientation + 45.0F));
    this.yRotO = Mth.clamp(this.yRotO, initialOrientation - 45.0F, initialOrientation + 45.0F);
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
