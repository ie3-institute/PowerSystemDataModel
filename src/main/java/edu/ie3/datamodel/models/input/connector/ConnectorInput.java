/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import edu.ie3.datamodel.io.extractor.HasNodes;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.*;

/** Describes an asset that connects two {@link NodeInput}s */
public abstract class ConnectorInput extends AssetInput implements HasNodes {
  /** Grid node at one side of the connector */
  private final NodeInput nodeA;
  /** Grid node at the other side of the connector */
  private final NodeInput nodeB;
  /** Amount of parallelDevices */
  private final int parallelDevices;

  /**
   * Constructor for an operated connector
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param nodeA Grid node at one side of the connector
   * @param nodeB Grid node at the other side of the connector
   * @param parallelDevices Amount of parallel devices
   */
  public ConnectorInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices) {
    super(uuid, id, operator, operationTime);
    this.nodeA = nodeA;
    this.nodeB = nodeB;
    this.parallelDevices = parallelDevices;
  }

  /**
   * Constructor for an operated, always on connector
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

  public NodeInput getNodeB() {
    return nodeB;
  }

  @Override
  public List<NodeInput> allNodes() {
    return Collections.unmodifiableList(Arrays.asList(getNodeA(), getNodeB()));
  }

  public int getParallelDevices() {
    return parallelDevices;
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

  @Override
  public String toString() {
    return "ConnectorInput{"
        + "nodeA="
        + nodeA
        + ", nodeB="
        + nodeB
        + ", noOfParallelDevices="
        + parallelDevices
        + '}';
  }
}
