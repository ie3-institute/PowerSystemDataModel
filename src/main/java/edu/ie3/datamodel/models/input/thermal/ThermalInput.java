/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.UUID;

public abstract class ThermalInput extends AssetInput {
  protected ThermalInput(
      UUID uuid, String id, OperatorInput operator, OperationTime operationTime) {
    super(uuid, id, operator, operationTime);
  }

  protected ThermalInput(UUID uuid, String id) {
    super(uuid, id);
  }
}
