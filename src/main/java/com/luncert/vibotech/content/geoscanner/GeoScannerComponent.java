package com.luncert.vibotech.content.geoscanner;

import com.google.common.collect.ImmutableMap;
import com.luncert.vibotech.common.LocalVariable;
import com.luncert.vibotech.compat.vibotech.BaseViboComponent;
import com.luncert.vibotech.compat.vibotech.ViboComponentType;
import com.luncert.vibotech.common.ScanUtils;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public class GeoScannerComponent extends BaseViboComponent {

  @Override
  public ViboComponentType getComponentType() {
    return ViboComponentType.GEO_SCANNER;
  }

  @LuaFunction(mainThread = true)
  public final MethodResult search(String harvestable) throws LuaException {
    EHarvestable target;
    try {
      target = EHarvestable.valueOf(harvestable.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new LuaException("Invalid argument, must be one of " + Arrays.toString(EHarvestable.values()));
    }

    BlockPos center = accessor.viboMachineEntity.blockPosition();

    LocalVariable<Pair<BlockPos, BlockPos>> ref = new LocalVariable<>();

    ScanUtils.traverseBlocks(accessor.world, center, 8, (state, pos) -> {
      if (target.test(state)) {
        ref.set(ScanUtils.calcShapeForAdjacentBlocks(accessor.world, pos));
        return false;
      }
      return true;
    });

    if (ref.isEmpty()) {
      return MethodResult.of(false);
    }

    Pair<BlockPos, BlockPos> locator = ref.get();
    BlockPos a = locator.getLeft();
    BlockPos b = locator.getRight();
    return MethodResult.of(
        ImmutableMap.of(
            "x1", a.getX(),
            "y1", a.getY(),
            "z1", a.getZ(),
            "x2", b.getX(),
            "y2", b.getY(),
            "z2", b.getZ()
        ));
  }
}
