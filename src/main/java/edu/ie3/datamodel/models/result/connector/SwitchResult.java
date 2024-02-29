/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.connector;

import edu.ie3.datamodel.models.result.ResultEntity;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents calculation results of a {@link edu.ie3.datamodel.models.input.connector.SwitchInput}
 */
public class SwitchResult extends ResultEntity {

  /** is the switching state 'closed'? */
  private boolean closed;

  /**
   * Standard constructor with automatic uuid generation.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param closed true if switch is closed, false if switch is open
   */
  public SwitchResult(ZonedDateTime time, UUID inputModel, boolean closed) {
    super(time, inputModel);
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
    SwitchResult that = (SwitchResult) o;
    return closed == that.closed;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), closed);
  }

  @Override
  public String toString() {
    return "SwitchResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + "closed="
        + closed
        + '}';
  }
}
