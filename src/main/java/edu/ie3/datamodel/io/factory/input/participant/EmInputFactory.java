/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.io.factory.input.AssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.AssetInputEntityFactory;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.EmInput;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EmInputFactory extends AssetInputEntityFactory<EmInput, AssetInputEntityData> {

  private static final String CONTROL_STRATEGY = "controlstrategy";

  private static final String PARENT_EM = "parentem";

  public EmInputFactory() {
    super(EmInput.class);
  }

  @Override
  protected List<Set<String>> getFields(AssetInputEntityData data) {
    List<Set<String>> fields = new ArrayList<>(super.getFields(data));

    List<Set<String>> withEm =
        fields.stream().map(f -> (Set<String>) expandSet(f, PARENT_EM)).toList();

    fields.addAll(withEm);

    return fields;
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {CONTROL_STRATEGY};
  }

  @Override
  protected EmInput buildModel(
      AssetInputEntityData data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime) {
    String controlStrategy = data.getField(CONTROL_STRATEGY);

    UUID parentEm = null;
    if (data.containsKey(PARENT_EM)) parentEm = data.getUUID(PARENT_EM);

    return new EmInput(uuid, id, operator, operationTime, controlStrategy, parentEm);
  }
}
