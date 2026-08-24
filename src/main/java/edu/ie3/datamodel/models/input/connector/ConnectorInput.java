/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import edu.ie3.datamodel.io.extractor.HasNodes;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Describes an asset that connects two {@link NodeInput}s. */
public abstract class ConnectorInput extends AssetInput implements HasNodes {
  /** Grid node at one side of the connector. */
  private final NodeInput nodeA;

  /** Grid node at the other side of the connector. */
  private final NodeInput nodeB;

  /** Amount of parallel devices. */
  private final int parallelDevices;

  /**
   * Constructor for an operated connector.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param nodeA Grid node at one side of the connector
   * @param nodeB Grid node at the other side of the connector
   * @param parallelDevices overall amount of parallel devices to automatically construct (e.g.
   *     parallelDevices = 2 will build a total of two entities using the specified parameters)
   */
  protected ConnectorInput(
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
   * Constructor for an operated connector with no parallel devices (parallelDevices=1).
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param nodeA Grid node at one side of the connector
   * @param nodeB Grid node at the other side of the connector
   */
  protected ConnectorInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput nodeA,
      NodeInput nodeB) {
    super(uuid, id, operator, operationTime);
    this.nodeA = nodeA;
    this.nodeB = nodeB;
    this.parallelDevices = 1;
  }

  /**
   * Constructor for an operated connector.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA Grid node at one side of the connector
   * @param nodeB Grid node at the other side of the connector
   * @param parallelDevices overall amount of parallel devices to automatically construct (e.g.
   *     parallelDevices = 2 will build a total of two entities using the specified parameters)
   */
  protected ConnectorInput(
      UUID uuid, String id, NodeInput nodeA, NodeInput nodeB, int parallelDevices) {
    super(uuid, id);
    this.nodeA = nodeA;
    this.nodeB = nodeB;
    this.parallelDevices = parallelDevices;
  }

  /**
   * Constructor for an operated connector with no parallel devices (parallelDevices=1).
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA Grid node at one side of the connector
   * @param nodeB Grid node at the other side of the connector
   */
  protected ConnectorInput(UUID uuid, String id, NodeInput nodeA, NodeInput nodeB) {
    super(uuid, id);
    this.nodeA = nodeA;
    this.nodeB = nodeB;
    this.parallelDevices = 1;
  }

  public NodeInput getNodeA() {
    return nodeA;
  }

  public NodeInput getNodeB() {
    return nodeB;
  }

  public int getParallelDevices() {
    return parallelDevices;
  }

  @Override
  public List<NodeInput> allNodes() {
    return List.of(getNodeA(), getNodeB());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ConnectorInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(nodeA, that.nodeA)
        && Objects.equals(nodeB, that.nodeB)
        && parallelDevices == that.parallelDevices;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), nodeA, nodeB, parallelDevices);
  }

  @Override
  public String toString() {
    return "ConnectorInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", nodeA="
        + nodeA.getUuid()
        + ", nodeB="
        + nodeB.getUuid()
        + ", parallelDevices="
        + parallelDevices
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public abstract ConnectorInputCopyBuilder<?> copy();

  public abstract static class ConnectorInputCopyBuilder<B extends ConnectorInputCopyBuilder<B>>
      extends AssetInputCopyBuilder<B> {
    private NodeInput nodeA;

    private NodeInput nodeB;

    private int parallelDevices;

    protected ConnectorInputCopyBuilder(ConnectorInput entity) {
      super(entity);
      this.nodeA = entity.nodeA;
      this.nodeB = entity.nodeB;
      this.parallelDevices = entity.parallelDevices;
    }

    public B nodeA(NodeInput nodeA) {
      this.nodeA = nodeA;
      return thisInstance();
    }

    protected NodeInput getNodeA() {
      return nodeA;
    }

    public B nodeB(NodeInput nodeB) {
      this.nodeB = nodeB;
      return thisInstance();
    }

    protected NodeInput getNodeB() {
      return nodeB;
    }

    public B parallelDevices(int parallelDevices) {
      this.parallelDevices = parallelDevices;
      return thisInstance();
    }

    protected int getParallelDevices() {
      return parallelDevices;
    }

    @Override
    public abstract ConnectorInput build();

    @Override
    protected abstract B thisInstance();
  }
}
