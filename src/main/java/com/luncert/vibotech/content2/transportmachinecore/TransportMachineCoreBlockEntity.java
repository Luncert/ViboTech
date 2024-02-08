package com.luncert.vibotech.content2.transportmachinecore;

import com.luncert.vibotech.compat.create.EContraptionMovementMode;
import com.luncert.vibotech.exception.TransportMachineAssemblyException;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class TransportMachineCoreBlockEntity extends SmartBlockEntity {

  private static final Logger LOGGER = LogUtils.getLogger();

  public TransportMachineCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  // api

  public TransportMachineCoreEntity assemble(EContraptionMovementMode mode) throws TransportMachineAssemblyException {
    BlockState blockState = getBlockState();

    TransportMachineCoreEntity transportMachineCoreEntity = new TransportMachineCoreEntity(level, worldPosition, blockState);
    level.addFreshEntity(transportMachineCoreEntity);
    if (!transportMachineCoreEntity.assemble(mode, worldPosition)) {
      transportMachineCoreEntity.discard();
      throw new TransportMachineAssemblyException("structure_not_found");
    }
    return transportMachineCoreEntity;
  }

  // impl

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> list) {
  }
}
