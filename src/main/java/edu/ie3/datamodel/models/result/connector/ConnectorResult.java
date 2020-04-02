/*
 * © 2020. TU Dortmund University,
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
import tec.uom.se.ComparableQuantity;

/** Abstract class to hold most 'ElectricCurrent and Angle'-mappings common to all connectors */
public abstract class ConnectorResult extends ResultEntity {

  /** Electric current magnitude @ port A, normally provided in Ampere */
  private ComparableQuantity<ElectricCurrent> iAMag; // TODO #65 Quantity replaced

  /** Electric current angle @ Port A in degree ° */
  private ComparableQuantity<Angle> iAAng; // TODO #65 Quantity replaced

  /** Electric current magnitude @ port B, normally provided in Ampere */
  private ComparableQuantity<ElectricCurrent> iBMag; // TODO #65 Quantity replaced

  /** Electric current angle @ Port B in degree ° */
  private ComparableQuantity<Angle> iBAng; // TODO #65 Quantity replaced

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
      ComparableQuantity<ElectricCurrent> iAMag, // TODO #65 Quantity replaced
      ComparableQuantity<Angle> iAAng, // TODO #65 Quantity replaced
      ComparableQuantity<ElectricCurrent> iBMag, // TODO #65 Quantity replaced
      ComparableQuantity<Angle> iBAng) { // TODO #65 Quantity replaced
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
      ComparableQuantity<ElectricCurrent> iAMag, // TODO #65 Quantity replaced
      ComparableQuantity<Angle> iAAng, // TODO #65 Quantity replaced
      ComparableQuantity<ElectricCurrent> iBMag, // TODO #65 Quantity replaced
      ComparableQuantity<Angle> iBAng) { // TODO #65 Quantity replaced
    super(uuid, timestamp, inputModel);
    this.iAMag = iAMag;
    this.iAAng = iAAng;
    this.iBMag = iBMag;
    this.iBAng = iBAng;
  }

  public ComparableQuantity<ElectricCurrent> getiAMag() {
    return iAMag;
  } // TODO #65 Quantity replaced

  public void setiAMag(ComparableQuantity<ElectricCurrent> iAMag) {
    this.iAMag = iAMag;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<Angle> getiAAng() {
    return iAAng;
  } // TODO #65 Quantity replaced

  public void setiAAng(ComparableQuantity<Angle> iAAng) {
    this.iAAng = iAAng;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<ElectricCurrent> getiBMag() {
    return iBMag;
  } // TODO #65 Quantity replaced

  public void setiBMag(ComparableQuantity<ElectricCurrent> iBMag) {
    this.iBMag = iBMag;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<Angle> getiBAng() {
    return iBAng;
  } // TODO #65 Quantity replaced

  public void setiBAng(ComparableQuantity<Angle> iBAng) {
    this.iBAng = iBAng;
  } // TODO #65 Quantity replaced

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
        + "iAMag="
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
