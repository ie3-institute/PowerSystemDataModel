/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.io.factory.UniqueEntityFactory;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Map;

public class OperatorInputFactory extends UniqueEntityFactory<OperatorInput, OperatorInput> {

  public OperatorInputFactory() {
    super(OperatorInput.class);
  }

  @Override
  protected OperatorInput buildModel(Map<String, String> data) {
    return new OperatorInput(getUUID(data, UUID), getField(data, ID), data);
  }
}
