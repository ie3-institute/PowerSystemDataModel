/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.result.connector;

import edu.ie3.models.result.ResultEntity;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;

/** Abstract class to hold most 'ElectricCurrent and Angle'-mappings common to all connectors */
public abstract class ConnectorResult extends ResultEntity {

  /** Electric current magnitude @ port A, normally provided in Ampere */
  Quantity<ElectricCurrent> iAMag;

  /** Electric current angle @ Port A in degree ° */
  Quantity<Angle> iAAng;

  /** Electric current magnitude @ port B, normally provided in Ampere */
  Quantity<ElectricCurrent> iBMag;

  /** Electric current angle @ Port B in degree ° */
  Quantity<Angle> iBAng;

  /**
   * Standard constructor with automatic uuid generation.
   *
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param iAMag electric current magnitude @ port A, normally provided in Ampere
   * @param iAAng electric current angle @ Port A in degree
   * @param iBMag electric current magnitude @ port B, normally provided in Ampere
   * @param iBAng electric current angle @ Port B in degree
   */
  public ConnectorResult(
      ZonedDateTime timestamp,
      UUID inputModel,
      Quantity<ElectricCurrent> iAMag,
      Quantity<Angle> iAAng,
      Quantity<ElectricCurrent> iBMag,
      Quantity<Angle> iBAng) {
    super(timestamp, inputModel);
    this.iAMag = iAMag;
    this.iAAng = iAAng;
    this.iBMag = iBMag;
    this.iBAng = iBAng;
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
   */
  public ConnectorResult(
      UUID uuid,
      ZonedDateTime timestamp,
      UUID inputModel,
      Quantity<ElectricCurrent> iAMag,
      Quantity<Angle> iAAng,
      Quantity<ElectricCurrent> iBMag,
      Quantity<Angle> iBAng) {
    super(uuid, timestamp, inputModel);
    this.iAMag = iAMag;
    this.iAAng = iAAng;
    this.iBMag = iBMag;
    this.iBAng = iBAng;
  }

  public Quantity<ElectricCurrent> getiAMag() {
    return iAMag;
  }

  public void setiAMag(Quantity<ElectricCurrent> iAMag) {
    this.iAMag = iAMag;
  }

  public Quantity<Angle> getiAAng() {
    return iAAng;
  }

  public void setiAAng(Quantity<Angle> iAAng) {
    this.iAAng = iAAng;
  }

  public Quantity<ElectricCurrent> getiBMag() {
    return iBMag;
  }

  public void setiBMag(Quantity<ElectricCurrent> iBMag) {
    this.iBMag = iBMag;
  }

  public Quantity<Angle> getiBAng() {
    return iBAng;
  }

  public void setiBAng(Quantity<Angle> iBAng) {
    this.iBAng = iBAng;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ConnectorResult that = (ConnectorResult) o;
    return iAMag.equals(that.iAMag)
        && iAAng.equals(that.iAAng)
        && iBMag.equals(that.iBMag)
        && iBAng.equals(that.iBAng);
  }

  @Override
  public int hashCode() {
    return Objects.hash(iAMag, iAAng, iBMag, iBAng);
  }
}
