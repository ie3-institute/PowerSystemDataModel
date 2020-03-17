/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.connector;

import edu.ie3.models.OperationTime;
import edu.ie3.models.input.AssetInput;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import java.util.Objects;
import java.util.UUID;

/** Describes an asset that connects two {@link NodeInput}s */
public abstract class ConnectorInput extends AssetInput {
  /** Grid node at one side of the connector */
  private NodeInput nodeA;
  /** Grid node at the other side of the connector */
  private NodeInput nodeB;
  /** Amount of parallelDevices */
  private int parallelDevices;

  /**
   * Constructor for an operated connector
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param nodeA Grid node at one side of the connector
   * @param nodeB Grid node at the other side of the connector
   * @param parallelDevices Amount of parallel devices
   */
  public ConnectorInput(
      UUID uuid,
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices) {
    super(uuid, operationTime, operator, id);
    this.nodeA = nodeA;
    this.nodeB = nodeB;
    this.parallelDevices = parallelDevices;
  }

  /**
   * Constructor for a non-operated connector
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA Grid node at one side of the connector
   * @param nodeB Grid node at the other side of the connector
   * @param parallelDevices Amount of parallel devices
   */
  public ConnectorInput(
      UUID uuid, String id, NodeInput nodeA, NodeInput nodeB, int parallelDevices) {
    super(uuid, id);
    this.nodeA = nodeA;
    this.nodeB = nodeB;
    this.parallelDevices = parallelDevices;
  }

  public NodeInput getNodeA() {
    return nodeA;
  }

  public void setNodeA(NodeInput nodeA) {
    this.nodeA = nodeA;
  }

  public NodeInput getNodeB() {
    return nodeB;
  }

  public void setNodeB(NodeInput nodeB) {
    this.nodeB = nodeB;
  }

  public int getParallelDevices() {
    return parallelDevices;
  }

  public void setParallelDevices(int parallelDevices) {
    this.parallelDevices = parallelDevices;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ConnectorInput that = (ConnectorInput) o;
    return parallelDevices == that.parallelDevices
        && Objects.equals(nodeA, that.nodeA)
        && Objects.equals(nodeB, that.nodeB);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), nodeA, nodeB, parallelDevices);
  }
}
