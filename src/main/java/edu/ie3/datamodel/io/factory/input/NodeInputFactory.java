/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import org.locationtech.jts.geom.Point;

public class NodeInputFactory extends AssetInputEntityFactory<NodeInput, AssetInputEntityData> {
  private static final String V_TARGET = "vtarget";
  public static final String V_RATED = "vRated";
  private static final String SLACK = "slack";
  private static final String GEO_POSITION = "geoposition";
  public static final String VOLT_LVL = "voltLvl";
  private static final String SUBNET = "subnet";

  public NodeInputFactory() {
    super(NodeInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {
      V_TARGET, V_RATED.toLowerCase(), SLACK, GEO_POSITION, VOLT_LVL.toLowerCase(), SUBNET
    };
  }

  @Override
  protected NodeInput buildModel(
      AssetInputEntityData data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {
    final Quantity<Dimensionless> vTarget =
        data.getQuantity(V_TARGET, StandardUnits.TARGET_VOLTAGE_MAGNITUDE);
    final boolean slack = data.getBoolean(SLACK);
    final Point geoPosition = data.getPoint(GEO_POSITION).orElse(NodeInput.DEFAULT_GEO_POSITION);
    final VoltageLevel voltLvl = data.getVoltageLvl(VOLT_LVL.toLowerCase(), V_RATED.toLowerCase());
    final int subnet = data.getInt(SUBNET);
    return new NodeInput(
        uuid, id, operator, operationTime, vTarget, slack, geoPosition, voltLvl, subnet);
  }
}
