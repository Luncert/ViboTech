package com.luncert.vibotech.compat.vibotech;

import com.luncert.vibotech.content.TransportMachineComponent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ViboComponentType {

    private static final Map<String, Class<? extends IViboComponent>> TYPE_MAPPINGS = new HashMap<>();
    public static final ViboComponentType TRANSPORT_MACHINE = ViboComponentType.register("transport_machine", TransportMachineComponent.class);

    public static ViboComponentType register(String name, Class<? extends IViboComponent> type) {
        if (TYPE_MAPPINGS.containsKey(name)) {
            throw new IllegalArgumentException("cannot register duplicated component type: " + name + " " + type);
        }
        TYPE_MAPPINGS.put(name, type);
        return new ViboComponentType(name);
    }

    public static IViboComponent createComponent(String name) {
        Class<? extends IViboComponent> type = TYPE_MAPPINGS.get(name);
        if (type == null) {
            throw new IllegalArgumentException("invalid type name: " + name);
        }

        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final String name;

    private ViboComponentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
