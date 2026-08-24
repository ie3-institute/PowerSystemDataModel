/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** Describes an electrical grid switch between two {@link NodeInput}s. */
public class SwitchInput extends ConnectorInput {
  /** Is the switching state 'closed'? */
  private final boolean closed;

  /**
   * Constructor for an operated switch.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param nodeA Grid node at one side of the switch
   * @param nodeB Grid node at the other side of the switch
   * @param closed Is the switching state 'closed'?
   */
  public SwitchInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput nodeA,
      NodeInput nodeB,
      boolean closed) {
    super(uuid, id, operator, operationTime, nodeA, nodeB);
    this.closed = closed;
  }

  /**
   * Constructor for an operated switch.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param nodeA Grid node at one side of the switch
   * @param nodeB Grid node at the other side of the switch
   * @param closed Is the switching state 'closed'?
   * @param additionalInformation Of the model
   */
  public SwitchInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput nodeA,
      NodeInput nodeB,
      boolean closed,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime, nodeA, nodeB);
    this.closed = closed;
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for an operated switch.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA Grid node at one side of the switch
   * @param nodeB Grid node at the other side of the switch
   * @param closed Is the switching state 'closed'?
   */
  public SwitchInput(UUID uuid, String id, NodeInput nodeA, NodeInput nodeB, boolean closed) {
    super(uuid, id, nodeA, nodeB);
    this.closed = closed;
  }

  public boolean isClosed() {
    return closed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SwitchInput that)) return false;
    if (!super.equals(o)) return false;
    return closed == that.closed;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), closed);
  }

  @Override
  public String toString() {
    return "SwitchInput{"
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
        + ", parallelDevices="
        + getParallelDevices()
        + ", closed="
        + closed
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public SwitchInputCopyBuilder copy() {
    return new SwitchInputCopyBuilder(this);
  }

  public static class SwitchInputCopyBuilder
      extends ConnectorInputCopyBuilder<SwitchInputCopyBuilder> {
    private boolean closed;

    protected SwitchInputCopyBuilder(SwitchInput entity) {
      super(entity);
      this.closed = entity.closed;
    }

    public SwitchInputCopyBuilder closed(boolean closed) {
      this.closed = closed;
      return thisInstance();
    }

    protected boolean isClosed() {
      return closed;
    }

    @Override
    public SwitchInput build() {
      return new SwitchInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNodeA(),
          getNodeB(),
          closed,
          getAdditionalInformation());
    }

    @Override
    protected SwitchInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
