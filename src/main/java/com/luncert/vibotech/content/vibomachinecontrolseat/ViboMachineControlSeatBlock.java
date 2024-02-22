package com.luncert.vibotech.content.vibomachinecontrolseat;

import static com.simibubi.create.content.contraptions.actors.seat.SeatBlock.getLeashed;
import static com.simibubi.create.content.contraptions.actors.seat.SeatBlock.sitDown;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

import com.luncert.vibotech.index.AllShapes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ViboMachineControlSeatBlock extends Block implements IWrenchable, ProperWaterloggedBlock {

  public static final VoxelShape SHAPE = AllShapes
      .shape(1, 0, 1, 15, 2, 15)
      .build();

  public ViboMachineControlSeatBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
  }

  @Override
  public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    return SHAPE;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(HORIZONTAL_FACING, WATERLOGGED);
    super.createBlockStateDefinition(builder);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
    this.updateWater(pLevel, pState, pCurrentPos);
    return pState;
  }

  @Override
  public FluidState getFluidState(BlockState pState) {
    return this.fluidState(pState);
  }

  @Override
  public BlockPathTypes getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
    return BlockPathTypes.RAIL;
  }

  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult p_225533_6_) {
    if (player.isShiftKeyDown()) {
      return InteractionResult.PASS;
    } else {
      List<SeatEntity> seats = world.getEntitiesOfClass(SeatEntity.class, new AABB(pos));
      if (!seats.isEmpty()) {
        SeatEntity seatEntity = seats.get(0);
        List<Entity> passengers = seatEntity.getPassengers();
        if (!passengers.isEmpty() && passengers.get(0) instanceof Player) {
          return InteractionResult.PASS;
        } else {
          if (!world.isClientSide) {
            seatEntity.ejectPassengers();
            player.startRiding(seatEntity);
          }

          return InteractionResult.SUCCESS;
        }
      } else if (world.isClientSide) {
        return InteractionResult.SUCCESS;
      } else {
        sitDown(world, pos, getLeashed(world, player).or(player));
        return InteractionResult.SUCCESS;
      }
    }
  }

}
