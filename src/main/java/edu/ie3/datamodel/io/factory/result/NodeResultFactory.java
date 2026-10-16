/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.NodeResult;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import tech.units.indriya.ComparableQuantity;

public class NodeResultFactory extends ResultEntityFactory<NodeResult, NodeResult> {

  public NodeResultFactory() {
    super(NodeResult.class);
  }

  @Override
  protected NodeResult buildModel(Map<String, String> data, ZonedDateTime time, UUID inputModel) {
    ComparableQuantity<Dimensionless> vMagValue =
        getQuantity(data, V_MAG, StandardUnits.VOLTAGE_MAGNITUDE);
    ComparableQuantity<Angle> vAngValue = getQuantity(data, V_ANG, StandardUnits.VOLTAGE_ANGLE);

    return new NodeResult(time, inputModel, vMagValue, vAngValue);
  }
}
