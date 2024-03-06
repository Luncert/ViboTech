package com.luncert.vibotech.compat.pneumatic;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractAirHandlingBlockEntity extends SmartBlockEntity {

  private final Map<IAirHandlerMachine, List<Direction>> airHandlerMap = new IdentityHashMap<>();

  public AbstractAirHandlingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  // called clientside when a PacketUpdatePressureBlock is received
  // this ensures the BE can tick this air handler for air leak sound and particle purposes
  public void initializeHullAirHandlerClient(Direction dir, IAirHandlerMachine handler) {
    airHandlerMap.clear();
    List<Direction> l = Collections.singletonList(dir);
    airHandlerMap.put(handler, l);
    handler.setConnectedFaces(l);
  }
}
