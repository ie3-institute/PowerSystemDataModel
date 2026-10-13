/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.io.factory.input.AssetInputEntityFactory;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.EvInput;
import java.util.UUID;

public class EvInputFactory extends AssetInputEntityFactory<EvInput, EvInputEntityData> {

  public EvInputFactory() {
    super(EvInput.class);
  }

  @Override
  protected EvInput buildModel(
      EvInputEntityData data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {
    return new EvInput(
        uuid, id, operator, operationTime, data.getTypeInput(), data.getFieldsToValues());
  }
}
