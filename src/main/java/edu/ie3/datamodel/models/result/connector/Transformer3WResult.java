/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.connector;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;
import tec.uom.se.ComparableQuantity;

public class Transformer3WResult extends TransformerResult {

  /** Electric current magnitude @ port C, normally provided in Ampere */
  private ComparableQuantity<ElectricCurrent> iCMag; // TODO #65 Quantity replaced

  /** Electric current angle @ Port C in degree ° */
  private ComparableQuantity<Angle> iCAng; // TODO #65 Quantity replaced

  /**
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param iAMag electric current magnitude @ port A, normally provided in Ampere
   * @param iAAng electric current angle @ Port A in degree
   * @param iBMag electric current magnitude @ port B, normally provided in Ampere
   * @param iBAng electric current angle @ Port B in degree
   * @param iCMag electric current magnitude @ port C, normally provided in Ampere
   * @param iCAng electric current angle @ Port C in degree
   * @param tapPos the current position of the transformers tap changer
   */
  public Transformer3WResult(
      ZonedDateTime timestamp,
      UUID inputModel,
      ComparableQuantity<ElectricCurrent> iAMag, // TODO #65 Quantity replaced
      ComparableQuantity<Angle> iAAng, // TODO #65 Quantity replaced
      ComparableQuantity<ElectricCurrent> iBMag, // TODO #65 Quantity replaced
      ComparableQuantity<Angle> iBAng, // TODO #65 Quantity replaced
      ComparableQuantity<ElectricCurrent> iCMag, // TODO #65 Quantity replaced
      ComparableQuantity<Angle> iCAng, // TODO #65 Quantity replaced
      int tapPos) {
    super(timestamp, inputModel, iAMag, iAAng, iBMag, iBAng, tapPos);
    this.iCMag = iCMag;
    this.iCAng = iCAng;
  }

  /**
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param iAMag electric current magnitude @ port A, normally provided in Ampere
   * @param iAAng electric current angle @ Port A in degree
   * @param iBMag electric current magnitude @ port B, normally provided in Ampere
   * @param iBAng electric current angle @ Port B in degree
   * @param iCMag electric current magnitude @ port C, normally provided in Ampere
   * @param iCAng electric current angle @ Port C in degree
   * @param tapPos the current position of the transformers tap changer
   */
  public Transformer3WResult(
      UUID uuid,
      ZonedDateTime timestamp,
      UUID inputModel,
      ComparableQuantity<ElectricCurrent> iAMag, // TODO #65 Quantity replaced
      ComparableQuantity<Angle> iAAng, // TODO #65 Quantity replaced
      ComparableQuantity<ElectricCurrent> iBMag, // TODO #65 Quantity replaced
      ComparableQuantity<Angle> iBAng, // TODO #65 Quantity replaced
      ComparableQuantity<ElectricCurrent> iCMag, // TODO #65 Quantity replaced
      ComparableQuantity<Angle> iCAng, // TODO #65 Quantity replaced
      int tapPos) {
    super(uuid, timestamp, inputModel, iAMag, iAAng, iBMag, iBAng, tapPos);
    this.iCMag = iCMag;
    this.iCAng = iCAng;
  }

  public ComparableQuantity<ElectricCurrent> getiCMag() {
    return iCMag;
  } // TODO #65 Quantity replaced

  public void setiCMag(ComparableQuantity<ElectricCurrent> iCMag) {
    this.iCMag = iCMag;
  } // TODO #65 Quantity replaced

  public ComparableQuantity<Angle> getiCAng() {
    return iCAng;
  } // TODO #65 Quantity replaced

  public void setiCAng(ComparableQuantity<Angle> iCAng) {
    this.iCAng = iCAng;
  } // TODO #65 Quantity replaced

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Transformer3WResult that = (Transformer3WResult) o;
    return iCMag.equals(that.iCMag) && iCAng.equals(that.iCAng);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), iCMag, iCAng);
  }

  @Override
  public String toString() {
    return "Transformer3WResult{" + "iCMag=" + iCMag + ", iCAng=" + iCAng + '}';
  }
}
