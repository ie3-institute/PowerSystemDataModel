/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.connector;

import edu.ie3.datamodel.utils.QuantityUtils;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;
import tech.units.indriya.ComparableQuantity;

/**
 * Represents calculation results of a {@link
 * edu.ie3.datamodel.models.input.connector.Transformer3WInput}.
 */
public class Transformer3WResult extends TransformerResult {
  private ComparableQuantity<ElectricCurrent> iCMag;

  private ComparableQuantity<Angle> iCAng;

  /**
   * Standard constructor for a three-winding transformer result.
   *
   * @param time date and time when the result is produced
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
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<ElectricCurrent> iAMag,
      ComparableQuantity<Angle> iAAng,
      ComparableQuantity<ElectricCurrent> iBMag,
      ComparableQuantity<Angle> iBAng,
      ComparableQuantity<ElectricCurrent> iCMag,
      ComparableQuantity<Angle> iCAng,
      int tapPos) {
    super(time, inputModel, iAMag, iAAng, iBMag, iBAng, tapPos);
    this.iCMag = iCMag;
    this.iCAng = iCAng;
  }

  public ComparableQuantity<ElectricCurrent> getiCMag() {
    return iCMag;
  }

  public ComparableQuantity<Angle> getiCAng() {
    return iCAng;
  }

  public void setiCMag(ComparableQuantity<ElectricCurrent> iCMag) {
    this.iCMag = iCMag;
  }

  public void setiCAng(ComparableQuantity<Angle> iCAng) {
    this.iCAng = iCAng;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Transformer3WResult that)) return false;
    if (!super.equals(o)) return false;
    return QuantityUtils.equals(iCMag, that.iCMag) && QuantityUtils.equals(iCAng, that.iCAng);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), iCMag, iCAng);
  }

  @Override
  public String toString() {
    return "Transformer3WResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", iAMag="
        + getiAMag()
        + ", iAAng="
        + getiAAng()
        + ", iBMag="
        + getiBMag()
        + ", iBAng="
        + getiBAng()
        + ", tapPos="
        + getTapPos()
        + ", iCMag="
        + iCMag
        + ", iCAng="
        + iCAng
        + "}";
  }
}
