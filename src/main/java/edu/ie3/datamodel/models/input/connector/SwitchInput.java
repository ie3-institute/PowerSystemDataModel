/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Objects;
import java.util.UUID;

/** Describes an electrical grid switch between two {@link NodeInput}s */
public class SwitchInput extends ConnectorInput {
  /** Is the switching state 'closed'? */
  private final boolean closed;

  /**
   * Constructor for an operated switch
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
    super(uuid, id, operator, operationTime, nodeA, nodeB, 1);
    this.closed = closed;
  }

  /**
   * Constructor for an operated, always on switch
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA Grid node at one side of the switch
   * @param nodeB Grid node at the other side of the switch
   * @param closed Is the switching state 'closed'?
   */
  public SwitchInput(UUID uuid, String id, NodeInput nodeA, NodeInput nodeB, boolean closed) {
    super(uuid, id, nodeA, nodeB, 1);
    this.closed = closed;
  }

  public boolean isClosed() {
    return closed;
  }

  public SwitchInputCopyBuilder copy() {
    return new SwitchInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SwitchInput that = (SwitchInput) o;
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
        + ", id='"
        + getId()
        + '\''
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", nodeA="
        + getNodeA().getUuid()
        + ", nodeB="
        + getNodeB().getUuid()
        + ", noOfParallelDevices="
        + getParallelDevices()
        + ", closed="
        + closed
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link LineInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link LineInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class SwitchInputCopyBuilder
      extends ConnectorInputCopyBuilder<SwitchInputCopyBuilder> {

    private boolean closed;

    private SwitchInputCopyBuilder(SwitchInput entity) {
      super(entity);
      this.closed = entity.isClosed();
    }

    @Override
    public SwitchInput build() {
      return new SwitchInput(
          getUuid(), getId(), getOperator(), getOperationTime(), getNodeA(), getNodeB(), closed);
    }

    public SwitchInputCopyBuilder closed(boolean closed) {
      this.closed = closed;
      return this;
    }

    @Override
    protected SwitchInputCopyBuilder childInstance() {
      return this;
    }
  }
}
