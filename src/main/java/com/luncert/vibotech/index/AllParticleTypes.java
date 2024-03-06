package com.luncert.vibotech.index;

import com.simibubi.create.foundation.particle.ICustomParticleData;
import com.simibubi.create.foundation.utility.Lang;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public enum AllParticleTypes {

  ;

  private final ParticleEntry<?> entry;

  AllParticleTypes(Supplier typeFactory) {
    String name = Lang.asId(this.name());
    this.entry = new ParticleEntry(name, typeFactory);
  }


  public ParticleType<?> get() {
    return this.entry.object.get();
  }

  public String parameter() {
    return this.entry.name;
  }

  public static void register(IEventBus modEventBus) {
    AllParticleTypes.ParticleEntry.REGISTER.register(modEventBus);
  }

  @OnlyIn(Dist.CLIENT)
  public static void registerFactories(RegisterParticleProvidersEvent event) {
    for (AllParticleTypes type : values()) {
      type.entry.registerFactory(event);
    }
  }

  private static class ParticleEntry<D extends ParticleOptions> {
    private static final DeferredRegister<ParticleType<?>> REGISTER;
    private final String name;
    private final Supplier<? extends ICustomParticleData<D>> typeFactory;
    private final RegistryObject<ParticleType<D>> object;

    public ParticleEntry(String name, Supplier<? extends ICustomParticleData<D>> typeFactory) {
      this.name = name;
      this.typeFactory = typeFactory;
      this.object = REGISTER.register(name, () -> this.typeFactory.get().createType());
    }

    @OnlyIn(Dist.CLIENT)
    public void registerFactory(RegisterParticleProvidersEvent event) {
      this.typeFactory.get().register(this.object.get(), event);
    }

    static {
      REGISTER = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "create");
    }
  }
}
