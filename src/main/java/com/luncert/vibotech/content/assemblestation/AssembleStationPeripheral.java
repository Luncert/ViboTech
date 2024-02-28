package com.luncert.vibotech.content.assemblestation;

import com.luncert.vibotech.common.Utils;
import com.luncert.vibotech.compat.create.EContraptionMovementMode;
import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.IViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.luncert.vibotech.exception.ViboMachineAssemblyException;
import com.mojang.logging.LogUtils;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class AssembleStationPeripheral implements IPeripheral {

    private static final Logger LOGGER = LogUtils.getLogger();

    protected String type;
    protected AssembleStationBlockEntity assembleStationBlockEntity;

    protected final List<IComputerAccess> connected = new ArrayList<>();

    public AssembleStationPeripheral(String type, AssembleStationBlockEntity blockEntity) {
        this.type = type;
        this.assembleStationBlockEntity = blockEntity;
    }

    public List<IComputerAccess> getConnectedComputers() {
        return connected;
    }

    @Override
    public Object getTarget() {
        return assembleStationBlockEntity;
    }

    @NotNull
    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral == this;
    }

    @Override
    public void attach(IComputerAccess computer) {
        connected.add(computer);
    }

    @Override
    public void detach(IComputerAccess computer) {
        connected.remove(computer);
    }

    // api

    @LuaFunction(mainThread = true)
    public final void assemble(String rotationMode) throws LuaException {
        EContraptionMovementMode mode;
        try {
            mode = EContraptionMovementMode.valueOf(rotationMode.toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new LuaException("Invalid argument, must be one of " + Arrays.toString(EContraptionMovementMode.values()));
        }

        try {
            assembleStationBlockEntity.assemble(mode);
        } catch (ViboMachineAssemblyException e) {
            throw new LuaException("failed to assemble structure: " + e.getMessage());
        }
    }

    @LuaFunction(mainThread = true)
    public final void dissemble() throws LuaException {
        try {
            assembleStationBlockEntity.dissemble();
        } catch (ViboMachineAssemblyException e) {
            throw new LuaException("failed to dissemble structure: " + e.getMessage());
        }
    }

    @LuaFunction
    public final List<String> getComponents() {
        Map<ViboComponentType, List<IViboComponent>> components = assembleStationBlockEntity.getComponents();
        List<String> result = new ArrayList<>(components.size());
        components.forEach((t, viboComponents) -> {
            if (t.isInternal()) {
                return;
            }
            if (t.isSingleton()) {
                result.add(t.getName());
            } else {
                for (int i = 0; i < viboComponents.size(); i++) {
                    IViboComponent c = viboComponents.get(i);
                    result.add(c.getComponentType().getName() + "-" + i);
                }
            }
        });
        return result;
    }

    @LuaFunction
    public final Map<String, ILuaFunction> getComponent(String componentName) {
        Pair<String, Integer> name = BaseViboComponent.parseName(componentName);
        ViboComponentType type = ViboComponentType.valueOf(name.getKey());
        if (type.isInternal()) {
            return Collections.emptyMap();
        }

        List<IViboComponent> components = assembleStationBlockEntity.getComponents().get(type);
        int componentId = name.getValue();
        if (components != null && components.size() > componentId) {
            return getLuaFunctions(components.get(componentId));
        }

        return Collections.emptyMap();
    }

    private Map<String, ILuaFunction> getLuaFunctions(IViboComponent c) {
        Map<String, ILuaFunction> functions = new HashMap<>();
        for (Method method : c.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(LuaFunction.class)) {
                String name = method.getDeclaringClass().getName() + "." + method.getName();
                int modifiers = method.getModifiers();

                if(!Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)) {
                    LOGGER.warn("Lua Method {} should be final.", name);
                }

                if(!Modifier.isPublic(modifiers)) {
                    LOGGER.error( "Lua Method {} should be a public method.", name );
                    return Collections.emptyMap();
                }

                functions.put(method.getName(), generate(c, method));
            }
        }

        return functions;
    }

    private ILuaFunction generate(IViboComponent c, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return arguments -> {
            Object[] args = parseArguments(arguments, parameterTypes);
            try {
                Object result = method.invoke(c, args);
                if (result instanceof MethodResult r) {
                    return r;
                }
                return MethodResult.of(result);
            } catch (Exception e) {
                LOGGER.error("failed to call component api", e);
                throw new LuaException(Utils.findCauseUsingPlainJava(e).getMessage());
            }
        };
    }

    @SuppressWarnings("unchecked")
    private Object[] parseArguments(IArguments arguments, Class<?>[] parameterTypes) throws LuaException {
        if (arguments.count() != parameterTypes.length) {
            throw new LuaException(parameterTypes.length + " arguments expected, received " + arguments.count());
        }

        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            Object value;
            if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
                value = arguments.getBoolean(i);
            } else if (byte.class.isAssignableFrom(type) || Byte.class.isAssignableFrom(type)) {
                value = arguments.getBytes(i);
            } else if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
                value = arguments.getInt(i);
            } else if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
                value = arguments.getFiniteDouble(i);
            } else if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
                value = arguments.getLong(i);
            } else if (Map.class.isAssignableFrom(type)) {
                value = arguments.getTable(i);
            } else if (String.class.isAssignableFrom(type)) {
                value = arguments.getString(i);
            } else if (Enum.class.isAssignableFrom(type)) {
                value = arguments.getEnum(i, (Class<? extends Enum>) type);
            } else {
                throw new LuaException("invalid argument " + i + ", " + type + " expected");
            }
            args[i] = value;
        }
        return args;
    }
}
