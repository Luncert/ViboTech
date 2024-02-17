package com.luncert.vibotech.content2.assemblestation;

import com.luncert.vibotech.compat.create.TransportMachineContraptionEntity;
import com.luncert.vibotech.index.AllBlocks;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class AssembleStationItem extends BlockItem {

  // private static final Logger LOGGER = LogUtils.getLogger();

  public AssembleStationItem(AssembleStationBlock block, Properties properties) {
    super(block, properties);
  }

  public ItemStack create(AssembleStationBlockEntity assembleStation) {
    ItemStack result = new ItemStack(this);
    if (assembleStation.isAssembled()) {
      assembleStation.write(result.getOrCreateTag());
    }
    return result;
  }

  public ItemStack create(TransportMachineContraptionEntity contraptionEntity) {
    ItemStack result = new ItemStack(this);
    Entity vehicle = contraptionEntity.getVehicle();
    if (vehicle != null) {
      CompoundTag compound = result.getOrCreateTag();
      compound.putString("transport_machine_reference", vehicle.getUUID().toString());
      compound.putBoolean("assembled", true);
    }
    return result;
  }

  @Override
  public @NotNull InteractionResult useOn(UseOnContext context) {
    if (tryPlaceAssembleStation(context)) {
      context.getLevel()
          .playSound(null, context.getClickedPos(), SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1, 1);
      return InteractionResult.SUCCESS;
    }

    return super.useOn(context);
  }

  public boolean tryPlaceAssembleStation(UseOnContext context) {
    BlockPos pos = context.getClickedPos().above();
    Level world = context.getLevel();
    Player player = context.getPlayer();
    if (player == null)
      return false;

    BlockState newState = AllBlocks.ASSEMBLE_STATION.getDefaultState();
    world.setBlockAndUpdate(pos, newState);
    ItemStack itemStack = context.getItemInHand();

    if (!player.isCreative()) {
      itemStack.shrink(1);
    }

    if (itemStack.getOrCreateTag().contains("transport_machine_reference")
        && world.getBlockEntity(pos) instanceof AssembleStationBlockEntity assembleStationBlockEntity) {
      assembleStationBlockEntity.read(itemStack.getOrCreateTag());
    }

    AdvancementBehaviour.setPlacedBy(world, pos, player);
    return true;
  }
}
