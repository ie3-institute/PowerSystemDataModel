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
import java.util.Map;
import java.util.UUID;

public class SwitchInputFactory extends ConnectorInputEntityFactory<SwitchInput> {

  public SwitchInputFactory(Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes) {
    super(operators, nodes, SwitchInput.class);
  }

  @Override
  protected SwitchInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      OperatorInput operator,
      OperationTime operationTime) {
    boolean closed = getBoolean(data, CLOSED);

    if (data.containsKey(PARALLEL_DEVICES)) {
      String parallelDevices = getField(data, PARALLEL_DEVICES);

      log.warn(
          "The SwitchInput with id `{}` specifies the unused parameter `parallelDevices` with a value of `{}`."
              + " SwitchInputs do not need to specify `parallelDevices`, as its physical value depends on"
              + " the electrotechnical context of where the switch is embedded.",
          id,
          parallelDevices);
    }

    return new SwitchInput(uuid, id, operator, operationTime, nodeA, nodeB, closed, data);
  }
}
