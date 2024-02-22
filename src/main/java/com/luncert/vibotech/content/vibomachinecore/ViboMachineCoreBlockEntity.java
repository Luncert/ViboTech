package com.luncert.vibotech.content.vibomachinecore;

import com.luncert.vibotech.compat.create.EContraptionMovementMode;
import com.luncert.vibotech.exception.ViboMachineAssemblyException;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class ViboMachineCoreBlockEntity extends SmartBlockEntity {

  private static final Logger LOGGER = LogUtils.getLogger();

  public ViboMachineCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  // api

  public ViboMachineEntity assemble(EContraptionMovementMode mode) throws ViboMachineAssemblyException {
    BlockState blockState = getBlockState();

    ViboMachineEntity viboMachineEntity = new ViboMachineEntity(level, worldPosition, blockState);
    level.addFreshEntity(viboMachineEntity);
    if (!viboMachineEntity.assemble(mode, worldPosition)) {
      viboMachineEntity.discard();
      throw new ViboMachineAssemblyException("structure_not_found");
    }
    return viboMachineEntity;
  }

  // impl

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> list) {
  }
}
