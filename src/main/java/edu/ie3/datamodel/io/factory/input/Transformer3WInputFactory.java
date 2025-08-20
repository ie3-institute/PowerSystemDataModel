/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import java.util.UUID;

/** The type Transformer 3 w input factory. */
public class Transformer3WInputFactory
    extends ConnectorInputEntityFactory<Transformer3WInput, Transformer3WInputEntityData> {

  private static final String TAP_POS = "tapPos";
  private static final String AUTO_TAP = "autoTap";

  /** Instantiates a new Transformer 3 w input factory. */
  public Transformer3WInputFactory() {
    super(Transformer3WInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {PARALLEL_DEVICES, TAP_POS, AUTO_TAP};
  }

  @Override
  protected Transformer3WInput buildModel(
      Transformer3WInputEntityData data,
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      OperatorInput operator,
      OperationTime operationTime) {
    final int parallelDevices = data.getInt(PARALLEL_DEVICES);
    final NodeInput nodeC = data.getNodeC();
    final Transformer3WTypeInput type = data.getType();
    final int tapPos = data.getInt(TAP_POS);
    final boolean autoTap = data.getBoolean(AUTO_TAP);

    return new Transformer3WInput(
        uuid,
        id,
        operator,
        operationTime,
        nodeA,
        nodeB,
        nodeC,
        parallelDevices,
        type,
        tapPos,
        autoTap);
  }
}
