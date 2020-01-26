/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.connector;

import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Describes an electrical grid switch between two {@link NodeInput}s */
public class SwitchInput extends ConnectorInput {
  /** Is the switching state 'closed'? */
  private boolean closed;

  /**
   * @param uuid of the input entity
   * @param operationInterval Empty for a non-operated asset, Interval of operation period else
   * @param operator of the asset
   * @param id of the asset
   * @param nodeA
   * @param nodeB
   * @param closed Is the switching state 'closed'?
   */
  public SwitchInput(
      UUID uuid,
      Optional<ClosedInterval<ZonedDateTime>> operationInterval,
      OperatorInput operator,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      boolean closed) {
    super(uuid, operationInterval, operator, id, nodeA, nodeB, 1);
    this.closed = closed;
  }

  /**
   * If both operatesFrom and operatesUntil are Empty, it is assumed that the asset is non-operated.
   *
   * @param uuid of the input entity
   * @param operatesFrom start of operation period, will be replaced by LocalDateTime.MIN if Empty
   * @param operatesUntil end of operation period, will be replaced by LocalDateTime.MAX if Empty
   * @param operator of the asset
   * @param id of the asset
   * @param nodeA
   * @param nodeB
   * @param closed Is the switching state 'closed'?
   */
  public SwitchInput(
      UUID uuid,
      Optional<ZonedDateTime> operatesFrom,
      Optional<ZonedDateTime> operatesUntil,
      OperatorInput operator,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      boolean closed) {
    super(uuid, operatesFrom, operatesUntil, operator, id, nodeA, nodeB, 1);
    this.closed = closed;
  }

  /**
   * Constructor for a non-operated asset
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
