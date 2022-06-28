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

/**
 * Abstract class as a common super class of all thermal input models
 */
public abstract class ThermalInput extends AssetInput {
  /**
   * Constructor for a thermal input model
   *
   * @param uuid Unique identifier
   * @param id Human readable identifier
   * @param operator Reference to the operator
   * @param operationTime Time frame, within the asset is in operation
   */
  protected ThermalInput(
      UUID uuid, String id, OperatorInput operator, OperationTime operationTime) {
    super(uuid, id, operator, operationTime);
  }

  /**
   * Constructor for a thermal input model
   *
   * @param uuid Unique identifier
   * @param id Human readable identifier
   */
  protected ThermalInput(UUID uuid, String id) {
    super(uuid, id);
  }
}
