package com.luncert.vibotech.foundation.item;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public record ItemDescription(ImmutableList<Component> lines, ImmutableList<Component> linesOnShift,
                              ImmutableList<Component> linesOnCtrl) {

  public static class Modifier implements TooltipModifier {

    protected final Item item;
    protected final TooltipHelper.Palette palette;

    public Modifier(Item item, TooltipHelper.Palette palette) {
      this.item = item;
      this.palette = palette;
    }

    @Override
    public void modify(ItemTooltipEvent context) {

    }
  }
}
