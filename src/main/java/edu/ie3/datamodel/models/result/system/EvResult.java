/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.system;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Represents calculation results of a {@link edu.ie3.datamodel.models.input.system.EvInput} */
public class EvResult extends SystemParticipantResult {

  private ComparableQuantity<Dimensionless> soc;

  /**
   * Standard constructor with automatic uuid generation.
   *
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   * @param soc the current state of charge of the electric vehicle
   */
  public EvResult(
      ZonedDateTime timestamp,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q,
      ComparableQuantity<Dimensionless> soc) {
    super(timestamp, inputModel, p, q);
    this.soc = soc;
  }

  /**
   * Standard constructor with automatic uuid generation.
   *
   * @param uuid Unique identifier
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param p active power output normally provided in MW
   * @param q reactive power output normally provided in MVAr
   * @param soc the current state of charge of the electric vehicle
   */
  public EvResult(
      UUID uuid,
      ZonedDateTime timestamp,
      UUID inputModel,
      ComparableQuantity<Power> p,
      ComparableQuantity<Power> q,
      ComparableQuantity<Dimensionless> soc) {
    super(uuid, timestamp, inputModel, p, q);
    this.soc = soc;
  }

  public ComparableQuantity<Dimensionless> getSoc() {
    return soc;
  }

  public void setSoc(ComparableQuantity<Dimensionless> soc) {
    this.soc = soc;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    EvResult evResult = (EvResult) o;
    return soc.equals(evResult.soc);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), soc);
  }

  @Override
  public String toString() {
    return "EvResult{" + "soc=" + soc + '}';
  }
}
