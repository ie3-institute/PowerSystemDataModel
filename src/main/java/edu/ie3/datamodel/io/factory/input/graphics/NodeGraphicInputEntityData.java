/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.graphics;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import java.util.Map;
import java.util.Objects;

/**
 * Data used by {@link NodeGraphicInputFactory} used to create instances of {@link
 * edu.ie3.datamodel.models.input.graphics.NodeGraphicInput}s holding one {@link NodeInput} entity.
 */
public class NodeGraphicInputEntityData extends EntityData {

  /** The NodeInput to this graphic data */
  private final NodeInput node;

  /**
   * Creates a new NodeGraphicInputentityData object for an a NodeGraphicInput
   *
   * @param fieldsToAttributes containing mapping of field name to value
   * @param node node input element of this graphic
   */
  public NodeGraphicInputEntityData(Map<String, String> fieldsToAttributes, NodeInput node) {
    super(fieldsToAttributes, NodeGraphicInput.class);
    this.node = node;
  }

  public NodeInput getNode() {
    return node;
  }

  @Override
  public String toString() {
    return "NodeGraphicInputEntityData{"
        + "node="
        + node.getUuid()
        + ", fieldsToValues="
        + getFieldsToValues()
        + ", targetClass="
        + getTargetClass()
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    NodeGraphicInputEntityData that = (NodeGraphicInputEntityData) o;
    return getNode().equals(that.getNode());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getNode());
  }
}
