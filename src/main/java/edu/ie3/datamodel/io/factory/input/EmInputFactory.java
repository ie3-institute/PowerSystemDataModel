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

/** The type Em input factory. */
public class EmInputFactory extends AssetInputEntityFactory<EmInput, EmAssetInputEntityData> {

  private static final String CONTROL_STRATEGY = "controlStrategy";

  /** The constant CONTROLLING_EM. */
  public static final String CONTROLLING_EM = "controllingEm";

  /** Instantiates a new Em input factory. */
  public EmInputFactory() {
    super(EmInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {CONTROL_STRATEGY, CONTROLLING_EM};
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
