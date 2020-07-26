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

@Deprecated
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

  public EvcsInputCopyBuilder copy() {
    return new EvcsInputCopyBuilder(this);
  }

  /**
   * A builder pattern based approach to create copies of {@link EvcsInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link EvcsInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class EvcsInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<EvcsInputCopyBuilder> {

    private EvcsInputCopyBuilder(EvcsInput entity) {
      super(entity);
    }

    @Override
    public EvcsInput build() {
      return new EvcsInput(
          getUuid(), getId(), getOperator(), getOperationTime(), getNode(), getqCharacteristics());
    }

    @Override
    protected EvcsInputCopyBuilder childInstance() {
      return this;
    }
  }
}
