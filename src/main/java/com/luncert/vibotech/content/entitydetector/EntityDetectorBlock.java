package com.luncert.vibotech.content.entitydetector;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

import com.luncert.vibotech.index.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class EntityDetectorBlock extends Block implements IBE<EntityDetectorBlockEntity> {

  public EntityDetectorBlock(Properties properties) {
    super(properties);
  }

  @Override
  public Class<EntityDetectorBlockEntity> getBlockEntityClass() {
    return EntityDetectorBlockEntity.class;
  }

  @Override
  public BlockEntityType<? extends EntityDetectorBlockEntity> getBlockEntityType() {
    return AllBlockEntityTypes.ENTITY_DETECTOR.get();
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
    super.createBlockStateDefinition(builder);
  }
}
