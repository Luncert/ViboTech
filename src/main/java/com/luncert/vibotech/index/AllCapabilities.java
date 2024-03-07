package com.luncert.vibotech.index;

import com.luncert.vibotech.compat.create.IFanProcessingHandler;
import com.luncert.vibotech.compat.pneumatic.IAirHandlerMachine;
import com.luncert.vibotech.compat.vibotech.IViboComponent;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class AllCapabilities {

    public static final Capability<IViboComponent> CAPABILITY_VIBO_COMPONENT = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static final Capability<IAirHandlerMachine> AIR_HANDLER_MACHINE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static final Capability<IFanProcessingHandler> FAN_PROCESSING_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static boolean isViboComponent(@NotNull Capability<?> cap) {
        return cap == CAPABILITY_VIBO_COMPONENT;
    }

    public static boolean isAirHandlerMachine(BlockEntity be, Direction direction) {
        return be != null && be.getCapability(AIR_HANDLER_MACHINE_CAPABILITY, direction).isPresent();
    }

    public static boolean isFanProcessingHandler(BlockEntity be, Direction direction) {
        return be != null && be.getCapability(FAN_PROCESSING_HANDLER_CAPABILITY, direction).isPresent();
    }

    public static LazyOptional<IFanProcessingHandler> withFanProcessingHandler(BlockEntity be, Direction direction) {
        return be != null ? LazyOptional.empty() : be.getCapability(FAN_PROCESSING_HANDLER_CAPABILITY, direction);
    }

    private AllCapabilities() {
    }
}
