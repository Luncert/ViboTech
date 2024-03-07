package com.luncert.vibotech.index;

import static com.luncert.vibotech.content.airpipe.AirPipeBlockEntity.TransportAirBehaviour.AttachmentTypes.ComponentPartials;

import com.jozufozu.flywheel.core.PartialModel;
import com.luncert.vibotech.ViboTechMod;
import com.luncert.vibotech.common.Lang;
import com.simibubi.create.foundation.utility.Iterate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Direction;

public class AllPartialModels {

  public static final Map<ComponentPartials, Map<Direction, PartialModel>> PIPE_ATTACHMENTS =
      new EnumMap<>(ComponentPartials.class);
  public static final PartialModel FLUID_PIPE_CASING = block("air_pipe/casing");

  static {
    for (ComponentPartials type : ComponentPartials.values()) {
      Map<Direction, PartialModel> map = new HashMap<>();
      for (Direction d : Iterate.directions) {
        String asId = Lang.asId(type.name());
        map.put(d, block("air_pipe/" + asId + "/" + Lang.asId(d.getSerializedName())));
      }
      PIPE_ATTACHMENTS.put(type, map);
    }
  }

  private static PartialModel block(String path) {
    return new PartialModel(ViboTechMod.asResource("block/" + path));
  }

  public static void init() {
    // init static fields
  }
}
