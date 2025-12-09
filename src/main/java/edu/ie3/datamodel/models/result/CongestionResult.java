/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.source.SourceValidator;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import tech.units.indriya.ComparableQuantity;

public class CongestionResult extends ResultEntity {
  /* Static fields. */
  public static final String TYPE = "type";
  public static final String SUBGRID = "subgrid";
  public static final String VALUE = "value";
  public static final String MIN = "min";
  public static final String MAX = "max";

  /** Values */
  private final Integer subgrid;

  private final InputModelType type;
  private final ComparableQuantity<Dimensionless> value;
  private final ComparableQuantity<Dimensionless> min;
  private final ComparableQuantity<Dimensionless> max;

  /**
   * Standard constructor which includes auto generation of the resulting output models uuid.
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

  public CongestionResult(
      ResultEntity resultEntity,
      InputModelType type,
      int subgrid,
      ComparableQuantity<Dimensionless> value,
      ComparableQuantity<Dimensionless> min,
      ComparableQuantity<Dimensionless> max) {
    super(resultEntity);
    this.type = type;
    this.subgrid = subgrid;
    this.value = value;
    this.min = min;
    this.max = max;
  }

  public static SourceValidator.Fields getFields() {
    return resultFields().add(TYPE, SUBGRID, VALUE, MIN, MAX);
  }

  public InputModelType getType() {
    return type;
  }

  public int getSubgrid() {
    return subgrid;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CongestionResult that = (CongestionResult) o;
    return getTime().equals(that.getTime())
        && getInputModel().equals(that.getInputModel())
        && type.equals(that.type)
        && Objects.equals(subgrid, that.subgrid)
        && value.equals(that.value)
        && min.equals(that.min)
        && max.equals(that.max);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getTime(), type, subgrid, value, min, max);
  }

  @Override
  public String toString() {
    return "InputResultEntity{time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", type="
        + type
        + ", subgrid="
        + subgrid
        + ", value="
        + value
        + ", min="
        + min
        + ", max="
        + max
        + '}';
  }

  public enum InputModelType {
    NODE("node"),
    LINE("line"),
    TRANSFORMER_2W("transformer_2w"),
    TRANSFORMER_3W("transforerm_3w");

    public final String type;

    InputModelType(String type) {
      this.type = type;
    }

    public static InputModelType parse(String inputModelType) throws ParsingException {
      return switch (inputModelType) {
        case "node" -> NODE;
        case "line" -> LINE;
        case "transformer_2w" -> TRANSFORMER_2W;
        case "transformer_3w" -> TRANSFORMER_3W;
        default ->
            throw new ParsingException("InputModelType '" + inputModelType + "' cannot be parsed!");
      };
    }
  }
}
