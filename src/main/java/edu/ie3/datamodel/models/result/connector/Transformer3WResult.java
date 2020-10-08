/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.connector;

import edu.ie3.util.quantities.QuantityUtil;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;
import tech.units.indriya.ComparableQuantity;

public class Transformer3WResult extends TransformerResult {

  /** Electric current magnitude @ port C, normally provided in Ampere */
  private ComparableQuantity<ElectricCurrent> iCMag;

  /** Electric current angle @ Port C in degree ° */
  private ComparableQuantity<Angle> iCAng;

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
      ComparableQuantity<ElectricCurrent> iAMag,
      ComparableQuantity<Angle> iAAng,
      ComparableQuantity<ElectricCurrent> iBMag,
      ComparableQuantity<Angle> iBAng,
      ComparableQuantity<ElectricCurrent> iCMag,
      ComparableQuantity<Angle> iCAng,
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
      ComparableQuantity<ElectricCurrent> iAMag,
      ComparableQuantity<Angle> iAAng,
      ComparableQuantity<ElectricCurrent> iBMag,
      ComparableQuantity<Angle> iBAng,
      ComparableQuantity<ElectricCurrent> iCMag,
      ComparableQuantity<Angle> iCAng,
      int tapPos) {
    super(uuid, timestamp, inputModel, iAMag, iAAng, iBMag, iBAng, tapPos);
    this.iCMag = iCMag;
    this.iCAng = iCAng;
  }

  public ComparableQuantity<ElectricCurrent> getiCMag() {
    return iCMag;
  }

  public void setiCMag(ComparableQuantity<ElectricCurrent> iCMag) {
    this.iCMag = iCMag;
  }

  public ComparableQuantity<Angle> getiCAng() {
    return iCAng;
  }

  public void setiCAng(ComparableQuantity<Angle> iCAng) {
    this.iCAng = iCAng;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Transformer3WResult that = (Transformer3WResult) o;
    return QuantityUtil.isTheSameConsideringEmpty(iCMag, that.iCMag)
        && QuantityUtil.isTheSameConsideringEmpty(iCAng, that.iCAng);
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
