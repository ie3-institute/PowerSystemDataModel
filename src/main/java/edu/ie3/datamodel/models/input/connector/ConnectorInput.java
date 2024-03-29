/*
 * © 2021. TU Dortmund University,
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
   * Constructor for an operated, always on connector
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

  public NodeInput getNodeA() {
    return nodeA;
  }

  public NodeInput getNodeB() {
    return nodeB;
  }

  @Override
  public abstract ConnectorInputCopyBuilder<?> copy();

  @Override
  public List<NodeInput> allNodes() {
    return List.of(getNodeA(), getNodeB());
  }

  public int getParallelDevices() {
    return parallelDevices;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ConnectorInput that)) return false;
    if (!super.equals(o)) return false;
    return parallelDevices == that.parallelDevices
        && nodeA.equals(that.nodeA)
        && nodeB.equals(that.nodeB);
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
        + ", noOfParallelDevices="
        + parallelDevices
        + '}';
  }

  /**
   * Abstract class for all builder that build child entities of abstract class {@link
   * ConnectorInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public abstract static class ConnectorInputCopyBuilder<B extends ConnectorInputCopyBuilder<B>>
      extends AssetInputCopyBuilder<B> {

    private NodeInput nodeA;
    private NodeInput nodeB;
    private int parallelDevices;

    protected ConnectorInputCopyBuilder(ConnectorInput entity) {
      super(entity);
      this.nodeA = entity.getNodeA();
      this.nodeB = entity.getNodeB();
      this.parallelDevices = entity.getParallelDevices();
    }

    public B nodeA(NodeInput nodeA) {
      this.nodeA = nodeA;
      return thisInstance();
    }

    public B nodeB(NodeInput nodeB) {
      this.nodeB = nodeB;
      return thisInstance();
    }

    public B parallelDevices(int parallelDevices) {
      this.parallelDevices = parallelDevices;
      return thisInstance();
    }

    protected NodeInput getNodeA() {
      return nodeA;
    }

    protected NodeInput getNodeB() {
      return nodeB;
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
