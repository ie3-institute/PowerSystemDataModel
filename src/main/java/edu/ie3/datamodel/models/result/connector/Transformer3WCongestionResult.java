/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.connector;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.measure.quantity.ElectricCurrent;
import tech.units.indriya.ComparableQuantity;

/**
 * Represents congestion results of a {@link
 * edu.ie3.datamodel.models.input.connector.Transformer3WInput}.
 */
public class Transformer3WCongestionResult extends ConnectorCongestionResult {
  private final ComparableQuantity<ElectricCurrent> iBMin;
  private final ComparableQuantity<ElectricCurrent> iCMin;

  /**
   * Standard constructor for congestion results.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param iAMin minimum required current at port A
   * @param iBMin minimum required current at port B
   * @param iCMin minimum required current at port C
   */
  public Transformer3WCongestionResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<ElectricCurrent> iAMin,
      ComparableQuantity<ElectricCurrent> iBMin,
      ComparableQuantity<ElectricCurrent> iCMin) {
    super(time, inputModel, iAMin);
    this.iBMin = iBMin;
    this.iCMin = iCMin;
  }

  /** Returns the maximum current. */
  @Override
  protected ComparableQuantity<ElectricCurrent> getMax() {
    ComparableQuantity<ElectricCurrent> b = iBMin;
    ComparableQuantity<ElectricCurrent> c = iCMin;

    ComparableQuantity<ElectricCurrent> maxOfAB = iMin.isLessThan(b) ? b : iMin;
    return maxOfAB.isLessThan(c) ? c : maxOfAB;
  }
}
