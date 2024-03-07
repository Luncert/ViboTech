package com.luncert.vibotech.content.airpipe;

import static com.luncert.vibotech.content.airpipe.AirPipeBlock.isAirPipe;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.ITransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.fluids.FluidTransportBehaviour.AttachmentTypes;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class AirPipeBlockEntity extends SmartBlockEntity implements ITransformableBlockEntity {

  private static final Logger LOGGER = LogUtils.getLogger();

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

  private boolean canHaveBracket(BlockState state) {
    return true;
  }

  public class TransportAirBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<TransportAirBehaviour> TYPE = new BehaviourType<>();

    public TransportAirBehaviour(SmartBlockEntity be) {
      super(be);
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
  }
}
