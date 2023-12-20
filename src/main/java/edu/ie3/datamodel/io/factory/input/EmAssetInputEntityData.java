/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Map;

/**
 * Data used for the construction of {@link edu.ie3.datamodel.models.input.AssetInput} entities
 * which also require an EM attribute. This data object can include additional information about the
 * {@link EmInput}, which cannot be provided through the attribute map as it is a complex shared
 * entity.
 */
public class EmAssetInputEntityData extends AssetInputEntityData {

  private final EmInput emUnit;

  public EmAssetInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      EmInput emUnit) {
    super(fieldsToAttributes, entityClass);
    this.emUnit = emUnit;
  }

  public EmAssetInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operator,
      EmInput emUnit) {
    super(fieldsToAttributes, entityClass, operator);
    this.emUnit = emUnit;
  }

  public EmInput getEmUnit() {
    return emUnit;
  }

  /**
   * Creates a new EmAssetInputEntityData object based on a given {@link AssetInputEntityData}
   * object and given em unit
   *
   * @param entityData The entity data object to use attributes of
   * @param emUnit The em input to use
   */
  public EmAssetInputEntityData(AssetInputEntityData entityData, EmInput emUnit) {
    super(entityData, entityData.getOperatorInput());
    this.emUnit = emUnit;
  }
}
