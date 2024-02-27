/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.UniqueInputEntity;
import java.util.Map;
import java.util.Objects;

/**
 * Data used by all factories used to create instances of {@link UniqueInputEntity}s holding one
 * {@link NodeInput} entity, thus needing additional information about the {@link NodeInput}, which
 * cannot be provided through the attribute map.
 */
public class NodeAssetInputEntityData extends AssetInputEntityData {
  private final NodeInput node;

  /**
   * Creates a new UntypedSingleNodeEntityData object for an operated, always on system participant
   * input
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param entityClass class of the entity to be created with this data
   * @param node input node
   */
  public NodeAssetInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      NodeInput node) {
    super(fieldsToAttributes, entityClass);
    this.node = node;
  }

  /**
   * Creates a new UntypedSingleNodeEntityData object for an operable system participant input
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param entityClass class of the entity to be created with this data
   * @param node input node
   * @param operator operator input
   */
  public NodeAssetInputEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operator,
      NodeInput node) {
    super(fieldsToAttributes, entityClass, operator);
    this.node = node;
  }

  /**
   * Creates a new NodeAssetInputEntityData object based on a given {@link AssetInputEntityData}
   * object and given node
   *
   * @param assetInputEntityData The asset entity data object to use attributes of
   * @param node input node
   */
  public NodeAssetInputEntityData(AssetInputEntityData assetInputEntityData, NodeInput node) {
    super(assetInputEntityData, assetInputEntityData.getOperatorInput());
    this.node = node;
  }

  public NodeInput getNode() {
    return node;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    NodeAssetInputEntityData that = (NodeAssetInputEntityData) o;
    return getNode().equals(that.getNode());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getNode());
  }

  @Override
  public String toString() {
    return "NodeAssetInputEntityData{"
        + "fieldsToValues="
        + getFieldsToValues()
        + ", targetClass="
        + getTargetClass()
        + ", operatorInput="
        + getOperatorInput().getUuid()
        + ", node="
        + node.getUuid()
        + '}';
  }
}
