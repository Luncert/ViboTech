package com.luncert.vibotech.content.controlseat;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

public class ControlSeatInputPacket extends SimplePacketBase {

  private static final Logger LOGGER = LogUtils.getLogger();

  private final Collection<InputConstants.Key> activatedButtons;
  private final boolean press;

  public ControlSeatInputPacket(Collection<InputConstants.Key> activatedButtons, boolean press) {
    this.activatedButtons = activatedButtons;
    this.press = press;
  }

  public ControlSeatInputPacket(FriendlyByteBuf buffer) {
    activatedButtons = new ArrayList<>();
    press = buffer.readBoolean();
    int size = buffer.readVarInt();
    for (int i = 0; i < size; i++) {
      InputConstants.Key key =
          switch (InputConstants.Type.values()[buffer.readVarInt()]) {
            case KEYSYM -> InputConstants.Type.KEYSYM.getOrCreate(buffer.readVarInt());
            case MOUSE -> InputConstants.Type.MOUSE.getOrCreate(buffer.readVarInt());
            case SCANCODE -> null;
          };
      if (key != null) {
        activatedButtons.add(key);
      }
    }
  }

  @Override
  public void write(FriendlyByteBuf buffer) {
    buffer.writeBoolean(press);
    buffer.writeVarInt(activatedButtons.size());
    activatedButtons.forEach(activated -> {
      buffer.writeVarInt(activated.getType().ordinal());
      buffer.writeVarInt(activated.getValue());
    });
  }

  @Override
  public boolean handle(NetworkEvent.Context context) {
    context.enqueueWork(() -> {
      ServerPlayer player = context.getSender();
      if (player == null)
        return;

      Level world = player.getCommandSenderWorld();
      UUID uniqueID = player.getUUID();

      if (player.isSpectator() && press)
        return;

      ControlSeatComponent.receivePressed(world, uniqueID, activatedButtons, press);
    });
    return true;
  }
}
