package com.luncert.vibotech.compat.vibotech;

import com.luncert.vibotech.compat.computercraft.AssembleStationPeripheral;
import com.luncert.vibotech.compat.create.TransportMachineContraption;
import com.luncert.vibotech.content.AssembleStationBlockEntity;
import com.luncert.vibotech.content.TransportMachineEntity;
import dan200.computercraft.api.peripheral.IComputerAccess;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;

public final class ViboContraptionAccessor {

    public final Level world;
    public final AssembleStationPeripheral peripheral;
    public final AssembleStationBlockEntity station;
    public final TransportMachineEntity transportMachine;
    public final TransportMachineContraption contraption;
    public final ViboContextResources resources = new ViboContextResources();

    public ViboContraptionAccessor(Level world,
                                   AssembleStationPeripheral peripheral,
                                   AssembleStationBlockEntity station,
                                   TransportMachineEntity transportMachine,
                                   TransportMachineContraption contraption) {
        Objects.requireNonNull(world);
        Objects.requireNonNull(peripheral);
        Objects.requireNonNull(station);
        Objects.requireNonNull(transportMachine);
        Objects.requireNonNull(contraption);
        this.world = world;
        this.peripheral = peripheral;
        this.station = station;
        this.transportMachine = transportMachine;
        this.contraption = contraption;
    }

    @SuppressWarnings("unchecked")
    public <T extends IViboComponent> Optional<T> findOne(String componentType) {
        List<IViboComponent> components = station.getComponents().get(componentType);
        if (components != null && components.size() > 0) {
            return Optional.of((T) components.get(0));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T extends IViboComponent> List<T> findAll(String componentType) {
        List<IViboComponent> components = station.getComponents().get(componentType);
        return components == null ? Collections.emptyList() : (List<T>) components;
    }

    public Optional<IViboComponent> getComponent(String componentName) {
        Pair<String, Integer> name = BaseViboComponent.parseName(componentName);

        List<IViboComponent> components = station.getComponents().get(name.getKey());
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

    public void queueEvent(String event, Object... args) {
        withComputer(c -> c.queueEvent(event, args));
    }

    private void withComputer(Consumer<IComputerAccess> action) {
        for (IComputerAccess computer : peripheral.getConnectedComputers()) {
            action.accept(computer);
        }
    }
}
