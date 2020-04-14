/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.graphics;

import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import java.util.UUID;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

/**
 * Factory to create {@link NodeGraphicInput} entities
 *
 * @version 0.1
 * @since 08.04.20
 */
public final class NodeGraphicInputFactory
    extends GraphicInputFactory<NodeGraphicInput, NodeGraphicInputEntityData> {

  private static final String POINT = "point";

  public NodeGraphicInputFactory() {
    super(NodeGraphicInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {POINT};
  }

  @Override
  protected NodeGraphicInput buildModel(
      NodeGraphicInputEntityData data, UUID uuid, String graphicLayer, LineString pathLineString) {
    final Point point = data.getPoint(POINT).orElse(NodeInput.DEFAULT_GEO_POSITION);
    return new NodeGraphicInput(uuid, graphicLayer, pathLineString, data.getNode(), point);
  }
}
