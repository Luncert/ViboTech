package com.luncert.vibotech.content.camera;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

import com.luncert.vibotech.content.camera.packet.ServerDisconnectCameraPacket;
import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.luncert.vibotech.index.AllPackets;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class CameraBlock extends Block implements IBE<CameraBlockEntity>, IWrenchable {

  public CameraBlock(Properties properties) {
    super(properties);
  }

  @Override
  public Class<CameraBlockEntity> getBlockEntityClass() {
    return CameraBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends CameraBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.CAMERA.get();
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
    super.createBlockStateDefinition(builder);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
  }

  @Override
  public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, Player player,
                               @NotNull InteractionHand hand, @NotNull BlockHitResult p_225533_6_) {
    if (player.isShiftKeyDown()) {
      return InteractionResult.PASS;
    }
    withBlockEntityDo(world, pos, be -> {
      if (world.isClientSide) {
        be.connectFromClient(player);
      }
    });
    return InteractionResult.SUCCESS;
  }

  @Override
  public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
    super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    if (pLevel.isClientSide) {
      AllPackets.getChannel().sendToServer(new ServerDisconnectCameraPacket());
    }
  }
}
