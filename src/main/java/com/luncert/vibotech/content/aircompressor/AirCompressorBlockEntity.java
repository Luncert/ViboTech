package com.luncert.vibotech.content.aircompressor;

import static com.luncert.vibotech.index.AllCapabilities.AIR_HANDLER_MACHINE_CAPABILITY;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

import com.luncert.vibotech.compat.pneumatic.IAirHandlerMachine;
import com.luncert.vibotech.compat.pneumatic.MachineAirHandler;
import com.luncert.vibotech.compat.pneumatic.PressureTier;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;

public class AirCompressorBlockEntity extends KineticBlockEntity {

  public static final int VOLUME_AIR_COMPRESSOR = 5000;

  protected final IAirHandlerMachine airHandler =
      new MachineAirHandler(PressureTier.TIER_ONE, VOLUME_AIR_COMPRESSOR);
  private LazyOptional<IAirHandlerMachine> airHandlerCap = LazyOptional.of(() -> airHandler);
  private final Map<IAirHandlerMachine, List<Direction>> airHandlerMap = new IdentityHashMap<>();

  public AirCompressorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    super.addBehaviours(behaviours);

    behaviours.add(new CompressAirBehaviour(this));
  }

  public Direction getOutputSide() {
    return getBlockState().getValue(HORIZONTAL_FACING);
  }

  public boolean isSideAccessible(Direction side) {
    return getBlockState().getValue(HORIZONTAL_FACING)
        .getAxis() == side.getAxis();
  }

  @Override
  public void invalidateCaps() {
    this.airHandlerCap.invalidate();
    this.airHandlerCap = LazyOptional.empty();
    super.invalidateCaps();
  }

  @Override
  public void reviveCaps() {
    super.reviveCaps();
    this.airHandlerCap = LazyOptional.of(() -> airHandler);
  }

  @Override
  public void handleUpdateTag(CompoundTag tag) {
    super.handleUpdateTag(tag);

    initializeHullAirHandlers();
  }

  @Override
  public void onLoad() {
    super.onLoad();

    initializeHullAirHandlers();
  }

  //@Override
  //public void tickCommonPre() {
  //  super.tickCommonPre();
  //
  //  // note: needs to tick client-side too (for handling leak particles & sounds)
  //  airHandlerMap.keySet().forEach(handler -> handler.tick(this));
  //}

  @Override
  public void remove() {
    airHandlerMap.forEach((handler, sides) -> {
      if (!sides.isEmpty()) getCapability(AIR_HANDLER_MACHINE_CAPABILITY, sides.get(0)).invalidate();
    });
  }

  public void initializeHullAirHandlers() {
    airHandlerMap.clear();
    for (Direction side : Direction.values()) {
      getCapability(AIR_HANDLER_MACHINE_CAPABILITY, side)
          .ifPresent(handler -> airHandlerMap.computeIfAbsent(handler, k -> new ArrayList<>()).add(side));
    }
    airHandlerMap.forEach(IAirHandlerMachine::setConnectedFaces);
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    if (cap == AIR_HANDLER_MACHINE_CAPABILITY ) {
      return level != null && (side == null || canConnectPneumatic(side)) ? airHandlerCap.cast() : LazyOptional.empty();
    } else {
      return super.getCapability(cap, side);
    }
  }

  public boolean canConnectPneumatic(Direction side) {
    return getBlockState().getValue(HORIZONTAL_FACING) == side;
  }

  class CompressAirBehaviour extends FluidTransportBehaviour {

    public CompressAirBehaviour(SmartBlockEntity be) {
      super(be);
    }

    @Override
    public void tick() {
      super.tick();
      //for (Entry<Direction, PipeConnection> entry : interfaces.entrySet()) {
      //  boolean pull = isPullingOnSide(isFront(entry.getKey()));
      //  Couple<Float> pressure = entry.getValue().getPressure();
      //  pressure.set(pull, Math.abs(getSpeed()));
      //  pressure.set(!pull, 0f);
      //}
    }

    @Override
    public boolean canPullFluidFrom(FluidStack fluid, BlockState state, Direction direction) {
      return getOutputSide().equals(direction);
    }

    @Override
    public boolean canHaveFlowToward(BlockState state, Direction direction) {
      return isSideAccessible(direction);
    }

    @Override
    public AttachmentTypes getRenderedRimAttachment(BlockAndTintGetter world, BlockPos pos, BlockState state,
                                                    Direction direction) {
      AttachmentTypes attachment = super.getRenderedRimAttachment(world, pos, state, direction);
      if (attachment == AttachmentTypes.RIM)
        return AttachmentTypes.NONE;
      return attachment;
    }
  }
}
