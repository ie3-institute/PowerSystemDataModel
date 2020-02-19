/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import com.vividsolutions.jts.geom.Point;
import edu.ie3.models.OperationTime;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.VoltageLevel;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricPotential;

public class NodeInputFactory extends AssetInputEntityFactory<NodeInput, NodeInputEntityData> {
  private static final String V_TARGET = "vtarget";
  private static final String V_RATED = "vrated";
  private static final String SLACK = "slack";
  private static final String GEO_POSITION = "geoposition";
  private static final String VOLT_LVL = "voltlvl";
  private static final String SUBNET = "subnet";

  public NodeInputFactory() {
    super(NodeInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {V_TARGET, V_RATED, SLACK, GEO_POSITION, VOLT_LVL, SUBNET};
  }

  @Override
  protected NodeInput buildModel(
      NodeInputEntityData data,
      UUID uuid,
      String id,
      OperatorInput operatorInput,
      OperationTime operationTime) {
    final Quantity<Dimensionless> vTarget =
        data.getQuantity(V_TARGET, StandardUnits.TARGET_VOLTAGE_MAGNITUDE);
    final Quantity<ElectricPotential> vRated =
        data.getQuantity(V_RATED, StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    final boolean slack = data.getBoolean(SLACK);
    final Point geoPosition = data.getGeoPosition();
    final VoltageLevel voltLvl = data.getVoltageLvl();
    final int subnet = data.getInt(SUBNET);
    return new NodeInput(
        uuid,
        operationTime,
        operatorInput,
        id,
        vTarget,
        vRated,
        slack,
        geoPosition,
        voltLvl,
        subnet);
  }

  @Override
  protected NodeInput buildModel(NodeInputEntityData data, UUID uuid, String id) {
    final Quantity<Dimensionless> vTarget =
        data.getQuantity(V_TARGET, StandardUnits.TARGET_VOLTAGE_MAGNITUDE);
    final Quantity<ElectricPotential> vRated =
        data.getQuantity(V_RATED, StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    final boolean slack = data.getBoolean(SLACK);
    final Point geoPosition = data.getGeoPosition();
    final VoltageLevel voltLvl = data.getVoltageLvl();
    final int subnet = data.getInt(SUBNET);
    return new NodeInput(uuid, id, vTarget, vRated, slack, geoPosition, voltLvl, subnet);
  }
}
