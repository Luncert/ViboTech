package com.luncert.vibotech.content.airpipe;

import static com.luncert.vibotech.content.airpipe.AirPipeBlock.isAirPipe;
import static com.luncert.vibotech.index.AllCapabilities.AIR_HANDLER_MACHINE_CAPABILITY;

import com.luncert.vibotech.compat.pneumatic.IAirHandlerMachine;
import com.luncert.vibotech.compat.pneumatic.MachineAirHandler;
import com.luncert.vibotech.compat.pneumatic.PressureTier;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.ITransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.slf4j.Logger;

public class AirPipeBlockEntity extends SmartBlockEntity implements ITransformableBlockEntity {

  public static final int VOLUME_AIR_COMPRESSOR = 1000;

  private static final Logger LOGGER = LogUtils.getLogger();

  protected final IAirHandlerMachine airHandler =
      new MachineAirHandler(PressureTier.TIER_ONE, VOLUME_AIR_COMPRESSOR);
  private LazyOptional<IAirHandlerMachine> airHandlerCap = LazyOptional.of(() -> airHandler);
  private final Map<IAirHandlerMachine, List<Direction>> airHandlerMap = new IdentityHashMap<>();

  public AirPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    behaviours.add(new TransportAirBehaviour(this));
    behaviours.add(new BracketedBlockEntityBehaviour(this, this::canHaveBracket));
  }

  @Override
  public void transform(StructureTransform structureTransform) {
    BracketedBlockEntityBehaviour bracketBehaviour = (BracketedBlockEntityBehaviour)this.getBehaviour(BracketedBlockEntityBehaviour.TYPE);
    if (bracketBehaviour != null) {
      bracketBehaviour.transformBracket(structureTransform);
    }
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

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    if (cap == AIR_HANDLER_MACHINE_CAPABILITY) {
      return airHandlerCap.cast();
    } else {
      return super.getCapability(cap, side);
    }
  }

  private boolean canHaveBracket(BlockState state) {
    return true;
  }

  public class TransportAirBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<TransportAirBehaviour> TYPE = new BehaviourType<>();

    public TransportAirBehaviour(SmartBlockEntity be) {
      super(be);
    }

    @Override
    public void tick() {
      airHandler.tick(blockEntity);
    }

    @Override
    public BehaviourType<?> getType() {
      return TYPE;
    }
    public boolean canHaveFlowToward(BlockState state, Direction direction) {
      return isAirPipe(state) && state.getValue(AirPipeBlock.PROPERTY_BY_DIRECTION.get(direction));
    }

    public AttachmentTypes getRenderedRimAttachment(BlockAndTintGetter world, BlockPos pos, BlockState state,
                                                                            Direction direction) {
      AttachmentTypes attachment = canHaveFlowToward(state, direction) ? AttachmentTypes.RIM : AttachmentTypes.NONE;

      BlockPos offsetPos = pos.relative(direction);
      BlockState otherState = world.getBlockState(offsetPos);

      if (attachment == AttachmentTypes.RIM && !isAirPipe(otherState)) {
        TransportAirBehaviour pipeBehaviour =
            BlockEntityBehaviour.get(world, offsetPos, TransportAirBehaviour.TYPE);
        if (pipeBehaviour != null)
          if (pipeBehaviour.canHaveFlowToward(otherState, direction.getOpposite()))
            return AttachmentTypes.CONNECTION;
      }

      if (attachment == AttachmentTypes.RIM && !AirPipeBlock.shouldDrawRim(world, pos, state, direction))
        return AttachmentTypes.CONNECTION;
      if (attachment == AttachmentTypes.NONE && state.getValue(AirPipeBlock.PROPERTY_BY_DIRECTION.get(direction)))
        return AttachmentTypes.CONNECTION;

      return attachment;
    }

    public enum AttachmentTypes {
      NONE,
      CONNECTION(ComponentPartials.CONNECTION),
      RIM(ComponentPartials.RIM_CONNECTOR, ComponentPartials.RIM),
      PARTIAL_RIM(ComponentPartials.RIM),
      DRAIN(ComponentPartials.RIM_CONNECTOR, ComponentPartials.DRAIN),
      PARTIAL_DRAIN(ComponentPartials.DRAIN);

      public final ComponentPartials[] partials;

      AttachmentTypes(ComponentPartials... partials) {
        this.partials = partials;
      }

      public AttachmentTypes withoutConnector() {
        if (this == AttachmentTypes.RIM)
          return AttachmentTypes.PARTIAL_RIM;
        if (this == AttachmentTypes.DRAIN)
          return AttachmentTypes.PARTIAL_DRAIN;
        return this;
      }

      public enum ComponentPartials {
        CONNECTION, RIM_CONNECTOR, RIM, DRAIN;
      }
    }
  }
}
