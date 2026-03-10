/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.UUID;

public class MeasurementUnitInputFactory
    extends AssetInputEntityFactory<MeasurementUnitInput, NodeAssetInputEntityData> {

  public MeasurementUnitInputFactory() {
    super(MeasurementUnitInput.class);
  }

  @Override
  protected MeasurementUnitInput buildModel(
      NodeAssetInputEntityData data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {
    final NodeInput node = data.getNode();
    final boolean vMag = data.getBoolean(V_MAG);
    final boolean vAng = data.getBoolean(V_ANG);
    final boolean p = data.getBoolean(P);
    final boolean q = data.getBoolean(Q);
    return new MeasurementUnitInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        vMag,
        vAng,
        p,
        q,
        data.determineAdditionalInformation());
  }
}
