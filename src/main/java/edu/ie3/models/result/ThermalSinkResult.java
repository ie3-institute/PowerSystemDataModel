/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.result;

import edu.ie3.util.quantities.interfaces.HeatCapacity;

import javax.measure.Quantity;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/** Represents calculation results of a {@link edu.ie3.models.input.thermal.ThermalSinkInput} */
public class ThermalSinkResult extends ResultEntity {

  /** The thermal heat demand of the sink */
  private Quantity<HeatCapacity> qDemand;

  /**
   * Standard constructor which includes auto generation of the resulting output models uuid.
   *
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param qDemand thermal heat demand of the sink
   */
  public ThermalSinkResult(
      ZonedDateTime timestamp, UUID inputModel, Quantity<HeatCapacity> qDemand) {
    super(timestamp, inputModel);
    this.qDemand = qDemand;
  }

  /**
   * Standard constructor without uuid generation.
   *
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param qDemand thermal heat demand of the sink
   */
  public ThermalSinkResult(
      UUID uuid, ZonedDateTime timestamp, UUID inputModel, Quantity<HeatCapacity> qDemand) {
    super(uuid, timestamp, inputModel);
    this.qDemand = qDemand;
  }

  public Quantity<HeatCapacity> getqDemand() {
    return qDemand;
  }

  public void setqDemand(Quantity<HeatCapacity> qDemand) {
    this.qDemand = qDemand;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ThermalSinkResult that = (ThermalSinkResult) o;
    return qDemand.equals(that.qDemand);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), qDemand);
  }
}
