/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.connector;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;

/**
 * Represents calculation results of a {@link edu.ie3.datamodel.models.input.connector.SwitchInput}
 */
public class SwitchResult extends ConnectorResult {

  /** is the switching state 'closed'? */
  private boolean closed;

  /**
   * Standard constructor with automatic uuid generation.
   *
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param iAMag electric current magnitude @ port A, normally provided in Ampere
   * @param iAAng electric current angle @ Port A in degree
   * @param iBMag electric current magnitude @ port B, normally provided in Ampere
   * @param iBAng electric current angle @ Port B in degree
   * @param closed true if switch is closed, false if switch is open
   */
  public SwitchResult(
      ZonedDateTime timestamp,
      UUID inputModel,
      Quantity<ElectricCurrent> iAMag,
      Quantity<Angle> iAAng,
      Quantity<ElectricCurrent> iBMag,
      Quantity<Angle> iBAng,
      boolean closed) {
    super(timestamp, inputModel, iAMag, iAAng, iBMag, iBAng);
    this.closed = closed;
  }

  /**
   * Standard constructor which allows uuid provision
   *
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param iAMag electric current magnitude @ port A, normally provided in Ampere
   * @param iAAng electric current angle @ Port A in degree
   * @param iBMag electric current magnitude @ port B, normally provided in Ampere
   * @param iBAng electric current angle @ Port B in degree
   * @param closed true if switch is closed, false if switch is open
   */
  public SwitchResult(
      UUID uuid,
      ZonedDateTime timestamp,
      UUID inputModel,
      Quantity<ElectricCurrent> iAMag,
      Quantity<Angle> iAAng,
      Quantity<ElectricCurrent> iBMag,
      Quantity<Angle> iBAng,
      boolean closed) {
    super(uuid, timestamp, inputModel, iAMag, iAAng, iBMag, iBAng);
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
    return "SwitchResult{" + "closed=" + closed + '}';
  }
}
