package com.luncert.vibotech.content.airpipe;

import com.luncert.vibotech.content.airpipe.AirPipeBlockEntity.TransportAirBehaviour;
import com.mojang.logging.LogUtils;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.fluids.FluidTransportBehaviour.AttachmentTypes;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.model.BakedModelWrapperWithData;
import com.simibubi.create.foundation.utility.Iterate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class PipeAttachmentModel extends BakedModelWrapperWithData {

  private static final Logger LOGGER = LogUtils.getLogger();

  private static final ModelProperty<PipeModelData> PIPE_PROPERTY = new ModelProperty<>();

  public PipeAttachmentModel(BakedModel template) {
    super(template);
  }

  @Override
  protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData modelData) {
    PipeModelData data = new PipeModelData();
    TransportAirBehaviour transport = BlockEntityBehaviour.get(world, pos, TransportAirBehaviour.TYPE);
    BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get(world, pos, BracketedBlockEntityBehaviour.TYPE);

    LOGGER.info("asd {}", transport);
    if (transport != null) {
      for (Direction d : Iterate.directions) {
        data.putAttachment(d, transport.getRenderedRimAttachment(world, pos, state, d));
      }
    }
    if (bracket != null) {
      data.putBracket(bracket.getBracket());
    }

    // data.setEncased(AirPipeBlock.shouldDrawCasing(world, pos, state));
    return builder.with(PIPE_PROPERTY, data);
  }

  // TODO: Update once MinecraftForge#9163 is merged
  @Override
  public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
    ChunkRenderTypeSet set = super.getRenderTypes(state, rand, data);
    if (set.isEmpty()) {
      return ItemBlockRenderTypes.getRenderLayers(state);
    }
    return set;
  }

  @Override
  public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderType) {
    List<BakedQuad> quads = super.getQuads(state, side, rand, data, renderType);
    if (data.has(PIPE_PROPERTY)) {
      PipeModelData pipeData = data.get(PIPE_PROPERTY);
      quads = new ArrayList<>(quads);
      addQuads(quads, state, side, rand, data, pipeData, renderType);
    }
    return quads;
  }

  private void addQuads(List<BakedQuad> quads, BlockState state, Direction side, RandomSource rand, ModelData data,
                        PipeModelData pipeData, RenderType renderType) {
    BakedModel bracket = pipeData.getBracket();
    if (bracket != null)
      quads.addAll(bracket.getQuads(state, side, rand, data, renderType));
    for (Direction d : Iterate.directions) {
      AttachmentTypes type = pipeData.getAttachment(d);
      for (AttachmentTypes.ComponentPartials partial : type.partials) {
        quads.addAll(AllPartialModels.PIPE_ATTACHMENTS.get(partial)
            .get(d)
            .get()
            .getQuads(state, side, rand, data, renderType));
      }
    }
    // if (pipeData.isEncased())
    //   quads.addAll(AllPartialModels.FLUID_PIPE_CASING.get()
    //       .getQuads(state, side, rand, data, renderType));
  }

  private static class PipeModelData {
    private AttachmentTypes[] attachments;
    private boolean encased;
    private BakedModel bracket;

    public PipeModelData() {
      attachments = new AttachmentTypes[6];
      Arrays.fill(attachments, AttachmentTypes.NONE);
    }

    public void putBracket(BlockState state) {
      if (state != null) {
        this.bracket = Minecraft.getInstance()
            .getBlockRenderer()
            .getBlockModel(state);
      }
    }

    public BakedModel getBracket() {
      return bracket;
    }

    public void putAttachment(Direction face, AttachmentTypes rim) {
      attachments[face.get3DDataValue()] = rim;
    }

    public AttachmentTypes getAttachment(Direction face) {
      return attachments[face.get3DDataValue()];
    }

    public void setEncased(boolean encased) {
      this.encased = encased;
    }

    public boolean isEncased() {
      return encased;
    }
  }
}
