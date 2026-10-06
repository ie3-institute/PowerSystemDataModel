/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.*;

public class EmInputFactory extends AssetInputEntityFactory<EmInput> {

  private final Map<UUID, EmInput> emUnits;

  public EmInputFactory(Map<UUID, OperatorInput> operators, Map<UUID, EmInput> emUnits) {
    super(operators, EmInput.class);
    this.emUnits = emUnits;
  }

  @Override
  protected EmInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {
    String controlStrategy = getField(data, CONTROL_STRATEGY);
    EmInput parentEm = getEntity(data, CONTROLLING_EM, emUnits, null);

    return new EmInput(uuid, id, operator, operationTime, controlStrategy, parentEm, data);
  }
}
