package com.luncert.vibotech.content.portableaccumulator;

import com.google.common.collect.ImmutableList;
import com.luncert.vibotech.ViboTechMod;
import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.luncert.vibotech.index.AllItems;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PortableAccumulatorBlock extends Block implements IBE<PortableAccumulatorBlockEntity>, IWrenchable {

  private static final ResourceLocation DROP = ViboTechMod.asResource("portable_accumulator");

  public PortableAccumulatorBlock(Properties properties) {
    super(properties);
  }

  @Override
  public Class<PortableAccumulatorBlockEntity> getBlockEntityClass() {
    return PortableAccumulatorBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends PortableAccumulatorBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.PORTABLE_ACCUMULATOR.get();
  }

  // custom drop

  @Override
  public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity tile, ItemStack tool) {
    // Don't drop blocks here - see playerWillDestroy.
    player.awardStat(Stats.BLOCK_MINED.get(this));
    player.causeFoodExhaustion(0.005F);
  }

  @Override
  public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
    super.playerWillDestroy(world, pos, state, player);
    if (!(world instanceof ServerLevel serverWorld)) return;

    var tile = world.getBlockEntity(pos);
    if (tile instanceof PortableAccumulatorBlockEntity) {
      var context = new LootParams.Builder(serverWorld)
          .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
          .withParameter(LootContextParams.TOOL, player.getMainHandItem())
          .withParameter(LootContextParams.THIS_ENTITY, player)
          .withParameter(LootContextParams.BLOCK_ENTITY, tile);
      for (var item : state.getDrops(context)) {
        popResource(world, pos, item);
      }

      state.spawnAfterBreak(serverWorld, pos, player.getMainHandItem(), true);
    }
  }

  @Override
  public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
    if (!(builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof PortableAccumulatorBlockEntity portableAccumulator))
      return super.getDrops(state, builder);

    builder.withDynamicDrop(DROP, (out) -> {
      out.accept(getItem(portableAccumulator));
    });
    return ImmutableList.of(getItem(portableAccumulator));
  }

  private ItemStack getItem(PortableAccumulatorBlockEntity blockEntity) {
    return AllItems.PORTABLE_ACCUMULATOR.get().create(blockEntity);
  }
}
