/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;

import javax.measure.quantity.Dimensionless;
import java.util.Map;
import java.util.UUID;

public class NodeInputFactory extends AssetInputEntityFactory<NodeInput> {

  public NodeInputFactory(Map<UUID, OperatorInput> operators) {
    super(operators, NodeInput.class);
  }

  @Override
  protected NodeInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {
    ComparableQuantity<Dimensionless> vTarget = getQuantity(data, V_TARGET, StandardUnits.TARGET_VOLTAGE_MAGNITUDE);
    boolean slack = getBoolean(data, SLACK);
    Point geoPosition = getPoint(data, GEO_POSITION).orElse(NodeInput.DEFAULT_GEO_POSITION);
    VoltageLevel voltLvl = getVoltageLvl(data, VOLT_LVL, V_RATED);
    int subnet = getInt(data, SUBNET);

    return new NodeInput(
        uuid, id, operator, operationTime, vTarget, slack, geoPosition, voltLvl, subnet, data);
  }
}
