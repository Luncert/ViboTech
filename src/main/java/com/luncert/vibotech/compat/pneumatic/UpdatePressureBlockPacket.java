/*
 * This file is part of pnc-repressurized.
 *
 *     pnc-repressurized is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     pnc-repressurized is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with pnc-repressurized.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.luncert.vibotech.compat.pneumatic;

import com.luncert.vibotech.index.AllCapabilities;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

/**
 * Received on: CLIENT
 * Sent periodically from server to sync pressure level:
 * - For pressure tubes with an attached pressure gauge module
 * - For air grate modules, when the pressure changes enough to modify the range
 * - For machine air handlers which are currently leaking
 */
public class UpdatePressureBlockPacket extends LocationIntPacket {
    private static final byte NO_DIRECTION = 127;

    private final Direction leakDir;
    private final Direction handlerDir;
    private final int currentAir;

    public UpdatePressureBlockPacket(BlockEntity te, Direction handlerDir, Direction leakDir, int currentAir) {
        super(te.getBlockPos());

        this.handlerDir = handlerDir;
        this.leakDir = leakDir;
        this.currentAir = currentAir;
    }

    public UpdatePressureBlockPacket(FriendlyByteBuf buffer) {
        super(buffer);
        this.currentAir = buffer.readInt();
        byte idx = buffer.readByte();
        this.handlerDir = idx >= 0 && idx < 6 ? Direction.from3DDataValue(idx) : null;
        idx = buffer.readByte();
        this.leakDir = idx >= 0 && idx < 6 ? Direction.from3DDataValue(idx) : null;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(currentAir);
        buf.writeByte(handlerDir == null ? NO_DIRECTION : handlerDir.get3DDataValue());
        buf.writeByte(leakDir == null ? NO_DIRECTION : leakDir.get3DDataValue());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
            if (blockEntity != null) {
                blockEntity.getCapability(AllCapabilities.AIR_HANDLER_MACHINE_CAPABILITY, handlerDir).ifPresent(handler -> {
                    handler.setSideLeaking(leakDir);
                    handler.addAir(currentAir - handler.getAir());
                    if (handlerDir != null && blockEntity instanceof AbstractAirHandlingBlockEntity be) {
                        be.initializeHullAirHandlerClient(handlerDir, handler);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
