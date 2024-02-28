package com.luncert.vibotech.content.portableaccumulator;

import com.luncert.vibotech.index.AllBlocks;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
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
    if (portableAccumulator.energyStorage.getEnergyStored() > 0) {
      portableAccumulator.energyStorage.write(result.getOrCreateTag());
    }
    return result;
  }

  @Override
  public @NotNull InteractionResult useOn(UseOnContext context) {
    if (tryPlacePortableAccumulator(new BlockPlaceContext(context))) {
      context.getLevel()
          .playSound(null, context.getClickedPos(), SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1, 1);
      return InteractionResult.SUCCESS;
    }

    return super.useOn(context);
  }

  public boolean tryPlacePortableAccumulator(BlockPlaceContext context) {
    var newState = AllBlocks.PORTABLE_ACCUMULATOR.getDefaultState();
    if (!this.placeBlock(context, newState)) {
      return false;
    }

    var pos = context.getClickedPos();
    var world = context.getLevel();
    var player = context.getPlayer();
    if (player == null)
      return false;

    ItemStack itemStack = context.getItemInHand();

    if (!player.getAbilities().instabuild) {
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
