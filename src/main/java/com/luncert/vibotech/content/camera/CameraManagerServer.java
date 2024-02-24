// package com.luncert.vibotech.content.camera;
//
// import com.luncert.vibotech.index.AllPackets;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Optional;
// import net.minecraft.world.level.Level;
// import net.minecraft.world.phys.Vec3;
// import net.minecraftforge.network.PacketDistributor;
//
// public class CameraManagerServer {
//
//   private static final Map<Integer, CameraEntity> CAMERAS = new HashMap<>();
//
//   private static int id = 0;
//
//   public static int addCamera(Level world, Vec3 pos, float yaw, float pitch) {
//     int newId = id++;
//     CAMERAS.put(id, new CameraEntity(world, pos, yaw, pitch));
//     return newId;
//   }
//
//   public static void removeCamera(int id) {
//     CAMERAS.remove(id);
//   }
//
//   public static Optional<CameraEntity> getCamera(int id) {
//     return Optional.of(CAMERAS.get(id));
//   }
//
//   public static void updateCamera(int id, Level world, Vec3 pos, float yaw, float pitch) {
//     getCamera(id).ifPresent(camera -> {
//       AllPackets.getChannel().send(PacketDistributor.ALL.noArg(), new CameraSyncPacket());
//     });
//   }
// }
