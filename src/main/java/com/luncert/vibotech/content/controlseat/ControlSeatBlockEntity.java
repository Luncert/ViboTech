package com.luncert.vibotech.content.controlseat;

import com.luncert.vibotech.index.AllCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ControlSeatBlockEntity extends BlockEntity {

  private final ControlSeatComponent component = new ControlSeatComponent();

  public ControlSeatBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
    super(pType, pPos, pBlockState);
  }

  @Override
  public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    if (AllCapabilities.isViboComponent(cap)) {
      return LazyOptional.of(() -> component).cast();
    }
    return super.getCapability(cap, side);
  }
}
