/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class OperatorInputFactory extends EntityFactory<OperatorInput, EntityData> {

  private static final String ENTITY_UUID = "uuid";
  private static final String ENTITY_ID = "id";

  public OperatorInputFactory() {
    super(OperatorInput.class);
  }

  @Override
  protected List<Set<String>> getFields(EntityData data) {
    Set<String> constructorParams = newSet(ENTITY_UUID, ENTITY_ID);
    return Collections.singletonList(constructorParams);
  }

  @Override
  protected OperatorInput buildModel(EntityData data) {
    return new OperatorInput(data.getUUID(ENTITY_UUID), data.getField(ENTITY_ID));
  }
}
