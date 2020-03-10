/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.EvInput;
import edu.ie3.datamodel.models.input.system.type.EvTypeInput;
import java.util.UUID;

public class EvInputFactory
    extends SystemParticipantInputEntityFactory<
        EvInput, SystemParticipantTypedEntityData<EvTypeInput>> {

  public EvInputFactory() {
    super(EvInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[0];
  }

  @Override
  protected EvInput buildModel(
      SystemParticipantTypedEntityData<EvTypeInput> data,
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      OperatorInput operatorInput,
      OperationTime operationTime) {
    return new EvInput(
        uuid, operationTime, operatorInput, id, node, qCharacteristics, data.getTypeInput());
  }
}
