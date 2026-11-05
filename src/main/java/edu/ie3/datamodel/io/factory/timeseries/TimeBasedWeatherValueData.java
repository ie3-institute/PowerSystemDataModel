/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.FieldMap;
import java.util.Optional;
import org.locationtech.jts.geom.Point;

public class TimeBasedWeatherValueData {

  private final Point coordinate;
  private final FieldMap fieldsToAttributes;

  /**
   * Id of the coordinate, or {@code null} if it is not known. As it is only a different
   * representation of the coordinate, it does not take part in {@link #equals(Object)} and {@link
   * #hashCode()}.
   */
  private final Integer coordinateId;

  /**
   * Creates a new TimeBasedEntryData object
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param coordinate coordinate for this WeatherValue
   */
  public TimeBasedWeatherValueData(FieldMap fieldsToAttributes, Point coordinate) {
    this(fieldsToAttributes, coordinate, null);
  }

  /**
   * Creates a new TimeBasedEntryData object, additionally retaining the id of the coordinate.
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param coordinate coordinate for this WeatherValue
   * @param coordinateId id of that coordinate (can be null)
   */
  public TimeBasedWeatherValueData(
      FieldMap fieldsToAttributes, Point coordinate, Integer coordinateId) {
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
  public Optional<Integer> coordinateId() {
    return Optional.ofNullable(coordinateId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TimeBasedWeatherValueData that = (TimeBasedWeatherValueData) o;
    return coordinate.equals(that.coordinate) && fieldsToAttributes.equals(that.fieldsToAttributes);
  }

  @Override
  public int hashCode() {
    int result = fieldsToAttributes.hashCode();
    result = 31 * result + (coordinate != null ? coordinate.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TimeBasedWeatherValueData{"
        + "fieldsToAttributes="
        + fieldsToAttributes
        + ", coordinate="
        + coordinate
        + '}';
  }
}
