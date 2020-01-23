/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.connector;

import edu.ie3.models.input.AssetInput;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Describes an asset that connects two {@link NodeInput}s */
public abstract class ConnectorInput extends AssetInput {
  /** Grid node at one side of the connector */
  NodeInput nodeA;
  /** Grid node at the other side of the connector */
  NodeInput nodeB;
  /** Amount of parallelDevices */
  Integer parallelDevices;

  /**
   * @param uuid of the input entity
   * @param operationInterval Empty for a non-operated asset, Interval of operation period else
   * @param operator of the asset
   * @param id of the asset
   * @param nodeA Grid node at one side of the connector
   * @param nodeB Grid node at the other side of the connector
   * @param parallelDevices Amount of parallel devices
   */
  public ConnectorInput(
      UUID uuid,
      Optional<ClosedInterval<ZonedDateTime>> operationInterval,
      OperatorInput operator,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      Integer parallelDevices) {
    super(uuid, operationInterval, operator, id);
    this.nodeA = nodeA;
    this.nodeB = nodeB;
    this.parallelDevices = parallelDevices;
  }

  /**
   * If both operatesFrom and operatesUntil are Empty, it is assumed that the asset is non-operated.
   *
   * @param uuid of the input entity
   * @param operatesFrom start of operation period, will be replaced by LocalDateTime.MIN if Empty
   * @param operatesUntil end of operation period, will be replaced by LocalDateTime.MAX if Empty
   * @param operator of the asset
   * @param id of the asset
   * @param nodeA Grid node at one side of the connector
   * @param nodeB Grid node at the other side of the connector
   * @param parallelDevices Amount of parallel devices
   */
  public ConnectorInput(
      UUID uuid,
      Optional<ZonedDateTime> operatesFrom,
      Optional<ZonedDateTime> operatesUntil,
      OperatorInput operator,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      Integer parallelDevices) {
    super(uuid, operatesFrom, operatesUntil, operator, id);
    this.nodeA = nodeA;
    this.nodeB = nodeB;
    this.parallelDevices = parallelDevices;
  }

  /**
   * Constructor for a non-operated asset
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA Grid node at one side of the connector
   * @param nodeB Grid node at the other side of the connector
   * @param parallelDevices Amount of parallel devices
   */
  public ConnectorInput(
      UUID uuid, String id, NodeInput nodeA, NodeInput nodeB, Integer parallelDevices) {
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

  public Integer getParallelDevices() {
    return parallelDevices;
  }

  public void setParallelDevices(Integer parallelDevices) {
    this.parallelDevices = parallelDevices;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ConnectorInput that = (ConnectorInput) o;
    return Objects.equals(nodeA, that.nodeA)
        && Objects.equals(nodeB, that.nodeB)
        && Objects.equals(parallelDevices, that.parallelDevices);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), nodeA, nodeB, parallelDevices);
  }
}
