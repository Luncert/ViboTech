package com.luncert.vibotech.content;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

import com.luncert.vibotech.compat.computercraft.AssembleStationPeripheral;
import com.luncert.vibotech.compat.computercraft.Peripherals;
import com.luncert.vibotech.compat.create.EContraptionMovementMode;
import com.luncert.vibotech.compat.create.TransportMachineContraption;
import com.luncert.vibotech.compat.vibotech.IViboComponent;
import com.luncert.vibotech.exception.TransportMachineAssemblyException;
import com.luncert.vibotech.index.AllBlocks;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.AssemblyException;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.slf4j.Logger;

public class AssembleStationBlockEntity extends SmartBlockEntity {

  private static final Logger LOGGER = LogUtils.getLogger();

  private AssembleStationPeripheral peripheral;
  private UUID transportMachineId;
  private TransportMachineEntity transportMachine;
  private boolean assembled;
  private boolean controlledByStation;

  private static final int assemblyCooldown = 8;
  private int ticksSinceLastUpdate;
  protected AssemblyException lastException;

  public AssembleStationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
    ticksSinceLastUpdate = assemblyCooldown;

    peripheral = Peripherals.createAssembleStationPeripheral(this);
    setLazyTickRate(20);
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

    // bind & unbind vehicle entity to station

    if (level instanceof ServerLevel world && transportMachine == null && transportMachineId != null) {
      if (world.getEntity(transportMachineId) instanceof TransportMachineEntity transportMachineEntity) {
        this.transportMachine = transportMachineEntity;
        transportMachineEntity.bindStation(this);
      }
    }

    if (transportMachine != null && transportMachine.isRemoved()) {
      transportMachineId = null;
      transportMachine = null;
    }

    try {
      tryAssemble();
    } catch (TransportMachineAssemblyException e) {
      LOGGER.error("exception", e);
    }
  }

  // api

  public boolean isAssembled() {
    return assembled;
  }

  public AssembleStationPeripheral getPeripheral() {
    return peripheral;
  }

  public Map<String, List<IViboComponent>> getComponents() {
    if (transportMachine == null) {
      return Collections.emptyMap();
    }
    return transportMachine.getContraption().map(TransportMachineContraption::getComponents).orElse(Collections.emptyMap());
  }

  public Vec3 getStationPosition() {
    return new Vec3(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
  }

  public Direction getStationFacing() {
    return getBlockState().getValue(HORIZONTAL_FACING);
  }

  public void tryAssemble() throws TransportMachineAssemblyException {
    resetTicksSinceLastUpdate();

    BlockState state = level.getBlockState(worldPosition);
    if (!AllBlocks.ASSEMBLE_STATION.has(state)) {
      return;
    }
    AssembleStationBlock block = (AssembleStationBlock) state.getBlock();
    AssembleStationBlock.AssembleStationAction action = AssembleStationBlock.getAction(state);
    if (assembled) {
      if (controlledByStation && action.shouldDisassemble()) {
        dissemble();
        controlledByStation = false;
      }
    } else {
      if (action.shouldAssemble()) {
        assemble(EContraptionMovementMode.ROTATE);
        controlledByStation = true;
      }
    }
  }

  public void assemble(EContraptionMovementMode mode) throws TransportMachineAssemblyException {
    if (transportMachine != null) {
      throw new TransportMachineAssemblyException("transport_machine_assembled");
    }

    TransportMachineEntity transportMachine = new TransportMachineEntity(level, worldPosition, getBlockState());
    level.addFreshEntity(transportMachine);
    if (!transportMachine.assemble(mode, worldPosition)) {
      transportMachine.discard();
      throw new TransportMachineAssemblyException("structure_not_found");
    }
    this.transportMachine = transportMachine;
    this.transportMachineId = transportMachine.getUUID();
    assembled = true;
  }

  public void dissemble() throws TransportMachineAssemblyException {
    checkContraptionStatus();

    Vec3 blockPos = Vec3.atCenterOf(getBlockPos()).add(0, -0.5, 0);
    if (!blockPos.equals(transportMachine.position())) {
        throw new TransportMachineAssemblyException("not_dissemble_at_station");
    }

    transportMachine.dissemble();
    transportMachine = null;
    assembled = false;
  }

  private void checkContraptionStatus() throws TransportMachineAssemblyException {
    if (transportMachine == null) {
      throw new TransportMachineAssemblyException("contraption_dissembled");
    }
  }


  public void resetTicksSinceLastUpdate() {
    ticksSinceLastUpdate = 0;
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> list) {
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if(Peripherals.isPeripheral(cap)) {
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
  protected void write(CompoundTag compound, boolean clientPacket) {
    super.write(compound, clientPacket);

    if (!clientPacket) {
      write(compound);
    }
  }

  void write(CompoundTag compound) {
    if (transportMachineId != null) {
      compound.putString("transport_machine", transportMachineId.toString());
    }
    compound.putBoolean("controlled_by_station", controlledByStation);
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
    transportMachineId = UUID.fromString(compound.getString("transport_machine"));
    controlledByStation = compound.getBoolean("controlled_by_station");
    assembled = compound.getBoolean("assembled");
  }
}
