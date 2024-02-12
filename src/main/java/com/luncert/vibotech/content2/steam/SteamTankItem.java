package com.luncert.vibotech.content2.steam;

import com.luncert.vibotech.content2.gastank.GasTankBlockEntity;
import com.luncert.vibotech.index.AllFluids;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.slf4j.Logger;

public class SteamTankItem extends Item {

   private static final Logger LOGGER = LogUtils.getLogger();

  public SteamTankItem(Properties properties) {
    super(properties);
  }

  //@Override
  //protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
  //  boolean ok = super.placeBlock(context, state);
  //  if (ok) {
  //    LOGGER.info("ok");
  //    GasTankBlockEntity be = (GasTankBlockEntity) context.getLevel()
  //        .getBlockEntity(context.getClickedPos());
  //    FluidStack content = new FluidStack(AllFluids.STEAM.get()
  //        .getSource(), 2000);
  //    LOGGER.info("{}", be.getTankInventory().fill(content, IFluidHandler.FluidAction.SIMULATE));
  //    be.getTankInventory().fill(content, IFluidHandler.FluidAction.EXECUTE);
  //  }
  //  return ok;
  //}
}
