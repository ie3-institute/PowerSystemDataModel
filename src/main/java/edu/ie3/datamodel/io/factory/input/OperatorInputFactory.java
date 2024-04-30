/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.UniqueEntityFactory;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class OperatorInputFactory extends UniqueEntityFactory<OperatorInput, EntityData> {

  public OperatorInputFactory() {
    super(OperatorInput.class);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    Set<String> constructorParams = newSet(UUID, ID);
    return Collections.singletonList(constructorParams);
  }

  @Override
  protected OperatorInput buildModel(EntityData data) {
    return new OperatorInput(data.getUUID(UUID), data.getField(ID));
  }
}
