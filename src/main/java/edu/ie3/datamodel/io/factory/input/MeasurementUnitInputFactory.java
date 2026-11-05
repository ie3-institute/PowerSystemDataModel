/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.io.factory.FieldMap;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Map;
import java.util.UUID;

public class MeasurementUnitInputFactory extends AssetInputEntityFactory<MeasurementUnitInput> {

  private final Map<UUID, NodeInput> nodes;

  public MeasurementUnitInputFactory(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes) {
    super(operators, MeasurementUnitInput.class);
    this.nodes = nodes;
  }

  @Override
  protected MeasurementUnitInput buildModel(
      FieldMap data, UUID uuid, String id, OperatorInput operator, OperationTime operationTime) {
    NodeInput node = data.getEntity(NODE, nodes);
    boolean vMag = data.getBoolean(V_MAG);
    boolean vAng = data.getBoolean(V_ANG);
    boolean p = data.getBoolean(P);
    boolean q = data.getBoolean(Q);
    return new MeasurementUnitInput(
        uuid, id, operator, operationTime, node, vMag, vAng, p, q, data.additionalInformation());
  }
}
