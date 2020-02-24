/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.connector;

import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.connector.type.Transformer3WTypeInput;
import java.util.Objects;
import java.util.UUID;

/**
 * Describes a three winding transformer, that is connected to three {@link
 * edu.ie3.models.input.NodeInput}s
 */
public class Transformer3WInput extends TransformerInput {
  /** Type of this 3W transformer, containing default values for transformers of this kind */
  private Transformer3WTypeInput type;
  /** The lower voltage node */
  private NodeInput nodeC;

  /**
   * Constructor for an operated three winding transformer
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param nodeA The higher voltage node
   * @param nodeB The middle voltage node
   * @param nodeC The lower voltage node
   * @param parallelDevices Amount of singular transformers
   * @param type of 3W transformer
   * @param tapPos Tap Position of this transformer
   * @param autoTap
   */
  public Transformer3WInput(
      UUID uuid,
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      NodeInput nodeC,
      int parallelDevices,
      Transformer3WTypeInput type,
      int tapPos,
      boolean autoTap) {
    super(uuid, operationTime, operator, id, nodeA, nodeB, parallelDevices, tapPos, autoTap);
    this.type = type;
    this.nodeC = nodeC;
  }

  /**
   * Constructor for a non-operated three winding transformer
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA The higher voltage node
   * @param nodeB The middle voltage node
   * @param nodeC The lower voltage node
   * @param parallelDevices Amount of singular transformers
   * @param type of 3W transformer
   * @param tapPos Tap Position of this transformer
   * @param autoTap
   */
  public Transformer3WInput(
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      NodeInput nodeC,
      int parallelDevices,
      Transformer3WTypeInput type,
      int tapPos,
      boolean autoTap) {
    super(uuid, id, nodeA, nodeB, parallelDevices, tapPos, autoTap);
    this.type = type;
    this.nodeC = nodeC;
  }

  public Transformer3WTypeInput getType() {
    return type;
  }

  public void setType(Transformer3WTypeInput type) {
    this.type = type;
  }

  /** @return the lower voltage node */
  public NodeInput getNodeC() {
    return nodeC;
  }

  /** @param nodeC The lower voltage node */
  public void setNodeC(NodeInput nodeC) {
    this.nodeC = nodeC;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Transformer3WInput that = (Transformer3WInput) o;
    return Objects.equals(type, that.type) && Objects.equals(nodeC, that.nodeC);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, nodeC);
  }
}
