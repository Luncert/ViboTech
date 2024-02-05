package com.luncert.vibotech.compat.vibotech;

import com.mojang.logging.LogUtils;
import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import java.util.Arrays;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class ViboApiCallback implements ILuaCallback {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final int executionId;
    private final MethodResult callbackHook;

    public static MethodResult hook(int executionId, String respEventName) {
        return new ViboApiCallback(executionId, respEventName).callbackHook;
    }

    private ViboApiCallback(int executionId, String respEventName) {
        this.executionId = executionId;
        callbackHook = MethodResult.pullEvent(respEventName, this);
    }

    @NotNull
    @Override
    public MethodResult resume(Object[] response) throws LuaException {
        // 0 is event name
        if (response.length == 3
                && response[1] instanceof Number
                && response[2] != null
                && response[2] instanceof Map<?,?> executionResult) {
            LOGGER.info("received response for {}: {}", executionId, Arrays.toString(response));
            int responseId = ((Number) response[1]).intValue();
            if (responseId == this.executionId) {
                return MethodResult.of(executionResult.values());
            }
        } else {
            LOGGER.info("discard response for {}: {}", executionId, Arrays.toString(response));
        }

        return callbackHook;
    }
}
