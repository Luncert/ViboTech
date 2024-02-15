package com.luncert.vibotech.compat.vibotech;

import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyManager;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.IEnergyStorage;
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
            String componentType = componentName.substring(0, i);
            int componentId = Integer.parseInt(componentName.substring(i + 1));
            return Pair.of(componentType, componentId);
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid component name");
        }
    }

    protected Optional<IEnergyStorage> getEnergyStorage() {
        return Optional.ofNullable(PortableEnergyManager.get(accessor.contraption));
    }

    protected BlockPos getWorldPos() {
        return accessor.getComponentPos(name);
    }
}
