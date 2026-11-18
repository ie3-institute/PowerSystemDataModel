/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import static edu.ie3.datamodel.models.value.WeatherValue.UNKNOWN_COORDINATE_ID;

import edu.ie3.datamodel.io.factory.FieldMap;
import java.util.OptionalInt;
import org.locationtech.jts.geom.Point;

public class TimeBasedWeatherValueData {

  private final Point coordinate;
  private final FieldMap fieldsToAttributes;

  /** Id of the coordinate, or {@link WeatherValue#UNKNOWN_COORDINATE_ID} if it is not known */
  private final int coordinateId;

  /**
   * Creates a new TimeBasedEntryData object
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param coordinate coordinate for this WeatherValue
   */
  public TimeBasedWeatherValueData(FieldMap fieldsToAttributes, Point coordinate) {
    this(fieldsToAttributes, coordinate, UNKNOWN_COORDINATE_ID);
  }

  /**
   * Creates a new TimeBasedEntryData object, additionally retaining the id of the coordinate.
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param coordinate coordinate for this WeatherValue
   * @param coordinateId id of that coordinate ({@link WeatherValue#UNKNOWN_COORDINATE_ID} if
   *     unknown)
   */
  public TimeBasedWeatherValueData(
      FieldMap fieldsToAttributes, Point coordinate, int coordinateId) {
    // this does the magic: case-insensitive get/set calls on keys
    this.fieldsToAttributes = fieldsToAttributes;
    this.coordinate = coordinate;
    this.coordinateId = coordinateId;
  }

  public Point getCoordinate() {
    return coordinate;
  }

  public FieldMap getFieldsToAttributes() {
    return fieldsToAttributes;
  }

  /**
   * Returns the id of the coordinate, if it is known.
   *
   * @return an option on the coordinate id
   */
  public OptionalInt getCoordinateId() {
    return coordinateId == UNKNOWN_COORDINATE_ID
        ? OptionalInt.empty()
        : OptionalInt.of(coordinateId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TimeBasedWeatherValueData that = (TimeBasedWeatherValueData) o;
    return coordinate.equals(that.coordinate)
        && coordinateId == that.coordinateId
        && fieldsToAttributes.equals(that.fieldsToAttributes);
  }

  @Override
  public int hashCode() {
    int result = fieldsToAttributes.hashCode();
    result = 31 * result + (coordinate != null ? coordinate.hashCode() : 0);
    result = 31 * result + coordinateId;
    return result;
  }

  @Override
  public String toString() {
    return "TimeBasedWeatherValueData{"
        + "fieldsToAttributes="
        + fieldsToAttributes
        + ", coordinate="
        + coordinate
        + ", coordinateId="
        + coordinateId
        + '}';
  }
}
