package com.luncert.vibotech.compat.vibotech;

import com.luncert.vibotech.compat.vibotech.component.EnergyAccessorComponent;
import com.luncert.vibotech.compat.vibotech.component.FinalizeComponent;
import com.luncert.vibotech.compat.vibotech.component.FluidAccessorComponent;
import com.luncert.vibotech.compat.vibotech.component.StorageAccessorComponent;
import com.luncert.vibotech.content.geoscanner.GeoScannerComponent;
import com.luncert.vibotech.content.photovoltaic.PhotovoltaicPanelComponent;
import com.luncert.vibotech.content.thruster.ThrusterComponent;
import com.luncert.vibotech.content.vibomachinecontrolseat.ControlSeatComponent;
import com.luncert.vibotech.content.vibomachinecore.ViboMachineCoreComponent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ViboComponentType {

    private static final Map<String, ViboComponentType> NAME_MAPPINGS = new HashMap<>();

    public static final ViboComponentType CORE = ViboComponentType.register("core", ViboMachineCoreComponent.class, true);
    public static final ViboComponentType FINALIZE = ViboComponentType.register("finalize", FinalizeComponent.class, true, true);
    public static final ViboComponentType ENERGY_ACCESSOR = ViboComponentType.register("energy_accessor", EnergyAccessorComponent.class, true);
    public static final ViboComponentType STORAGE_ACCESSOR = ViboComponentType.register("storage_accessor", StorageAccessorComponent.class, true);
    public static final ViboComponentType FLUID_ACCESSOR = ViboComponentType.register("fluid_accessor", FluidAccessorComponent.class, true);

    public static final ViboComponentType GEO_SCANNER = ViboComponentType.register("geo_scanner", GeoScannerComponent.class);
    public static final ViboComponentType THRUSTER = ViboComponentType.register("thruster", ThrusterComponent.class);
    public static final ViboComponentType PHOTOVOLTAIC_PANEL = ViboComponentType.register("photovoltaic_panel", PhotovoltaicPanelComponent.class);
    public static final ViboComponentType CONTROL_SEAT = ViboComponentType.register("control_seat", ControlSeatComponent.class);

    public static ViboComponentType register(String name, Class<? extends IViboComponent> type) {
        return register(name, type, false, false);
    }

    public static ViboComponentType register(String name, Class<? extends IViboComponent> type, boolean singleton) {
        return register(name, type, singleton, false);
    }

    public static ViboComponentType register(String name, Class<? extends IViboComponent> type, boolean singleton, boolean internal) {
        if (NAME_MAPPINGS.containsKey(name)) {
            throw new IllegalArgumentException("cannot register duplicated component type: " + name + " " + type);
        }
        ViboComponentType componentType = new ViboComponentType(name, singleton, internal, type);
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
    private final boolean internal;
    private final Class<? extends IViboComponent> type;

    private ViboComponentType(String name, boolean singleton, boolean internal, Class<? extends IViboComponent> type) {
        this.name = name;
        this.singleton = singleton;
        this.internal = internal;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<? extends IViboComponent> getType() {
        return type;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public boolean isInternal() {
        return internal;
    }

    @Override
    public String toString() {
        return "ViboComponentType{" +
            "name='" + name + '\'' +
            ", singleton=" + singleton +
            ", internal=" + internal +
            ", type=" + type +
            '}';
    }
}
