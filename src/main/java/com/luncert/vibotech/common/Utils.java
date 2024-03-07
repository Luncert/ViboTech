package com.luncert.vibotech.common;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.joml.Vector3f;

public class Utils {

    private Utils() {
    }

    public static Vector3f relative(Vector3f v, Direction.Axis axis, float delta) {
        if (delta != 0) {
            return switch (axis) {
                case X -> new Vector3f(v.x() + delta, v.y(), v.z());
                case Y -> new Vector3f(v.x(), v.y() + delta, v.z());
                case Z -> new Vector3f(v.x(), v.y(), v.z() + delta);
            };
        }

        return v;
    }

    public static Vec3 relative(Vec3 v, Direction.Axis axis, double delta) {
        if (delta != 0) {
            return switch (axis) {
                case X -> new Vec3(v.x + delta, v.y, v.z);
                case Y -> new Vec3(v.x, v.y + delta, v.z);
                case Z -> new Vec3(v.x, v.y, v.z + delta);
            };
        }

        return v;
    }

    public static Vec3 set(Vec3 v, Direction.Axis axis, double value) {
        return switch (axis) {
            case X -> new Vec3(value, v.y, v.z);
            case Y -> new Vec3(v.x, value, v.z);
            case Z -> new Vec3(v.x, v.y, value);
        };
    }

    public static Vec3 linear(Direction.Axis axis, double value) {
        return switch (axis) {
            case X -> new Vec3(value, 0, 0);
            case Y -> new Vec3(0, value, 0);
            case Z -> new Vec3(0, 0, value);
        };
    }

    public static BlockPos set(BlockPos v, Direction.Axis axis, double value) {
        return switch (axis) {
            case X -> new BlockPos(Mth.floor(value), v.getY(), v.getZ());
            case Y -> new BlockPos(v.getX(), Mth.floor(value), v.getZ());
            case Z -> new BlockPos(v.getX(), v.getY(), Mth.floor(value));
        };
    }

    public static Vector3f toV3f(BlockPos blockPos) {
        return new Vector3f(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static Vec3 toV3(BlockPos blockPos) {
        return new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static BlockPos toPos(Vec3 p) {
        return new BlockPos(Mth.floor(p.x()), Mth.floor(p.y()), Mth.floor(p.z()));
    }

    public static boolean compareFluidKinds(Fluid a, Fluid b) {
        if (a == b) {
            return true;
        }
        if (a == Fluids.WATER) {
            return b == Fluids.FLOWING_WATER;
        }
        if (a == Fluids.LAVA) {
            return b == Fluids.FLOWING_LAVA;
        }
        if (a instanceof ForgeFlowingFluid fa) {
            if (b instanceof ForgeFlowingFluid fb) {
                return fa.getSource() == fb.getSource();
            }
        }
        return false;
    }

    public static Throwable findCauseUsingPlainJava(Throwable throwable) {
        Objects.requireNonNull(throwable);
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    public static String format(double n) {
        double v;
        String s;
        if (n >= 1000000000) {
            v = (double)Math.round((double)n / 1.0E8);
            s = v / 10.0 + "G";
        } else if (n >= 1000000) {
            v = (double)Math.round((double)n / 100000.0);
            s = v / 10.0 + "M";
        } else if (n >= 1000) {
            v = (double)Math.round((double)n / 100.0);
            s = v / 10.0 + "K";
        } else {
            s = "" + n;
        }
        return s.substring(6);
    }
}
