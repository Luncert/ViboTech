package com.luncert.vibotech.content.assemblestation;

import com.luncert.vibotech.compat.computercraft.Peripherals;
import com.luncert.vibotech.compat.create.EContraptionMovementMode;
import com.luncert.vibotech.compat.create.ViboMachineContraption;
import com.luncert.vibotech.compat.vibotech.IViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.luncert.vibotech.content.vibomachinecore.ViboMachineCoreBlockEntity;
import com.luncert.vibotech.content.vibomachinecore.ViboMachineEntity;
import com.luncert.vibotech.exception.ViboMachineAssemblyException;
import com.luncert.vibotech.index.AllBlocks;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class AssembleStationBlockEntity extends SmartBlockEntity {

  //private static final Logger LOGGER = LogUtils.getLogger();

  private AssembleStationPeripheral peripheral;
  private ViboMachineEntity viboMachineEntity;
  private UUID viboMachineCoreEntityId;
  private boolean assembled;

  public AssembleStationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
    peripheral = Peripherals.createAssembleStationPeripheral(this);
    setLazyTickRate(20);
  }

  // api

  public AssembleStationPeripheral getPeripheral() {
    return peripheral;
  }

  public boolean isAssembled() {
    return assembled;
  }

  public void assemble(EContraptionMovementMode mode) throws ViboMachineAssemblyException {
    // check above block
    BlockState blockState = level.getBlockState(worldPosition.above());

    if (!AllBlocks.VIBO_MACHINE_CORE.has(blockState)) {
      return;
    }

    if (level.getBlockEntity(worldPosition.above()) instanceof ViboMachineCoreBlockEntity blockEntity) {
      viboMachineEntity = blockEntity.assemble(mode);
      viboMachineEntity.bindAssembleStation(this);
      viboMachineCoreEntityId = viboMachineEntity.getUUID();
      assembled = true;
    }
  }

  public void dissemble() throws ViboMachineAssemblyException {
    if (viboMachineEntity != null) {
      viboMachineEntity.dissemble();
      viboMachineEntity = null;
      viboMachineCoreEntityId = null;
    }
    assembled = false;
  }

  public Map<ViboComponentType, List<IViboComponent>> getComponents() {
    if (viboMachineEntity == null) {
      return Collections.emptyMap();
    }
    return viboMachineEntity.getContraption().map(ViboMachineContraption::getComponents).orElse(Collections.emptyMap());
  }

  // impl

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> list) {
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (Peripherals.isPeripheral(cap)) {
      return LazyOptional.of(() -> peripheral).cast();
    }
    return super.getCapability(cap, side);
  }

  @Override
  public void remove() {
    super.remove();
    peripheral = null;
  }

  @Override
  public void tick() {
    super.tick();

    if (level.isClientSide) {
      return;
    }

    // bind & unbind vehicle entity to station

    if (level instanceof ServerLevel world && viboMachineEntity == null && viboMachineCoreEntityId != null) {
      if (world.getEntity(viboMachineCoreEntityId) instanceof ViboMachineEntity entity) {
        viboMachineEntity = entity;
        entity.bindAssembleStation(this);
      }
    }

    if (viboMachineEntity != null && viboMachineEntity.isRemoved()) {
      viboMachineCoreEntityId = null;
      viboMachineEntity = null;
    }
  }

  @Override
  protected void write(CompoundTag compound, boolean clientPacket) {
    super.write(compound, clientPacket);

    if (!clientPacket) {
      write(compound);
    }
  }

  void write(CompoundTag compound) {
    if (viboMachineCoreEntityId != null) {
      compound.putString("vibo_machine_reference", viboMachineCoreEntityId.toString());
    }
    compound.putBoolean("assembled", assembled);
  }

  @Override
  protected void read(CompoundTag compound, boolean clientPacket) {
    super.read(compound, clientPacket);

    if (!clientPacket) {
      read(compound);
    }
  }

  void read(CompoundTag compound) {
    if (compound.contains("vibo_machine_reference")) {
      viboMachineCoreEntityId = UUID.fromString(compound.getString("vibo_machine_reference"));
    }
    assembled = compound.getBoolean("assembled");
  }
}
