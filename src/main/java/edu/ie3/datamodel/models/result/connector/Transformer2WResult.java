/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.connector;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;
import tech.units.indriya.ComparableQuantity;

/**
 * Represents calculation results of a {@link
 * edu.ie3.datamodel.models.input.connector.Transformer2WInput}
 */
public class Transformer2WResult extends TransformerResult {

  /**
   * Standard constructor with automatic uuid generation.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param iAMag electric current magnitude @ port A, normally provided in Ampere
   * @param iAAng electric current angle @ Port A in degree
   * @param iBMag electric current magnitude @ port B, normally provided in Ampere
   * @param iBAng electric current angle @ Port B in degree
   * @param tapPos Current position of the tap changer
   */
  public Transformer2WResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<ElectricCurrent> iAMag,
      ComparableQuantity<Angle> iAAng,
      ComparableQuantity<ElectricCurrent> iBMag,
      ComparableQuantity<Angle> iBAng,
      int tapPos) {
    super(time, inputModel, iAMag, iAAng, iBMag, iBAng, tapPos);
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
   * @param tapPos Current position of the tap changer
   */
  public Transformer2WResult(
      UUID uuid,
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<ElectricCurrent> iAMag,
      ComparableQuantity<Angle> iAAng,
      ComparableQuantity<ElectricCurrent> iBMag,
      ComparableQuantity<Angle> iBAng,
      int tapPos) {
    super(uuid, time, inputModel, iAMag, iAAng, iBMag, iBAng, tapPos);
  }

  @Override
  public String toString() {
    return "Transformer2WResult{"
        + "uuid="
        + getUuid()
        + ", time="
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
        + '}';
  }
}
