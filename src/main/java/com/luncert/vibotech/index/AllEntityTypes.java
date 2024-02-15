package com.luncert.vibotech.index;

import com.luncert.vibotech.ViboTechMod;
import com.luncert.vibotech.common.Lang;
import com.luncert.vibotech.compat.create.TransportMachineContraptionEntity;
import com.luncert.vibotech.compat.create.TransportMachineContraptionEntityRenderer;
import com.luncert.vibotech.content2.transportmachinecore.TransportMachineCoreEntity;
import com.luncert.vibotech.content2.transportmachinecore.TransportMachineCoreEntityRenderer;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class AllEntityTypes {

  public static final EntityEntry<TransportMachineCoreEntity> TRANSPORT_MACHINE_CORE =
      register("transport_machine_vehicle", TransportMachineCoreEntity::new, () -> TransportMachineCoreEntityRenderer::new, MobCategory.MISC,
          1, Integer.MAX_VALUE, false, true, TransportMachineCoreEntity::build).register();

  public static final EntityEntry<TransportMachineContraptionEntity> TRANSPORT_MACHINE_CONTRAPTION = contraption("transport_machine",
      TransportMachineContraptionEntity::new, () -> TransportMachineContraptionEntityRenderer::new, 5, Integer.MAX_VALUE, true).register();

  private static <T extends Entity> CreateEntityBuilder<T, ?> contraption(String name, EntityType.EntityFactory<T> factory,
                                                                          NonNullSupplier<NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T>>> renderer, int range,
                                                                          int updateFrequency, boolean sendVelocity) {
    return register(name, factory, renderer, MobCategory.MISC, range, updateFrequency, sendVelocity, true,
        AbstractContraptionEntity::build);
  }

  private static <T extends Entity> CreateEntityBuilder<T, ?> register(String name, EntityType.EntityFactory<T> factory,
                                                                       NonNullSupplier<NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T>>> renderer,
                                                                       MobCategory group, int range, int updateFrequency, boolean sendVelocity, boolean immuneToFire,
                                                                       NonNullConsumer<EntityType.Builder<T>> propertyBuilder) {
    String id = Lang.asId(name);
    return (CreateEntityBuilder<T, ?>) ViboTechMod.REGISTRATE
        .entity(id, factory, group)
        .properties(b -> b.setTrackingRange(range)
            .setUpdateInterval(updateFrequency)
            .setShouldReceiveVelocityUpdates(sendVelocity))
        .properties(propertyBuilder)
        .properties(b -> {
          if (immuneToFire)
            b.fireImmune();
        })
        .renderer(renderer);
  }

  public static void register() {}
}
