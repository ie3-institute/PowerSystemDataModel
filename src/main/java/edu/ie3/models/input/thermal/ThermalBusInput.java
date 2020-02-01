/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.thermal;

import edu.ie3.models.OperationTime;
import edu.ie3.models.input.AssetInput;
import edu.ie3.models.input.OperatorInput;
import java.util.UUID;

/** A thermal bus, to which different {@link ThermalUnitInput} units may be connected */
public class ThermalBusInput extends AssetInput {
  /**
   * Constructor for an operated thermal bus
   *
   * @param uuid Unique identifier of a certain thermal bus
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   */
  public ThermalBusInput(
      UUID uuid, OperationTime operationTime, OperatorInput operator, String id) {
    super(uuid, operationTime, operator, id);
  }

  /**
   * Constructor for a non-operated thermal bus
   *
   * @param uuid Unique identifier of a certain thermal bus
   * @param id of the asset
   */
  public ThermalBusInput(UUID uuid, String id) {
    super(uuid, id);
  }
}
