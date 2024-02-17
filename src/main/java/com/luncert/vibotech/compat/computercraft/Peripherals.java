package com.luncert.vibotech.compat.computercraft;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

import com.luncert.vibotech.content2.assemblestation.AssembleStationBlockEntity;
import com.luncert.vibotech.content2.camera.CameraBlockEntity;
import net.minecraftforge.common.capabilities.Capability;

public class Peripherals {

    public static boolean isPeripheral(Capability<?> cap) {
        return cap == CAPABILITY_PERIPHERAL;
    }

    public static AssembleStationPeripheral createAssembleStationPeripheral(AssembleStationBlockEntity be) {
        return new AssembleStationPeripheral("assemble_station", be);
    }

    public static CameraPeripheral createCameraPeripheral(CameraBlockEntity be) {
        return new CameraPeripheral("camera", be);
    }
}
