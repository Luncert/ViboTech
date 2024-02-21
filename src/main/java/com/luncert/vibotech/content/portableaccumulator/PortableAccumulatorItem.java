package com.luncert.vibotech.content.portableaccumulator;

import com.luncert.vibotech.content.assemblestation.AssembleStationBlockEntity;
import com.luncert.vibotech.index.AllBlocks;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class PortableAccumulatorItem extends BlockItem {

  public PortableAccumulatorItem(Block block, Properties pProperties) {
    super(block, pProperties);
  }

  public static PortableAccumulatorItem create(Properties properties) {
    return new PortableAccumulatorItem(AllBlocks.PORTABLE_ACCUMULATOR.get(), properties);
  }

  public ItemStack create(PortableAccumulatorBlockEntity portableAccumulator) {
    ItemStack result = new ItemStack(this);
    portableAccumulator.energyStorage.write(result.getOrCreateTag());
    return result;
  }

  @Override
  public @NotNull InteractionResult useOn(UseOnContext context) {
    if (tryPlacePortableAccumulator(context)) {
      context.getLevel()
          .playSound(null, context.getClickedPos(), SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1, 1);
      return InteractionResult.SUCCESS;
    }

    return super.useOn(context);
  }

  public boolean tryPlacePortableAccumulator(UseOnContext context) {
    BlockPos pos = context.getClickedPos().above();
    Level world = context.getLevel();
    Player player = context.getPlayer();
    if (player == null)
      return false;

    BlockState newState = AllBlocks.PORTABLE_ACCUMULATOR.getDefaultState();
    world.setBlockAndUpdate(pos, newState);
    ItemStack itemStack = context.getItemInHand();

    if (!player.isCreative()) {
      itemStack.shrink(1);
    }

    if (itemStack.getTag() != null
        && world.getBlockEntity(pos) instanceof PortableAccumulatorBlockEntity portableAccumulator) {
      portableAccumulator.energyStorage.read(itemStack.getTag());
    }

    AdvancementBehaviour.setPlacedBy(world, pos, player);
    return true;
  }
}
