package com.luncert.vibotech.compat.vibotech;

import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyManager;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

public abstract class BaseViboComponent implements IViboComponent {

    protected ViboContraptionAccessor accessor;
    protected String name;

    @Override
    public void init(ViboContraptionAccessor accessor, String name) {
        this.accessor = accessor;
        this.name = name;
    }

    @Override
    public Tag writeNBT() {
        return null;
    }

    @Override
    public void readNBT(Level world, Tag tag) {
    }

    public static Pair<String, Integer> parseName(String componentName) {
        try {
            int i = componentName.lastIndexOf('-');
            if (i > 0) {
                String componentType = componentName.substring(0, i);
                int componentId = Integer.parseInt(componentName.substring(i + 1));
                return Pair.of(componentType, componentId);
            }
            // singleton component's name doesn't contain id
            return Pair.of(componentName, 0);
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid component name");
        }
    }

    protected Optional<IEnergyStorage> getEnergyAccessor() {
        return Optional.ofNullable(PortableEnergyManager.get(accessor.contraption));
    }

    protected Optional<IItemHandler> getStorageAccessor() {
        return Optional.ofNullable(accessor.contraption.getSharedInventory());
    }

    protected Optional<IFluidHandler> getFluidAccessor() {
        return Optional.ofNullable(accessor.contraption.getSharedFluidTanks());
    }

    protected BlockPos getWorldPos() {
        return accessor.getComponentWorldPos(name);
    }
}
