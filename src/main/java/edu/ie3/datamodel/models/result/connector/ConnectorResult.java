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
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;
import tech.units.indriya.ComparableQuantity;

/** Abstract class to hold most 'ElectricCurrent and Angle'-mappings common to all connectors */
public abstract class ConnectorResult extends ResultEntity {

  /** Electric current magnitude @ port A, normally provided in Ampere */
  private ComparableQuantity<ElectricCurrent> iAMag;

  /** Electric current angle @ Port A in degree ° */
  private ComparableQuantity<Angle> iAAng;

  /** Electric current magnitude @ port B, normally provided in Ampere */
  private ComparableQuantity<ElectricCurrent> iBMag;

  /** Electric current angle @ Port B in degree ° */
  private ComparableQuantity<Angle> iBAng;

  /**
   * Standard constructor with automatic uuid generation.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param iAMag electric current magnitude @ port A, normally provided in Ampere
   * @param iAAng electric current angle @ Port A in degree
   * @param iBMag electric current magnitude @ port B, normally provided in Ampere
   * @param iBAng electric current angle @ Port B in degree
   */
  protected ConnectorResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<ElectricCurrent> iAMag,
      ComparableQuantity<Angle> iAAng,
      ComparableQuantity<ElectricCurrent> iBMag,
      ComparableQuantity<Angle> iBAng) {
    super(time, inputModel);
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
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param iAMag electric current magnitude @ port A, normally provided in Ampere
   * @param iAAng electric current angle @ Port A in degree
   * @param iBMag electric current magnitude @ port B, normally provided in Ampere
   * @param iBAng electric current angle @ Port B in degree
   */
  protected ConnectorResult(
      UUID uuid,
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<ElectricCurrent> iAMag,
      ComparableQuantity<Angle> iAAng,
      ComparableQuantity<ElectricCurrent> iBMag,
      ComparableQuantity<Angle> iBAng) {
    super(uuid, time, inputModel);
    this.iAMag = iAMag;
    this.iAAng = iAAng;
    this.iBMag = iBMag;
    this.iBAng = iBAng;
  }

  public ComparableQuantity<ElectricCurrent> getiAMag() {
    return iAMag;
  }

  public void setiAMag(ComparableQuantity<ElectricCurrent> iAMag) {
    this.iAMag = iAMag;
  }

  public ComparableQuantity<Angle> getiAAng() {
    return iAAng;
  }

  public void setiAAng(ComparableQuantity<Angle> iAAng) {
    this.iAAng = iAAng;
  }

  public ComparableQuantity<ElectricCurrent> getiBMag() {
    return iBMag;
  }

  public void setiBMag(ComparableQuantity<ElectricCurrent> iBMag) {
    this.iBMag = iBMag;
  }

  public ComparableQuantity<Angle> getiBAng() {
    return iBAng;
  }

  public void setiBAng(ComparableQuantity<Angle> iBAng) {
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

  @Override
  public String toString() {
    return "ConnectorResult{"
        + "uuid="
        + getUuid()
        + ", time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", iAMag="
        + iAMag
        + ", iAAng="
        + iAAng
        + ", iBMag="
        + iBMag
        + ", iBAng="
        + iBAng
        + '}';
  }
}
