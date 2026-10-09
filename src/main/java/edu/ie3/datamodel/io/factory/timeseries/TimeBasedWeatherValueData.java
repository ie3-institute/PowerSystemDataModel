/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import java.util.Map;
import java.util.TreeMap;
import org.locationtech.jts.geom.Point;

public class TimeBasedWeatherValueData {

  private final Point coordinate;
  private final Map<String, String> fieldsToAttributes;

  /**
   * Creates a new TimeBasedEntryData object
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param coordinate coordinate for this WeatherValue
   */
  public TimeBasedWeatherValueData(Map<String, String> fieldsToAttributes, Point coordinate) {
    // this does the magic: case-insensitive get/set calls on keys
    this.fieldsToAttributes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    this.fieldsToAttributes.putAll(fieldsToAttributes);
    this.coordinate = coordinate;
  }

  public Point getCoordinate() {
    return coordinate;
  }

  public Map<String, String> getFieldsToAttributes() {
    return fieldsToAttributes;
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
