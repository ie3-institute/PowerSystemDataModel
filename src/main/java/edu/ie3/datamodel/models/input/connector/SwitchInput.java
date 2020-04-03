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
   * @param nodeA
   * @param nodeB
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
   * @param nodeA
   * @param nodeB
   * @param closed Is the switching state 'closed'?
   */
  public SwitchInput(UUID uuid, String id, NodeInput nodeA, NodeInput nodeB, boolean closed) {
    super(uuid, id, nodeA, nodeB, 1);
    this.closed = closed;
  }

  public boolean isClosed() {
    return closed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SwitchInput that = (SwitchInput) o;
    return Objects.equals(closed, that.closed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), closed);
  }

  @Override
  public String toString() {
    return "SwitchInput{" + "closed=" + closed + '}';
  }
}
