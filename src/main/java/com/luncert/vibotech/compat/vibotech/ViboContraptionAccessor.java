package com.luncert.vibotech.compat.vibotech;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

import com.luncert.vibotech.compat.create.TransportMachineContraption;
import com.luncert.vibotech.content2.transportmachinecore.TransportMachineCoreEntity;
import dan200.computercraft.api.peripheral.IComputerAccess;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;

public final class ViboContraptionAccessor {

    public final Level world;
    public final TransportMachineCoreEntity transportMachineCoreEntity;
    public final TransportMachineContraption contraption;
    public final ViboContextResources resources = new ViboContextResources();

    public ViboContraptionAccessor(Level world,
                                   TransportMachineCoreEntity transportMachineCoreEntity,
                                   TransportMachineContraption contraption) {
        Objects.requireNonNull(world);
        Objects.requireNonNull(transportMachineCoreEntity);
        Objects.requireNonNull(contraption);
        this.world = world;
        this.transportMachineCoreEntity = transportMachineCoreEntity;
        this.contraption = contraption;
    }

    public Map<String, List<IViboComponent>> getComponents() {
      return transportMachineCoreEntity.getContraption()
          .map(TransportMachineContraption::getComponents)
          .orElse(Collections.emptyMap());
    }

    @SuppressWarnings("unchecked")
    public <T extends IViboComponent> Optional<T> findOne(String componentType) {
        List<IViboComponent> components = getComponents().get(componentType);
        if (components != null && !components.isEmpty()) {
            return Optional.of((T) components.get(0));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T extends IViboComponent> List<T> findAll(String componentType) {
        List<IViboComponent> components = getComponents().get(componentType);
        return components == null ? Collections.emptyList() : (List<T>) components;
    }

    public Optional<IViboComponent> getComponent(String componentName) {
        Pair<String, Integer> name = BaseViboComponent.parseName(componentName);

        List<IViboComponent> components = getComponents().get(name.getKey());
        if (components != null && components.size() > name.getValue()) {
            return Optional.of(components.get(name.getValue()));
        }

        return Optional.empty();
    }

    public BlockPos getComponentPos(String name) {
        StructureTemplate.StructureBlockInfo blockInfo = contraption.getComponentBlockInfo(name);
        if (blockInfo == null) {
            throw new IllegalArgumentException("block info missing for " + name);
        }

        return contraption.getWorldPos(blockInfo.pos());
    }

    public BlockState getComponentBlockState(String name) {
        StructureTemplate.StructureBlockInfo blockInfo = contraption.getComponentBlockInfo(name);
        if (blockInfo == null) {
            throw new IllegalArgumentException("block info missing for " + name);
        }

        return blockInfo.state();
    }

    public Optional<Direction> getAssembleStationFacing() {
        return transportMachineCoreEntity.getAssembleStationBlockEntity().map(
            blockEntity -> blockEntity.getBlockState().getValue(HORIZONTAL_FACING));
    }

    public Optional<BlockPos> getAssembleStationPosition() {
        return transportMachineCoreEntity.getAssembleStationBlockEntity().map(
            BlockEntity::getBlockPos);
    }

    public void queueEvent(String event, Object... args) {
        withComputer(c -> c.queueEvent(event, args));
    }

    private void withComputer(Consumer<IComputerAccess> action) {
        transportMachineCoreEntity.getAssembleStationBlockEntity().ifPresent(blockEntity -> {
            for (IComputerAccess computer : blockEntity.getPeripheral().getConnectedComputers()) {
                action.accept(computer);
            }
        });
    }
}
