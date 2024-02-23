package com.luncert.vibotech.content.controlseat;

import static com.simibubi.create.content.contraptions.actors.seat.SeatBlock.getLeashed;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.luncert.vibotech.index.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ControlSeatBlock extends Block implements IWrenchable, ProperWaterloggedBlock, IBE<ControlSeatBlockEntity> {

  public static final VoxelShape SHAPE = AllShapes
      .shape(1, 0, 1, 15, 2, 15)
      .build();

  public ControlSeatBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
  }

  public static void sitDown(Level world, BlockPos pos, Entity entity) {
    if (!world.isClientSide) {
      ControlSeatEntity seat = new ControlSeatEntity(world, pos);
      seat.setPos((float)pos.getX() + 0.5F, pos.getY(), (float)pos.getZ() + 0.5F);
      world.addFreshEntity(seat);
      entity.startRiding(seat, true);
      //if (entity instanceof TamableAnimal) {
      //  TamableAnimal ta = (TamableAnimal)entity;
      //  ta.setInSittingPose(true);
      //}
    }
  }

  @Override
  public VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
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
  public BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pDirection, @NotNull BlockState pNeighborState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pCurrentPos, @NotNull BlockPos pNeighborPos) {
    this.updateWater(pLevel, pState, pCurrentPos);
    return pState;
  }

  @Override
  public FluidState getFluidState(@NotNull BlockState pState) {
    return this.fluidState(pState);
  }

  @Override
  public BlockPathTypes getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
    return BlockPathTypes.RAIL;
  }

  @Override
  public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult p_225533_6_) {
    if (player.isShiftKeyDown()) {
      return InteractionResult.PASS;
    } else {
      List<ControlSeatEntity> seats = world.getEntitiesOfClass(ControlSeatEntity.class, new AABB(pos));
      if (!seats.isEmpty()) {
        ControlSeatEntity seatEntity = seats.get(0);
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

  @Override
  public Class<ControlSeatBlockEntity> getBlockEntityClass() {
    return ControlSeatBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends ControlSeatBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.CONTROL_SEAT.get();
  }
}
