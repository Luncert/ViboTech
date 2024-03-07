package com.luncert.vibotech.content.aircompressor;

import static com.luncert.vibotech.index.AllCapabilities.AIR_HANDLER_MACHINE_CAPABILITY;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

import com.luncert.vibotech.common.Utils;
import com.luncert.vibotech.compat.pneumatic.AirHandlerPacket;
import com.luncert.vibotech.compat.pneumatic.IAirHandlerMachine;
import com.luncert.vibotech.compat.pneumatic.MachineAirHandler;
import com.luncert.vibotech.compat.pneumatic.PressureTier;
import com.luncert.vibotech.index.AllPackets;
import com.mojang.logging.LogUtils;
import com.mrh0.createaddition.network.IObserveTileEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

public class AirCompressorBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation, IObserveTileEntity {

  private static final Logger LOGGER = LogUtils.getLogger();

  private static final int AIR_PRODUCTION = 10; // mL per pick

  public static final int VOLUME_AIR_COMPRESSOR = 5000;

  private static final int COOL_DOWN_AFFECT_DURATION = 10;

  private static final Direction[] AVAILABLE_COOLER_DIRECTION = new Direction[]{
      Direction.UP, Direction.DOWN, Direction.WEST, Direction.EAST
  };

  // private final int[] coolerDurations = new int[Direction.values().length];
  // private final IFanProcessingHandler fanProcessingHandler = new IFanProcessingHandler() {
  //   @Override
  //   public void tick(Direction airCurrentDirection, Direction airFlowDirection) {
  //     if (airCurrentDirection != airFlowDirection) {
  //       coolerDurations[airCurrentDirection.ordinal()] = COOL_DOWN_AFFECT_DURATION;
  //     }
  //   }
  // };

  protected final IAirHandlerMachine airHandler =
      new MachineAirHandler(PressureTier.TIER_ONE, VOLUME_AIR_COMPRESSOR);
  private LazyOptional<IAirHandlerMachine> airHandlerCap = LazyOptional.of(() -> airHandler);
  private final Map<IAirHandlerMachine, List<Direction>> airHandlerMap = new IdentityHashMap<>();

  public AirCompressorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    super.addBehaviours(behaviours);

    behaviours.add(new CompressAirBehaviour(this));
  }

  public Direction getOutputSide() {
    return getBlockState().getValue(HORIZONTAL_FACING);
  }

  public boolean isSideAccessible(Direction side) {
    return getBlockState().getValue(HORIZONTAL_FACING)
        .getAxis() == side.getAxis();
  }

  @Override
  public void invalidateCaps() {
    this.airHandlerCap.invalidate();
    this.airHandlerCap = LazyOptional.empty();
    super.invalidateCaps();
  }

  @Override
  public void reviveCaps() {
    super.reviveCaps();
    this.airHandlerCap = LazyOptional.of(() -> airHandler);
  }

  @Override
  public void handleUpdateTag(CompoundTag tag) {
    super.handleUpdateTag(tag);

    initializeHullAirHandlers();
  }

  @Override
  public void onLoad() {
    super.onLoad();

    initializeHullAirHandlers();
  }

  //@Override
  //public void tickCommonPre() {
  //  super.tickCommonPre();
  //
  //  // note: needs to tick client-side too (for handling leak particles & sounds)
  //  airHandlerMap.keySet().forEach(handler -> handler.tick(this));
  //}

  @Override
  public void remove() {
    airHandlerMap.forEach((handler, sides) -> {
      if (!sides.isEmpty()) getCapability(AIR_HANDLER_MACHINE_CAPABILITY, sides.get(0)).invalidate();
    });
  }

  public void initializeHullAirHandlers() {
    airHandlerMap.clear();
    for (Direction side : Direction.values()) {
      getCapability(AIR_HANDLER_MACHINE_CAPABILITY, side)
          .ifPresent(handler -> airHandlerMap.computeIfAbsent(handler, k -> new ArrayList<>()).add(side));
    }
    airHandlerMap.forEach(IAirHandlerMachine::setConnectedFaces);
  }

  // called clientside when a PacketUpdatePressureBlock is received
  // this ensures the BE can tick this air handler for air leak sound and particle purposes
  public void initializeHullAirHandlerClient(Direction dir, IAirHandlerMachine handler) {
    airHandlerMap.clear();
    List<Direction> l = Collections.singletonList(dir);
    airHandlerMap.put(handler, l);
    handler.setConnectedFaces(l);
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    if (cap == AIR_HANDLER_MACHINE_CAPABILITY ) {
      return level != null && (side == null || canConnectPneumatic(side)) ? airHandlerCap.cast() : LazyOptional.empty();
    } else {
      return super.getCapability(cap, side);
    }
  }

  public boolean canConnectPneumatic(Direction side) {
    return getOutputSide() == side;
  }

  @Override
  public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
    ObservePacket.send(this.worldPosition, 0);
    tooltip.add(Component.literal("    ")
        .append(Component.translatable("vibotech.tooltip.air.info").withStyle(ChatFormatting.WHITE)));
    tooltip.add(Component.literal("    ")
        .append(Component.translatable("vibotech.tooltip.air.volume").withStyle(ChatFormatting.GRAY)));
    tooltip.add(Component.literal("    ")
        .append(Component.literal(" "))
        .append(Util.format(AirHandlerPacket.clientAir) + "L/" + Util.format(AirHandlerPacket.clientVolume / 1000))
        .append("L")
        .withStyle(ChatFormatting.AQUA));
    tooltip.add(Component.literal("    ")
        .append(Component.translatable("vibotech.tooltip.air.pressure").withStyle(ChatFormatting.GRAY)));
    tooltip.add(Component.literal("    ")
        .append(Component.literal(" "))
        .append(Utils.format(AirHandlerPacket.clientPressure))
        .append("Bar")
        .withStyle(ChatFormatting.AQUA));
    tooltip.add(Component.literal("    ")
        .append(Component.translatable("vibotech.tooltip.air.heat").withStyle(ChatFormatting.GRAY)));
    tooltip.add(Component.literal("    ")
        .append(Component.literal(" "))
        .append(Utils.format(AirHandlerPacket.clientHeat))
        .append("%")
        .withStyle(ChatFormatting.AQUA));
    return true;
  }

  @Override
  public void onObserved(ServerPlayer serverPlayer, ObservePacket observePacket) {
    // triggered by observe packet, see createaddition
    AllPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> serverPlayer),
        new AirHandlerPacket(airHandler.getPressure(), airHandler.getVolume(), airHandler.getAir(),
            getBehaviour(CompressAirBehaviour.TYPE).heat));
  }

  class CompressAirBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<CompressAirBehaviour> TYPE = new BehaviourType<>();

    private float airBuffer;
    private float airPerTick;
    private float heat;
    private int coolerCount;

    public CompressAirBehaviour(SmartBlockEntity be) {
      super(be);
    }

    @Override
    public BehaviourType<?> getType() {
      return TYPE;
    }

    @Override
    public void tick() {
      super.tick();

      airHandler.tick(blockEntity);

      tickCooler();

      if (!level.isClientSide) {
        final float speed = Math.abs(getSpeed());
        float factoryOfSpeed = (float) Math.sqrt(65025 - Math.pow(speed - 256, 2));

        // update heat
        float heatLimit = getHeatLimit();
        float targetHeat = Mth.clamp(factoryOfSpeed / 256 * heatLimit, 0, heatLimit);
        heat = Mth.lerp(0.1f - coolerCount * 0.02f, heat, targetHeat);

        // update air production
        airPerTick = AIR_PRODUCTION * factoryOfSpeed / 10 * getHeatEfficiency();
        airBuffer += airPerTick;

        if (airBuffer >= 1f) {
          int toAdd = (int) airBuffer;
          airHandler.addAir(toAdd);
          airBuffer -= toAdd;
        }
        airHandler.setSideLeaking(airHandler.getConnectedAirHandlers(blockEntity).isEmpty()
            ? getOutputSide() : null);
      }
    }

    private void tickCooler() {
      coolerCount = 0;

      for (Direction direction : AVAILABLE_COOLER_DIRECTION) {
        var relativePos = worldPosition.relative(direction);
        var relativeState = level.getBlockState(relativePos);

        if (AllBlocks.ENCASED_FAN.has(relativeState)
            && level.getBlockEntity(relativePos) instanceof KineticBlockEntity be) {
          var facing = relativeState.getValue(BlockStateProperties.FACING);
          var speed = convertToDirection(be.getSpeed(), facing);
          if (speed < 0) {
            coolerCount++;
          }
        }
      }
    }

    private float getHeatEfficiency() {
      // 6 fan only reduce the heat up to 36% at most (6% per fan).
      // 6 fan with water can reduce the heat up to 78% at most (13% per fan).
      return 1 - heat / 200;
    }

    private float getHeatLimit() {
      return 100 - coolerCount * 6;
    }
  }
}
