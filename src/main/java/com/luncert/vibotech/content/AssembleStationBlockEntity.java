package com.luncert.vibotech.content;

import com.luncert.vibotech.exception.TransportMachineAssemblyException;
import com.luncert.vibotech.index.AllBlocks;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class AssembleStationBlockEntity extends SmartBlockEntity {

  private static final Logger LOGGER = LogUtils.getLogger();

  private UUID transportMachineId;
  private TransportMachineEntity transportMachine;

  private static final int assemblyCooldown = 8;
  private int ticksSinceLastUpdate;
  protected AssemblyException lastException;

  public AssembleStationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
    ticksSinceLastUpdate = assemblyCooldown;
  }

  @Override
  public void tick() {
    super.tick();

    if (level.isClientSide) {
      return;
    }

    if (ticksSinceLastUpdate < assemblyCooldown) {
      ticksSinceLastUpdate++;
    }

    try {
      tryAssemble();
    } catch (TransportMachineAssemblyException e) {
      throw new RuntimeException(e);
    }

    // bind & unbind vehicle entity to station

    if (level instanceof ServerLevel world && transportMachine == null && transportMachineId != null) {
      if (world.getEntity(transportMachineId) instanceof TransportMachineEntity transportMachine) {
        this.transportMachine = transportMachine;
        transportMachine.bindStation(this);
      }
    }

    if (transportMachine != null && transportMachine.isRemoved()) {
      transportMachineId = null;
      transportMachine = null;
    }
  }

  public void tryAssemble() throws TransportMachineAssemblyException {
    resetTicksSinceLastUpdate();

    BlockState state = level.getBlockState(worldPosition);
    if (!AllBlocks.ASSEMBLE_STATION.has(state)) {
      return;
    }
    AssembleStationBlock block = (AssembleStationBlock) state.getBlock();
    AssembleStationBlock.AssembleStationAction action = AssembleStationBlock.getAction(state);
    if (isAssembled()) {
      if (action.shouldDisassemble()) {
        dissemble(level, worldPosition);
      }
    } else {
      if (action.shouldAssemble()) {
        assemble(level, worldPosition);
      }
    }
  }

  public boolean isAssembled() {
    return transportMachine != null;
  }

  public void assemble() throws TransportMachineAssemblyException {

  }

  public void assemble(Level world, BlockPos pos) throws TransportMachineAssemblyException {
    if (transportMachine != null) {
      throw new TransportMachineAssemblyException("transport_machine_assembled");
    }

    TransportMachineEntity transportMachine = new TransportMachineEntity(world, pos, getBlockState());
    world.addFreshEntity(transportMachine);
    if (!transportMachine.assemble(worldPosition)) {
      transportMachine.discard();
      throw new TransportMachineAssemblyException("structure_not_found");
    }
    this.transportMachine = transportMachine;
    this.transportMachineId = transportMachine.getUUID();
  }

  public void dissemble(Level world, BlockPos pos) throws TransportMachineAssemblyException {
    checkContraptionStatus();

    // Vec3 blockPos = Vec3.atCenterOf(getBlockPos()).add(0, -0.5, 0);
    // if (!blockPos.equals(entity.position())) {
    //     throw new AircraftAssemblyException("not_dissemble_at_station");
    // }

    transportMachine.dissemble();
    transportMachine = null;
  }

  private void checkContraptionStatus() throws TransportMachineAssemblyException {
    if (transportMachine == null) {
      throw new TransportMachineAssemblyException("aircraft_dissembled");
    }
  }


  public void resetTicksSinceLastUpdate() {
    ticksSinceLastUpdate = 0;
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> list) {
  }

  @Override
  protected void write(CompoundTag compound, boolean clientPacket) {
    super.write(compound, clientPacket);

    if (!clientPacket) {
      if (transportMachineId != null) {
        compound.putString("transport_machine", transportMachineId.toString());
      }
    }
  }

  @Override
  protected void read(CompoundTag compound, boolean clientPacket) {
    super.read(compound, clientPacket);

    if (!clientPacket) {
      transportMachineId = UUID.fromString(compound.getString("transport_machine"));
    }
  }
}
