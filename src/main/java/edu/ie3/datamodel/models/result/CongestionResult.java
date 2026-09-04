/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result;

import edu.ie3.datamodel.utils.QuantityUtils;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import tech.units.indriya.ComparableQuantity;

/** Represents calculation results of a {@link edu.ie3.datamodel.models.input.NodeInput}. */
public class CongestionResult extends ResultEntity {
  private int subgrid;

  private InputModelType type;

  private ComparableQuantity<Dimensionless> value;

  private ComparableQuantity<Dimensionless> min;

  private ComparableQuantity<Dimensionless> max;

  /**
   * Standard constructor for a congestion result.
   *
   * @param time date and time when the result is produced
   * @param inputModel identifier of the input model
   * @param type of the input model
   * @param subgrid the subgrid
   * @param value the actual value in percent
   * @param min minimum value in percent
   * @param max maximal value in percent
   */
  public CongestionResult(
      ZonedDateTime time,
      UUID inputModel,
      InputModelType type,
      int subgrid,
      ComparableQuantity<Dimensionless> value,
      ComparableQuantity<Dimensionless> min,
      ComparableQuantity<Dimensionless> max) {
    super(time, inputModel);
    this.type = type;
    this.subgrid = subgrid;
    this.value = value;
    this.min = min;
    this.max = max;
  }

  public int getSubgrid() {
    return subgrid;
  }

  public InputModelType getType() {
    return type;
  }

  public ComparableQuantity<Dimensionless> getValue() {
    return value;
  }

  public ComparableQuantity<Dimensionless> getMin() {
    return min;
  }

  public ComparableQuantity<Dimensionless> getMax() {
    return max;
  }

  public void setSubgrid(int subgrid) {
    this.subgrid = subgrid;
  }

  public void setType(InputModelType type) {
    this.type = type;
  }

  public void setValue(ComparableQuantity<Dimensionless> value) {
    this.value = value;
  }

  public void setMin(ComparableQuantity<Dimensionless> min) {
    this.min = min;
  }

  public void setMax(ComparableQuantity<Dimensionless> max) {
    this.max = max;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CongestionResult that)) return false;
    if (!super.equals(o)) return false;
    return subgrid == that.subgrid
        && Objects.equals(type, that.type)
        && QuantityUtils.equals(value, that.value)
        && QuantityUtils.equals(min, that.min)
        && QuantityUtils.equals(max, that.max);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), subgrid, type, value, min, max);
  }

  @Override
  public String toString() {
    return "CongestionResult{"
        + "time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", subgrid="
        + subgrid
        + ", type="
        + type
        + ", value="
        + value
        + ", min="
        + min
        + ", max="
        + max
        + "}";
  }
}
