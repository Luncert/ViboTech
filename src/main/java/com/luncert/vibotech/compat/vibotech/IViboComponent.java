package com.luncert.vibotech.compat.vibotech;

import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

public interface IViboComponent {

    default void init(ViboContraptionAccessor accessor, String name) {
    }

    default void tickComponent(ViboComponentTickContext context) {
    }

    ViboComponentType getComponentType();

    @Nullable
    Tag writeNBT();

    void readNBT(Level world, Tag tag);
}
