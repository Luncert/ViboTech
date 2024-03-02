package com.luncert.vibotech.content.camera;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

import com.luncert.vibotech.content.camera.packet.ServerCreateCameraPacket;
import com.luncert.vibotech.index.AllPackets;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

public class CameraInteractionBehaviour extends MovingInteractionBehaviour {

  private static final Logger LOGGER = LogUtils.getLogger();

  @Override
  public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
    Direction blockDirection = contraptionEntity.getContraption().getBlocks().get(localPos).state().getValue(HORIZONTAL_FACING);
    AllPackets.getChannel().sendToServer(new ServerCreateCameraPacket(
        contraptionEntity.getVehicle().blockPosition().offset(localPos), blockDirection.toYRot()));
    return true;
  }
}
