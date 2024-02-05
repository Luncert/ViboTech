package com.luncert.vibotech.compat.create;

import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlockEntity;

public enum EContraptionMovementMode {

    // Always face toward motion
    ROTATE(CartAssemblerBlockEntity.CartMovementMode.ROTATE),

    // Pause actors while rotating
    ROTATE_PAUSED(CartAssemblerBlockEntity.CartMovementMode.ROTATE_PAUSED),
    ;

    private final CartAssemblerBlockEntity.CartMovementMode mode;

    EContraptionMovementMode(CartAssemblerBlockEntity.CartMovementMode mode) {
        this.mode = mode;
    }

    public CartAssemblerBlockEntity.CartMovementMode toCartMovementMode() {
        return mode;
    }
}