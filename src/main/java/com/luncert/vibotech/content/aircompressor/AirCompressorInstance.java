package com.luncert.vibotech.content.aircompressor;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class AirCompressorInstance extends KineticBlockEntityInstance<AirCompressorBlockEntity> {

  protected final RotatingData shaft;
  protected final RotatingData fan;
  final Direction direction;

  public AirCompressorInstance(MaterialManager materialManager, AirCompressorBlockEntity blockEntity) {
    super(materialManager, blockEntity);

    direction = blockState.getValue(HORIZONTAL_FACING);

    shaft = getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, blockState, direction).createInstance();
    fan = materialManager.defaultCutout()
        .material(AllMaterialSpecs.ROTATING)
        .getModel(AllPartialModels.ENCASED_FAN_INNER, blockState, direction)
        .createInstance();

    setup(shaft);
    setup(fan);
  }

  @Override
  public void update() {
    updateRotation(shaft);
    updateRotation(fan);
  }

  @Override
  public void updateLight() {
    BlockPos behind = pos.relative(direction);
    relight(behind, shaft);

    BlockPos inFront = pos.relative(direction);
    relight(inFront, fan);
  }

  @Override
  protected void remove() {
    shaft.delete();
    fan.delete();
  }
}
