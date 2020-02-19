/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.models.OperationTime;
import edu.ie3.models.input.MeasurementUnitInput;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import java.util.UUID;

public class MeasurementUnitInputFactory
    extends AssetInputEntityFactory<MeasurementUnitInput, MeasurementUnitInputEntityData> {
  private static final String V_MAG = "vmag";
  private static final String V_ANG = "vang";
  private static final String P = "p";
  private static final String Q = "q";

  public MeasurementUnitInputFactory() {
    super(MeasurementUnitInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {V_MAG, V_ANG, P, Q};
  }

  @Override
  protected MeasurementUnitInput buildModel(
      MeasurementUnitInputEntityData data,
      UUID uuid,
      String id,
      OperatorInput operatorInput,
      OperationTime operationTime) {
    final NodeInput node = data.getNode();
    final boolean vMag = data.getBoolean(V_MAG);
    final boolean vAng = data.getBoolean(V_ANG);
    final boolean p = data.getBoolean(P);
    final boolean q = data.getBoolean(Q);
    return new MeasurementUnitInput(uuid, operationTime, operatorInput, id, node, vMag, vAng, p, q);
  }

  @Override
  protected MeasurementUnitInput buildModel(
      MeasurementUnitInputEntityData data, UUID uuid, String id) {
    final NodeInput node = data.getNode();
    final boolean vMag = data.getBoolean(V_MAG);
    final boolean vAng = data.getBoolean(V_ANG);
    final boolean p = data.getBoolean(P);
    final boolean q = data.getBoolean(Q);
    return new MeasurementUnitInput(uuid, id, node, vMag, vAng, p, q);
  }
}
