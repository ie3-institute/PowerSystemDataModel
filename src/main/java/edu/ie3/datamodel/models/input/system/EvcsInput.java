/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import java.util.UUID;

public class EvcsInput extends SystemParticipantInput {

  /**
   * Dummy constructor
   *
   * @param uuid Unique identifier
   * @param id Human readable identifier
   * @deprecated only added to remove compile error. Please implement a real constructor
   */
  @Deprecated
  public EvcsInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics) {
    super(uuid, id, operator, operationTime, node, qCharacteristics);
  }

  // TODO please fill the void inside me :'(

}
