package com.luncert.vibotech.content.vibomachinecore;

import java.util.Optional;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class EntityMovementData {

    Direction.Axis axis;
    boolean positive;
    float expectedPos;

    public EntityMovementData(Direction.Axis axis, boolean positive, float expectedPos) {
        this.axis = axis;
        this.positive = positive;
        this.expectedPos = expectedPos;
    }

    public static final EntityDataSerializer<Optional<EntityMovementData>> MOVEMENT_SERIALIZER = new MovementSerializer();

    static {
        EntityDataSerializers.registerSerializer(MOVEMENT_SERIALIZER);
    }

    @MethodsReturnNonnullByDefault
    private static class MovementSerializer implements EntityDataSerializer<Optional<EntityMovementData>> {

        @Override
        public void write(FriendlyByteBuf buffer, Optional<EntityMovementData> opt) {
            buffer.writeBoolean(opt.isPresent());
            opt.ifPresent(movement -> {
                buffer.writeInt(movement.axis.ordinal());
                buffer.writeBoolean(movement.positive);
                buffer.writeFloat(movement.expectedPos);
            });
        }

        @Override
        public Optional<EntityMovementData> read(FriendlyByteBuf buffer) {
            boolean isPresent = buffer.readBoolean();
            return isPresent ? Optional.of(new EntityMovementData(Direction.Axis.values()[buffer.readInt()], buffer.readBoolean(), buffer.readFloat()))
                    : Optional.empty();
        }

        @Override
        public Optional<EntityMovementData> copy(Optional<EntityMovementData> opt) {
            return opt.map(movement -> new EntityMovementData(movement.axis, movement.positive, movement.expectedPos));
        }
    }
}
