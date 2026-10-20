/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.io.factory.FieldMap;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import java.util.Map;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;

public class NodeInputFactory extends AssetInputEntityFactory<NodeInput> {

  public NodeInputFactory(Map<UUID, OperatorInput> operators) {
    super(operators, NodeInput.class);
  }

  @Override
  protected NodeInput buildModel(
      FieldMap data, UUID uuid, String id, OperatorInput operator, OperationTime operationTime) {
    ComparableQuantity<Dimensionless> vTarget =
        data.getQuantity(V_TARGET, StandardUnits.TARGET_VOLTAGE_MAGNITUDE);
    boolean slack = data.getBoolean(SLACK);
    Point geoPosition = data.getPoint(GEO_POSITION).orElse(NodeInput.DEFAULT_GEO_POSITION);
    VoltageLevel voltLvl = data.getVoltageLvl(VOLT_LVL, V_RATED);
    int subnet = data.getInt(SUBNET);

    return new NodeInput(
        uuid,
        id,
        operator,
        operationTime,
        vTarget,
        slack,
        geoPosition,
        voltLvl,
        subnet,
        data.additionalInformation());
  }
}
