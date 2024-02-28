package com.luncert.vibotech.content.assemblestation;

import com.luncert.vibotech.compat.create.ViboMachineContraptionEntity;
import com.luncert.vibotech.content.vibomachinecore.ViboMachineEntity;
import com.luncert.vibotech.index.AllBlocks;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class AssembleStationItem extends Item {

  // private static final Logger LOGGER = LogUtils.getLogger();
  private final boolean bindViboMachine;

  public static AssembleStationItem empty(Properties properties) {
    return new AssembleStationItem(properties, false);
  }

  public static AssembleStationItem active(Properties properties) {
    return new AssembleStationItem(properties, true);
  }

  public AssembleStationItem(Properties properties, boolean bindViboMachine) {
    super(properties);
    this.bindViboMachine = bindViboMachine;
  }

  public ItemStack create(AssembleStationBlockEntity assembleStation) {
    ItemStack result = new ItemStack(this);
    if (bindViboMachine) {
      assembleStation.write(result.getOrCreateTag());
    }
    return result;
  }

  public ItemStack create(ViboMachineContraptionEntity contraptionEntity) {
    if (contraptionEntity.getVehicle() instanceof ViboMachineEntity viboMachineEntity) {
      ItemStack result = new ItemStack(this);
      CompoundTag compound = result.getOrCreateTag();
      compound.putString("vibo_machine_reference", viboMachineEntity.getUUID().toString());
      compound.putBoolean("assembled", true);
      return result;
    }
    return ItemStack.EMPTY;
  }

  @Override
  public String getDescriptionId() {
    return bindViboMachine ? "item.vibotech.assembled_assemble_station" : "item.vibotech.assemble_station";
  }

  @Override
  public @NotNull InteractionResult useOn(UseOnContext context) {
    if (tryPlaceAssembleStation(new BlockPlaceContext(context))) {
      context.getLevel()
          .playSound(null, context.getClickedPos(), SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1, 1);
      return InteractionResult.SUCCESS;
    }

    return super.useOn(context);
  }

  public boolean tryPlaceAssembleStation(BlockPlaceContext context) {
    var player = context.getPlayer();
    if (player == null)
      return false;

    var newState = AllBlocks.ASSEMBLE_STATION.getDefaultState();
    if (!this.placeBlock(context, newState)) {
      return false;
    }

    ItemStack itemStack = context.getItemInHand();

    if (!player.isCreative()) {
      itemStack.shrink(1);
    }

    var pos = context.getClickedPos();
    var world = context.getLevel();
    if (itemStack.getTag() != null && itemStack.getTag().contains("vibo_machine_reference")
        && world.getBlockEntity(pos) instanceof AssembleStationBlockEntity assembleStationBlockEntity) {
      assembleStationBlockEntity.read(itemStack.getTag());
    }

    AdvancementBehaviour.setPlacedBy(world, pos, player);
    return true;
  }

  protected boolean placeBlock(BlockPlaceContext pContext, BlockState pState) {
    return pContext.getLevel().setBlock(pContext.getClickedPos(), pState, 11);
  }
}
