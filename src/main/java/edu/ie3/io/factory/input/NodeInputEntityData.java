/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import com.vividsolutions.jts.geom.Point;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.VoltageLevel;
import edu.ie3.models.input.OperatorInput;
import java.util.Map;

public class NodeInputEntityData extends AssetInputEntityData {
  private final Point geoPosition;
  // FIXME maybe this should be parsed inside the factory
  private final VoltageLevel voltageLvl;

  public NodeInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      Point geoPosition,
      VoltageLevel voltageLvl) {
    super(fieldsToAttributes, entityClass);
    this.geoPosition = geoPosition;
    this.voltageLvl = voltageLvl;
  }

  public NodeInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operatorInput,
      Point geoPosition,
      VoltageLevel voltageLvl) {
    super(fieldsToAttributes, entityClass, operatorInput);
    this.geoPosition = geoPosition;
    this.voltageLvl = voltageLvl;
  }

  public Point getGeoPosition() {
    return geoPosition;
  }

  public VoltageLevel getVoltageLvl() {
    return voltageLvl;
  }
}
