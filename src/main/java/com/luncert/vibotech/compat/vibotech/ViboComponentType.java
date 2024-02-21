package com.luncert.vibotech.compat.vibotech;

import com.luncert.vibotech.content.geoscanner.GeoScannerComponent;
import com.luncert.vibotech.content.thruster.ThrusterComponent;
import com.luncert.vibotech.content.transportmachinecore.ViboMachineCoreComponent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ViboComponentType {

    private static final Map<String, ViboComponentType> NAME_MAPPINGS = new HashMap<>();

    public static final ViboComponentType CORE = ViboComponentType.register("core", ViboMachineCoreComponent.class, true);
    public static final ViboComponentType ENERGY_ACCESSOR = ViboComponentType.register("energy_accessor", EnergyAccessorComponent.class, true);
    public static final ViboComponentType STORAGE_ACCESSOR = ViboComponentType.register("storage_accessor", StorageAccessorComponent.class, true);
    public static final ViboComponentType FLUID_ACCESSOR = ViboComponentType.register("fluid_accessor", FluidAccessorComponent.class, true);

    public static final ViboComponentType GEO_SCANNER = ViboComponentType.register("geo_scanner", GeoScannerComponent.class);
    public static final ViboComponentType THRUSTER = ViboComponentType.register("thruster", ThrusterComponent.class);

    public static ViboComponentType register(String name, Class<? extends IViboComponent> type) {
        return register(name, type, false);
    }

    public static ViboComponentType register(String name, Class<? extends IViboComponent> type, boolean singleton) {
        if (NAME_MAPPINGS.containsKey(name)) {
            throw new IllegalArgumentException("cannot register duplicated component type: " + name + " " + type);
        }
        ViboComponentType componentType = new ViboComponentType(name, singleton, type);
        NAME_MAPPINGS.put(name, componentType);
        return componentType;
    }

    public static ViboComponentType valueOf(String name) {
        return NAME_MAPPINGS.get(name);
    }

    public static IViboComponent createComponent(ViboComponentType componentType) {
        Class<? extends IViboComponent> type = componentType.type;
        if (type == null) {
            throw new IllegalArgumentException("invalid type name: " + componentType);
        }

        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final String name;
    private final boolean singleton;
    private final Class<? extends IViboComponent> type;

    private ViboComponentType(String name, boolean singleton, Class<? extends IViboComponent> type) {
        this.name = name;
        this.singleton = singleton;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public boolean isSingleton() {
        return singleton;
    }
}
