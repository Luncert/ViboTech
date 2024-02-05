package com.luncert.vibotech.index;

import com.luncert.vibotech.compat.vibotech.IViboComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.jetbrains.annotations.NotNull;

public class AllCapabilities {

    public static final Capability<IViboComponent> CAPABILITY_VIBO_COMPONENT = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static boolean isViboComponent(@NotNull Capability<?> cap) {
        return cap == CAPABILITY_VIBO_COMPONENT;
    }

    private AllCapabilities() {
    }
}
