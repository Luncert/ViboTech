// package com.luncert.vibotech.compat.create;
//
// import com.luncert.vibotech.content.AssembleStationBlockEntity;
//
// public enum EContraptionMovementMode {
//
//     // Always face toward motion
//     ROTATE(AssembleStationBlockEntity.CartMovementMode.ROTATE),
//
//     // Pause actors while rotating
//     ROTATE_PAUSED(AssembleStationBlockEntity.CartMovementMode.ROTATE_PAUSED),
//
//     // Lock rotation
//     ROTATION_LOCKED(AssembleStationBlockEntity.CartMovementMode.ROTATION_LOCKED),
//     ;
//
//     private final AssembleStationBlockEntity.CartMovementMode mode;
//
//     EContraptionMovementMode(CartAssemblerTileEntity.CartMovementMode mode) {
//         this.mode = mode;
//     }
//
//     public CartAssemblerTileEntity.CartMovementMode toCartMovementMode() {
//         return mode;
//     }
// }