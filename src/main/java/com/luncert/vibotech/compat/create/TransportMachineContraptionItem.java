package com.luncert.vibotech.compat.create;

import com.luncert.vibotech.common.Lang;
import com.luncert.vibotech.content.transportmachinecore.ViboMachineEntity;
import com.mojang.logging.LogUtils;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionData;
import com.simibubi.create.content.contraptions.ContraptionMovementSetting;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;

@Mod.EventBusSubscriber
public class TransportMachineContraptionItem extends Item {

  private static final Logger LOGGER = LogUtils.getLogger();

  public TransportMachineContraptionItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    return super.useOn(context);
  }

  @Override
  public String getDescriptionId() {
    return super.getDescriptionId();
  }

  @SubscribeEvent
  public static void wrenchCanBeUsedToPickUpTransportMachineContraptions(PlayerInteractEvent.EntityInteract event) {
    LOGGER.info("entity interact event");

    Entity entity = event.getTarget();
    Player player = event.getEntity();
    if (player == null || entity == null)
      return;
    if (!AllConfigs.server().kinetics.survivalContraptionPickup.get() && !player.isCreative())
      return;

    ItemStack wrench = player.getItemInHand(event.getHand());
    if (!AllItems.WRENCH.isIn(wrench))
      return;
    if (entity instanceof AbstractContraptionEntity)
      entity = entity.getVehicle();
    if (!(entity instanceof ViboMachineEntity core))
      return;
    if (!entity.isAlive())
      return;
    if (player instanceof DeployerFakePlayer dfp && dfp.onMinecartContraption)
      return;

    List<Entity> passengers = core.getPassengers();
    if (passengers.isEmpty() || !(passengers.get(0) instanceof OrientedContraptionEntity oce))
      return;
    Contraption contraption = oce.getContraption();

    if (ContraptionMovementSetting.isNoPickup(contraption.getBlocks()
        .values())) {
      player.displayClientMessage(Lang.translateDirect("contraption.transport_machine_contraption_illegal_pickup")
          .withStyle(ChatFormatting.RED), true);
      return;
    }

    if (event.getLevel().isClientSide) {
      event.setCancellationResult(InteractionResult.SUCCESS);
      event.setCanceled(true);
      return;
    }

    contraption.stop(event.getLevel());

    for (MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> pair : contraption.getActors())
      if (AllMovementBehaviours.getBehaviour(pair.left.state()) instanceof PortableStorageInterfaceMovement psim)
        psim.reset(pair.right);

    ItemStack generatedStack = create(oce).setHoverName(entity.getCustomName());

    if (ContraptionData.isTooLargeForPickup(generatedStack.serializeNBT())) {
      MutableComponent message = Lang.translateDirect("contraption.transport_machine_contraption_too_big")
          .withStyle(ChatFormatting.RED);
      player.displayClientMessage(message, true);
      return;
    }

    player.getInventory()
        .placeItemBackInInventory(generatedStack);
    oce.discard();
    entity.discard();
    event.setCancellationResult(InteractionResult.SUCCESS);
    event.setCanceled(true);
  }

  public static ItemStack create(OrientedContraptionEntity entity) {
    ItemStack stack = ItemStack.EMPTY;

    CompoundTag tag = entity.getContraption().writeNBT(false);
    tag.remove("UUID");
    tag.remove("Pos");
    tag.remove("Motion");

    NBTHelper.writeEnum(tag, "InitialOrientation", entity.getInitialOrientation());
    stack.getOrCreateTag().put("Contraption", tag);
    return stack;
  }
}
