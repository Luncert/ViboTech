package com.luncert.vibotech.content;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.luncert.vibotech.index.AllBlocks;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.Iterate;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.slf4j.Logger;

/**
 * AssembleStationBlockEntity assemble in tick() if block is powered.
 * - AssembleStationBlockEntity#assemble: create TransportMachineEntity and call assemble
 * - TransportMachineEntity#assemble: create contraption, call assemble and start riding
 */
public class AssembleStationBlock extends Block implements IBE<AssembleStationBlockEntity> {

  private static final Logger LOGGER = LogUtils.getLogger();

  public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

  public AssembleStationBlock(Properties properties) {
    super(properties);
    registerDefaultState(defaultBlockState().setValue(POWERED, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(POWERED, HORIZONTAL_FACING);
    super.createBlockStateDefinition(builder);
  }

  @Override
  public Class<AssembleStationBlockEntity> getBlockEntityClass() {
    return AssembleStationBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends AssembleStationBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.ASSEMBLE_STATION.get();
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  public void neighborChanged(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos,
                              @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
    if (worldIn.isClientSide)
      return;
    boolean previouslyPowered = state.getValue(POWERED);
    if (previouslyPowered != worldIn.hasNeighborSignal(pos))
      worldIn.setBlock(pos, state.cycle(POWERED), 2);
    super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
  }

  public enum AssembleStationAction {
    ASSEMBLE, DISASSEMBLE;

    public boolean shouldAssemble() {
      return this == ASSEMBLE;
    }

    public boolean shouldDisassemble() {
      return this == DISASSEMBLE;
    }
  }

  public static AssembleStationAction getAction(BlockState state) {
    boolean powered = state.getValue(POWERED);
    return powered ? AssembleStationAction.ASSEMBLE : AssembleStationAction.DISASSEMBLE;
  }

  public static BlockState createAnchor(BlockState state) {
    return AllBlocks.TRANSPORT_MACHINE_ANCHOR.getDefaultState();
  }
}
