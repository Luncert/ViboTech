package com.luncert.vibotech.content.camera;

import com.luncert.vibotech.index.AllPackets;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class CameraBlockEntity extends SmartBlockEntity {

  private static final Logger LOGGER = LogUtils.getLogger();

  public CameraBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> list) {
  }

  public void connect(Level world, Player player) {
    if (world.isClientSide) {
      AllPackets.getChannel().sendToServer(new PreConnectCameraPacket(worldPosition));
    }
  }
}
