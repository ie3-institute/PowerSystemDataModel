/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system;

import edu.ie3.models.OperationTime;
import edu.ie3.models.input.AssetInput;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import java.util.Objects;
import java.util.UUID;

/** Describes a system asset that is connected to a node */
public abstract class SystemParticipantInput extends AssetInput {

  /** The node that the asset is connected to */
  private NodeInput node;

  /** Description of a reactive power characteristic. For details see further documentation */
  private String qCharacteristics;

  /**
   * Constructor for an operated system participant
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   */
  public SystemParticipantInput(
      UUID uuid,
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics) {
    super(uuid, operationTime, operator, id);
    this.node = node;
    this.qCharacteristics = qCharacteristics;
  }

  /**
   * Constructor for a non-operated system participant
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   */
  public SystemParticipantInput(UUID uuid, String id, NodeInput node, String qCharacteristics) {
    super(uuid, id);
    this.node = node;
    this.qCharacteristics = qCharacteristics;
  }

  public NodeInput getNode() {
    return node;
  }

  public void setNode(NodeInput node) {
    this.node = node;
  }

  public String getQCharacteristics() {
    return qCharacteristics;
  }

  public void setQCharacteristics(String qCharacteristics) {
    this.qCharacteristics = qCharacteristics;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SystemParticipantInput that = (SystemParticipantInput) o;
    return Objects.equals(node, that.node)
        && Objects.equals(qCharacteristics, that.qCharacteristics);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), node, qCharacteristics);
  }
}
