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

    try {
      final int parallelDevices = data.getInt(PARALLEL_DEVICES);
      log.warn(
          "Found a `SwitchInput` with the id `{}` that specifies `parallelDevices` with a value of `{}`. Because switches cannot be parallel, the given value is ignored!",
          id,
          parallelDevices);
    } catch (Exception ignored) {
      // because the field should not be used for switches, we can ignore the exception, that is
      // thrown, when we
      // do not find the field
    }

    return new SwitchInput(uuid, id, operator, operationTime, nodeA, nodeB, closed);
  }
}
