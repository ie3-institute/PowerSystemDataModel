/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.result.connector;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;

/** Represents calculation results of a {@link edu.ie3.models.input.connector.Transformer2WInput} */
public class Transformer2wResult extends TransformerResult {

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
  public Transformer2wResult(
      ZonedDateTime timestamp,
      UUID inputModel,
      Quantity<ElectricCurrent> iAMag,
      Quantity<Angle> iAAng,
      Quantity<ElectricCurrent> iBMag,
      Quantity<Angle> iBAng,
      int tapPos) {
    super(timestamp, inputModel, iAMag, iAAng, iBMag, iBAng, tapPos);
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
  public Transformer2wResult(
      UUID uuid,
      ZonedDateTime timestamp,
      UUID inputModel,
      Quantity<ElectricCurrent> iAMag,
      Quantity<Angle> iAAng,
      Quantity<ElectricCurrent> iBMag,
      Quantity<Angle> iBAng,
      int tapPos) {
    super(uuid, timestamp, inputModel, iAMag, iAAng, iBMag, iBAng, tapPos);
  }
}
