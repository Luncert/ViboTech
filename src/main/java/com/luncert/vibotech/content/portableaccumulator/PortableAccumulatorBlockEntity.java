package com.luncert.vibotech.content.portableaccumulator;

import com.luncert.vibotech.Config;
import com.luncert.vibotech.index.AllPackets;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.network.IObserveTileEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PortableAccumulatorBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IObserveTileEntity {

  protected InternalEnergyStorage energyStorage = this.createEnergyStorage();
  protected LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> this.energyStorage);

  public PortableAccumulatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  protected InternalEnergyStorage createEnergyStorage() {
    return new InternalEnergyStorage(getCapacity(),
        Config.PORTABLE_ACCUMULATOR_MAX_INPUT.get(),
        Config.PORTABLE_ACCUMULATOR_MAX_OUTPUT.get());
  }

  public static int getCapacity() {
    return Config.PORTABLE_ACCUMULATOR_CAPACITY.get();
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> list) {
  }

  @Override
  public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    if (cap == ForgeCapabilities.ENERGY) {
      return this.energyCap.cast();
    }
    return super.getCapability(cap, side);
  }

  @Override
  public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
    ObservePacket.send(this.worldPosition, 0);
    tooltip.add(Component.literal("    ")
        .append(Component.translatable("vibotech.tooltip.portableAccumulator.info").withStyle(ChatFormatting.WHITE)));
    tooltip.add(Component.literal("    ")
        .append(Component.translatable("createaddition.tooltip.energy.stored").withStyle(ChatFormatting.GRAY)));
    tooltip.add(Component.literal("    ")
        .append(Component.literal(" "))
        .append(Util.format(PortableAccumulatorEnergyPacket.clientBuff))
        .append("fe")
        .withStyle(ChatFormatting.AQUA));
    tooltip.add(Component.literal("    ")
        .append(Component.translatable("createaddition.tooltip.energy.capacity").withStyle(ChatFormatting.GRAY)));
    tooltip.add(Component.literal("    ")
        .append(Component.literal(" "))
        .append(Util.format(energyStorage.getMaxEnergyStored()))
        .append("fe")
        .withStyle(ChatFormatting.AQUA));
    return true;
  }

  @Override
  public void onObserved(ServerPlayer serverPlayer, ObservePacket observePacket) {
    // triggered by observe packet, see createaddition
    AllPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> serverPlayer),
        new PortableAccumulatorEnergyPacket(this.worldPosition, 0, energyStorage.getEnergyStored()));
  }
}
