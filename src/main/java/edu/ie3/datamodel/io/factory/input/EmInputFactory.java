/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EmInputFactory extends AssetInputEntityFactory<EmInput, EmAssetInputEntityData> {

  private static final String CONTROL_STRATEGY = "controlStrategy";

  public static final String CONTROLLING_EM = "controllingEm";

  public EmInputFactory() {
    super(EmInput.class);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    List<Set<String>> fields = new ArrayList<>(super.getFields(entityClass));

    List<Set<String>> withEm =
        fields.stream().map(f -> (Set<String>) expandSet(f, CONTROLLING_EM)).toList();

    fields.addAll(withEm);

    return fields;
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {CONTROL_STRATEGY};
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
