/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import tech.units.indriya.ComparableQuantity;

public class CongestionResult extends ResultEntity {
  /** Values */
  private final Integer subgrid;

  private final ComparableQuantity<Dimensionless> min;
  private final ComparableQuantity<Dimensionless> max;

  /**
   * Standard constructor which includes auto generation of the resulting output models uuid.
   *
   * @param time date and time when the result is produced
   * @param subgrid the subgrid
   * @param min minimum value in percent
   * @param max maximal value in percent
   */
  public CongestionResult(
      ZonedDateTime time,
      UUID inputModel,
      int subgrid,
      ComparableQuantity<Dimensionless> min,
      ComparableQuantity<Dimensionless> max) {
    super(time, inputModel);
    this.subgrid = subgrid;
    this.min = min;
    this.max = max;
  }

  public int getSubgrid() {
    return subgrid;
  }

  public ComparableQuantity<Dimensionless> getMin() {
    return min;
  }

  public ComparableQuantity<Dimensionless> getMax() {
    return max;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CongestionResult that = (CongestionResult) o;
    return getTime().equals(that.getTime())
        && Objects.equals(subgrid, that.subgrid)
        && min.equals(that.min)
        && max.equals(that.max);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getTime(), subgrid, min, max);
  }

  @Override
  public String toString() {
    return "InputResultEntity{time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", subgrid="
        + subgrid
        + ", min="
        + min
        + ", max="
        + max
        + '}';
  }
}
