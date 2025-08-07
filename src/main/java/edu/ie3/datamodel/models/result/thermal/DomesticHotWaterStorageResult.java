/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.thermal;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Represents the results of Domestic Hot Water Storage */
public class DomesticHotWaterStorageResult extends AbstractThermalStorageResult {

  /**
   * Instantiates a new Domestic hot water storage result.
   *
   * @param time the time
   * @param inputModel the input model
   * @param energy the energy
   * @param qDot the q dot
   * @param fillLevel the fill level
   */
  public DomesticHotWaterStorageResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Energy> energy,
      ComparableQuantity<Power> qDot,
      ComparableQuantity<Dimensionless> fillLevel) {
    super(time, inputModel, energy, qDot, fillLevel);
  }
}
