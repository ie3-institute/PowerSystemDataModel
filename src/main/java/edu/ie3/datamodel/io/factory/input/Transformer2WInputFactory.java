/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import java.util.UUID;

public class Transformer2WInputFactory
    extends ConnectorInputEntityFactory<Transformer2WInput, Transformer2WInputEntityData> {

  private static final String TAP_POS = "tappos";
  private static final String AUTO_TAP = "autotap";

  public Transformer2WInputFactory() {
    super(Transformer2WInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {PARALLEL_DEVICES, TAP_POS, AUTO_TAP};
  }

  @Override
  protected Transformer2WInput buildModel(
      Transformer2WInputEntityData data,
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      OperatorInput operator,
      OperationTime operationTime) {
    final int parallelDevices = data.getInt(PARALLEL_DEVICES);
    final Transformer2WTypeInput type = data.getType();
    final int tapPos = data.getInt(TAP_POS);
    final boolean autoTap = data.getBoolean(AUTO_TAP);

    return new Transformer2WInput(
        uuid, id, operator, operationTime, nodeA, nodeB, parallelDevices, type, tapPos, autoTap);
  }
}
