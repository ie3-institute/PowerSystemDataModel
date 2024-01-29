/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import java.util.UUID;

public class SwitchInputFactory
    extends ConnectorInputEntityFactory<SwitchInput, ConnectorInputEntityData> {
  private static final String CLOSED = "closed";

  public SwitchInputFactory() {
    super(SwitchInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {CLOSED};
  }

  @Override
  protected SwitchInput buildModel(
      ConnectorInputEntityData data,
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      OperatorInput operator,
      OperationTime operationTime) {
    final boolean closed = data.getBoolean(CLOSED);

    if (data.containsKey(PARALLEL_DEVICES)) {
      String parallelDevices = data.getField(PARALLEL_DEVICES);

      log.warn(
          "The `SwitchInput` with the id `{}` specifies the unused parameter `parallelDevices` with a value of `{}`."
              + " SwitchInputs do not need to specify `parallelDevices`, as its value depends on the electrotechnical"
              + " context where the switch is embedded.",
          id,
          parallelDevices);
    }

    return new SwitchInput(uuid, id, operator, operationTime, nodeA, nodeB, closed);
  }
}
