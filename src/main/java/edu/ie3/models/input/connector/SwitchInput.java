/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.connector;

import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;

import java.util.Objects;
import java.util.UUID;

/** Describes an electrical grid switch between two {@link NodeInput}s */
public class SwitchInput extends ConnectorInput {
  /** Is the switching state 'closed'? */
  private boolean closed;

  /**
   * Constructor for an operated switch
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param nodeA
   * @param nodeB
   * @param closed Is the switching state 'closed'?
   */
  public SwitchInput(
      UUID uuid,
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      boolean closed) {
    super(uuid, operationTime, operator, id, nodeA, nodeB, 1);
    this.closed = closed;
  }

  /**
   * Constructor for a non-operated switch
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

  public boolean getClosed() {
    return closed;
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
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
}
