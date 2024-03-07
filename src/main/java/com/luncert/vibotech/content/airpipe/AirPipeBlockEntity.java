package com.luncert.vibotech.content.airpipe;

import com.simibubi.create.content.contraptions.ITransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class AirPipeBlockEntity extends SmartBlockEntity implements ITransformableBlockEntity {

  public AirPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> list) {

  }

  @Override
  public void transform(StructureTransform structureTransform) {

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
      return AirPipeBlock.isAirPipe(state)
          && state.<Boolean>getValue(AirPipeBlock.PROPERTY_BY_DIRECTION.get(direction));
    }
  }
}
