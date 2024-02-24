// package com.luncert.vibotech.content.camera;
//
// import java.util.HashMap;
// import java.util.Map;
// import net.minecraft.world.level.dimension.DimensionType;
// import net.minecraft.world.phys.Vec3;
//
// public class CameraManagerClient {
//
//   private static final Map<Integer, CameraEntity> CAMERAS = new HashMap<>();
//
//   private static int clientId = -1;
//
//   public static void syncCamera(int id, DimensionType dimensionType, Vec3 pos, float yaw, float pitch) {
//     CAMERAS.computeIfPresent(id, ($, camera) -> {
//       if (camera.level().dimensionType().equals(dimensionType)) {
//         camera.setPos(pos);
//         camera.setXRot(yaw);
//         camera.setYRot(pitch);
//       }
//       return camera;
//     });
//   }
// }
