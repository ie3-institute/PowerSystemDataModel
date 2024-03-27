/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.connector;

import edu.ie3.datamodel.models.result.ResultEntity;
import java.time.ZonedDateTime;
import java.util.UUID;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Abstract class to hold most 'ElectricCurrent'-mappings common to all connectors congestion
 * results
 */
public abstract class ConnectorCongestionResult extends ResultEntity {
  protected final ComparableQuantity<ElectricCurrent> iMin;

  /**
   * Standard constructor for congestion results.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param iMin minimum required current
   */
  protected ConnectorCongestionResult(
      ZonedDateTime time, UUID inputModel, ComparableQuantity<ElectricCurrent> iMin) {
    super(time, inputModel);
    this.iMin = iMin;
  }

  /** Returns the minimal required current. */
  public ComparableQuantity<ElectricCurrent> getRequired() {
    return getMax();
  }

  /**
   * Method to calculate the minimal required power.
   *
   * @param voltage given rated voltage
   * @return the minimal required power
   */
  public ComparableQuantity<Power> getRequired(ComparableQuantity<ElectricPotential> voltage) {
    return voltage.multiply(getMax(), Power.class).multiply(Math.sqrt(3.0));
  }

  /** Returns the maximum current. */
  protected ComparableQuantity<ElectricCurrent> getMax() {
    return iMin;
  }

  @Override
  public String toString() {
    return "ConnectorCongestionResult{time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", iMin="
        + iMin
        + '}';
  }
}
