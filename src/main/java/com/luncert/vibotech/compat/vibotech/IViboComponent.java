package com.luncert.vibotech.compat.vibotech;

import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

public interface IViboComponent {

    default void init(ViboContraptionAccessor aircraftAccessor, String name) {
    }

    default void tickComponent() {
    }

    ViboComponentType getComponentType();

    @Nullable
    Tag writeNBT();

    void readNBT(Level world, Tag tag);
}
