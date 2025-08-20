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
import java.util.Objects;

/**
 * Data used for the construction of {@link edu.ie3.datamodel.models.input.AssetInput} entities
 * which also require an EM attribute. This data object can include additional information about the
 * {@link EmInput}, which cannot be provided through the attribute map as it is a complex shared
 * entity.
 */
public class EmAssetInputEntityData extends AssetInputEntityData {

  private final EmInput controllingEm;

  /**
   * Instantiates a new Em asset input entity data.
   *
   * @param fieldsToAttributes the fields to attributes
   * @param entityClass the entity class
   * @param controllingEm the controlling em
   */
  public EmAssetInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      EmInput controllingEm) {
    super(fieldsToAttributes, entityClass);
    this.controllingEm = controllingEm;
  }

  /**
   * Instantiates a new Em asset input entity data.
   *
   * @param fieldsToAttributes the fields to attributes
   * @param entityClass the entity class
   * @param operator the operator
   * @param controllingEm the controlling em
   */
  public EmAssetInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operator,
      EmInput controllingEm) {
    super(fieldsToAttributes, entityClass, operator);
    this.controllingEm = controllingEm;
  }

  /**
   * Creates a new EmAssetInputEntityData object based on a given {@link AssetInputEntityData}
   * object and given em unit
   *
   * @param entityData The entity data object to use attributes of
   * @param controllingEm The controlling em input to use
   */
  public EmAssetInputEntityData(AssetInputEntityData entityData, EmInput controllingEm) {
    super(entityData, entityData.getOperatorInput());
    this.controllingEm = controllingEm;
  }

  /**
   * Gets controlling em.
   *
   * @return the controlling em
   */
  public EmInput getControllingEm() {
    return controllingEm;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    EmAssetInputEntityData that = (EmAssetInputEntityData) o;
    return Objects.equals(controllingEm, that.controllingEm);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), controllingEm);
  }
}
