package com.luncert.vibotech.content.transportmachinecore;

import com.luncert.vibotech.compat.create.TransportMachineContraptionEntity;
import com.luncert.vibotech.content.assemblestation.AssembleStationItem;
import com.luncert.vibotech.index.AllBlocks;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class TransportMachineCoreInteractionBehaviour extends MovingInteractionBehaviour {

  @Override
  public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos,
                                         AbstractContraptionEntity contraptionEntity) {
    // bind assemble station
    Inventory inventory = player.getInventory();
    AssembleStationItem assembleStationItem = (AssembleStationItem) AllBlocks.ASSEMBLE_STATION.asItem();
    if (inventory.getSelected().is(assembleStationItem)) {
      ItemStack taggedItemStack = assembleStationItem.create((TransportMachineContraptionEntity) contraptionEntity);
      int freeSlot = inventory.getFreeSlot();
      if (freeSlot != -1) {
        ItemStack itemStack = inventory.removeFromSelected(true);
        inventory.add(inventory.selected, taggedItemStack);
        inventory.add(freeSlot, itemStack);
      } else {
        Vec3 pos = player.position();
        ItemEntity itemEntity = new ItemEntity(contraptionEntity.level(), pos.x, pos.y, pos.z, taggedItemStack);
        contraptionEntity.level().addFreshEntity(itemEntity);
      }
      return true;
    }


    return false;
  }
}
