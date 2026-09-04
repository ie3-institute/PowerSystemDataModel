/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import edu.ie3.datamodel.utils.SquantsBuilder;
import java.util.UUID;
import org.locationtech.jts.geom.Point;
import squants.Each;

public class NodeInputFactory extends AssetInputEntityFactory<NodeInput, AssetInputEntityData> {

  public NodeInputFactory() {
    super(NodeInput.class);
  }

  @Override
  protected NodeInput buildModel(
      AssetInputEntityData data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {
    final squants.Dimensionless vTarget =
        SquantsBuilder.build(data.getDouble(V_TARGET), Each::apply);
    final boolean slack = data.getBoolean(SLACK);
    final Point geoPosition = data.getPoint(GEO_POSITION).orElse(NodeInput.DEFAULT_GEO_POSITION);
    final VoltageLevel voltLvl = data.getVoltageLvl(VOLT_LVL.toLowerCase(), V_RATED.toLowerCase());
    final int subnet = data.getInt(SUBNET);
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
        data.getFieldsToValues());
  }
}
