package com.luncert.vibotech.foundation.mixin;

import com.luncert.vibotech.content.camera.CameraData;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

  @Inject(method = "handleRespawn(Lnet/minecraft/network/protocol/game/ClientboundRespawnPacket;)V", at = @At("HEAD"))
  private void onPlayerRespawn(CallbackInfo ci) {
    // release camera when the player respawns/switches dimensions.
    CameraData.restoreCamera();
  }
}
