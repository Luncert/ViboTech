package com.luncert.vibotech.content.aircompressor;

import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

public class AirCompressorBlockEntity extends KineticBlockEntity {

  public AirCompressorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    super.addBehaviours(behaviours);

    behaviours.add(new CompressAirBehaviour(this));
  }

  public Direction getOutputSide() {
    return getBlockState().getValue(AirCompressorBlock.HORIZONTAL_FACING);
  }

  public boolean isSideAccessible(Direction side) {
    return getBlockState().getValue(AirCompressorBlock.HORIZONTAL_FACING)
        .getAxis() == side.getAxis();
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
