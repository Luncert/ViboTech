package com.luncert.vibotech.content2.transportmachinecore;

import java.util.Optional;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class TransportMachineMovement {

    Direction.Axis axis;
    boolean positive;
    float expectedPos;

    public TransportMachineMovement(Direction.Axis axis, boolean positive, float expectedPos) {
        this.axis = axis;
        this.positive = positive;
        this.expectedPos = expectedPos;
    }

    public static final EntityDataSerializer<Optional<TransportMachineMovement>> MOVEMENT_SERIALIZER = new MovementSerializer();

    static {
        EntityDataSerializers.registerSerializer(MOVEMENT_SERIALIZER);
    }

    @MethodsReturnNonnullByDefault
    private static class MovementSerializer implements EntityDataSerializer<Optional<TransportMachineMovement>> {

        @Override
        public void write(FriendlyByteBuf buffer, Optional<TransportMachineMovement> opt) {
            buffer.writeBoolean(opt.isPresent());
            opt.ifPresent(movement -> {
                buffer.writeInt(movement.axis.ordinal());
                buffer.writeBoolean(movement.positive);
                buffer.writeFloat(movement.expectedPos);
            });
        }

        @Override
        public Optional<TransportMachineMovement> read(FriendlyByteBuf buffer) {
            boolean isPresent = buffer.readBoolean();
            return isPresent ? Optional.of(new TransportMachineMovement(Direction.Axis.values()[buffer.readInt()], buffer.readBoolean(), buffer.readFloat()))
                    : Optional.empty();
        }

        @Override
        public Optional<TransportMachineMovement> copy(Optional<TransportMachineMovement> opt) {
            return opt.map(movement -> new TransportMachineMovement(movement.axis, movement.positive, movement.expectedPos));
        }
    }
}
