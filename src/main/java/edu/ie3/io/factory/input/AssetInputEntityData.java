/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.io.factory.EntityData;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.OperatorInput;
import java.util.Map;
import java.util.Optional;

class AssetInputEntityData extends EntityData {

  private final OperatorInput operatorInput;

  public AssetInputEntityData(
      Map<String, String> fieldsToAttributes, Class<? extends UniqueEntity> entityClass) {
    this(fieldsToAttributes, entityClass, null);
  }

  public AssetInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operatorInput) {
    super(fieldsToAttributes, entityClass);
    this.operatorInput = operatorInput;
  }

  public boolean hasOperatorInput() {
    return operatorInput != null;
  }

  public Optional<OperatorInput> getOperatorInput() {
    return Optional.ofNullable(operatorInput);
  }
}
