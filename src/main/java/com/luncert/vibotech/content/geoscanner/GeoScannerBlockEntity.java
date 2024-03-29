package com.luncert.vibotech.content.geoscanner;

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

public class GeoScannerBlockEntity extends BlockEntity {

  private final GeoScannerComponent geoScannerComponent = new GeoScannerComponent();

  public GeoScannerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    if (AllCapabilities.isViboComponent(cap)) {
      return LazyOptional.of(() -> geoScannerComponent).cast();
    }
    return super.getCapability(cap, side);
  }
}
