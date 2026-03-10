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

public class EmInputFactory extends AssetInputEntityFactory<EmInput, EmAssetInputEntityData> {

  public EmInputFactory() {
    super(EmInput.class);
  }

  @Override
  protected EmInput buildModel(
      EmAssetInputEntityData data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {
    String controlStrategy = data.getField(CONTROL_STRATEGY);

    EmInput parentEm = data.getControllingEm();

    return new EmInput(uuid, id, operator, operationTime, controlStrategy, parentEm);
  }
}
