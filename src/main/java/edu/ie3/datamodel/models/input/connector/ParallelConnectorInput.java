/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import static edu.ie3.datamodel.io.naming.FieldNamingStrategy.*;

import edu.ie3.datamodel.io.extractor.HasNodes;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.utils.ModelConversionUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** Describes an asset that connects two {@link NodeInput}s */
public abstract class ParallelConnectorInput extends ConnectorInput implements HasNodes {

  /** Amount of parallelDevices */
  private final int parallelDevices;

  protected ParallelConnectorInput(
      Map<String, String> data, Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes) {
    super(data, operators, nodes);
    this.parallelDevices = ModelConversionUtils.getInt(data, PARALLEL_DEVICES);
  }

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
  protected ParallelConnectorInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices) {
    super(uuid, id, operator, operationTime, nodeA, nodeB);
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
  protected ParallelConnectorInput(
      UUID uuid, String id, NodeInput nodeA, NodeInput nodeB, int parallelDevices) {
    super(uuid, id, nodeA, nodeB);
    this.parallelDevices = parallelDevices;
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
    if (!(o instanceof ParallelConnectorInput that)) return false;
    if (!super.equals(o)) return false;
    return parallelDevices == that.parallelDevices;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), parallelDevices);
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
        + getNodeA().getUuid()
        + ", nodeB="
        + getNodeB().getUuid()
        + ", noOfParallelDevices="
        + parallelDevices
        + '}';
  }

  /**
   * Abstract class for all builder that build child entities of abstract class {@link
   * ParallelConnectorInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public abstract static class ParallelConnectorInputCopyBuilder<
          B extends ParallelConnectorInputCopyBuilder<B>>
      extends ConnectorInputCopyBuilder<B> {

    private int parallelDevices;

    protected ParallelConnectorInputCopyBuilder(ParallelConnectorInput entity) {
      super(entity);
      this.parallelDevices = entity.getParallelDevices();
    }

    public B parallelDevices(int parallelDevices) {
      this.parallelDevices = parallelDevices;
      return thisInstance();
    }

    protected int getParallelDevices() {
      return parallelDevices;
    }

    @Override
    public abstract ParallelConnectorInput build();

    @Override
    protected abstract B thisInstance();
  }
}
