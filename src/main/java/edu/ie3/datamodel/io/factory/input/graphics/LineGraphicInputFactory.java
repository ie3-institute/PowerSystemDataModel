/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.graphics;

import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import java.util.UUID;
import org.locationtech.jts.geom.LineString;

/**
 * Factory to create {@link LineGraphicInput} entities
 *
 * @version 0.1
 * @since 08.04.20
 */
public final class LineGraphicInputFactory
    extends GraphicInputFactory<LineGraphicInput, LineGraphicInputEntityData> {

  public LineGraphicInputFactory() {
    super(LineGraphicInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[0];
  }

  @Override
  protected LineGraphicInput buildModel(
      LineGraphicInputEntityData data, UUID uuid, String graphicLayer, LineString path) {
    return new LineGraphicInput(uuid, graphicLayer, path, data.getLine());
  }
}
