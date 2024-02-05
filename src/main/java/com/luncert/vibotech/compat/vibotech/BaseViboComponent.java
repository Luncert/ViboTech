package com.luncert.vibotech.compat.vibotech;

import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
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
}
