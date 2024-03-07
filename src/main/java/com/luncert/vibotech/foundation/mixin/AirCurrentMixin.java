// package com.luncert.vibotech.foundation.mixin;
//
//
// import com.luncert.vibotech.compat.create.IFanProcessingHandler;
// import com.luncert.vibotech.index.AllCapabilities;
// import com.simibubi.create.content.kinetics.fan.AirCurrent;
// import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
// import java.util.ArrayList;
// import java.util.List;
// import net.minecraft.core.BlockPos;
// import net.minecraft.core.Direction;
// import net.minecraft.world.level.Level;
// import org.spongepowered.asm.mixin.Final;
// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.Shadow;
// import org.spongepowered.asm.mixin.injection.At;
// import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
// @Mixin(AirCurrent.class)
// public class AirCurrentMixin {
//
//   @Shadow
//   @Final
//   public IAirCurrentSource source;
//
//   @Shadow
//   public float maxDistance;
//
//   @Shadow
//   public boolean pushing;
//
//   @Shadow
//   public Direction direction;
//
//   private final List<IFanProcessingHandler> processingHandlers = new ArrayList<>();
//
//   @Inject(method = "rebuild()V", at = @At("TAIL"))
//   private void onRebuild(CallbackInfo ci) {
//     findAffectedProcessingHandlers();
//   }
//
//   @Inject(method = "tick()V", at = @At("TAIL"))
//   private void onTick(CallbackInfo ci) {
//     tickAffectedProcessingHandlers();
//   }
//
//   private void findAffectedProcessingHandlers() {
//     processingHandlers.clear();
//
//     Level world = source.getAirCurrentWorld();
//     BlockPos start = source.getAirCurrentPos();
//
//     int limit = (int) (maxDistance + .5f);
//     int searchStart = pushing ? 0 : limit;
//     int searchEnd = pushing ? limit : 0;
//     int searchStep = pushing ? 1 : -1;
//     for (int i = searchStart; i * searchStep <= searchEnd * searchStep; i += searchStep) {
//       BlockPos currentPos = start.relative(direction, i);
//       AllCapabilities.withFanProcessingHandler(world.getBlockEntity(currentPos), direction)
//           .ifPresent(processingHandlers::add);
//     }
//   }
//
//   private void tickAffectedProcessingHandlers() {
//     for (IFanProcessingHandler handler : processingHandlers) {
//       handler.tick(direction, source.getAirFlowDirection());
//     }
//   }
// }
