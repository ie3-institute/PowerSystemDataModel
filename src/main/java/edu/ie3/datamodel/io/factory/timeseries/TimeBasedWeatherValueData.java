/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.models.value.WeatherValue;
import javax.measure.Quantity;
import javax.measure.Unit;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;

public class TimeBasedWeatherValueData extends TimeBasedValueData<WeatherValue> {

  private final Point coordinate;

  /**
   * Creates a new TimeBasedEntryData object
   *
   * @param mapWithRowIndex object containing an attribute map: field name to value and a row index
   * @param coordinate coordinate for this WeatherValue
   */
  public TimeBasedWeatherValueData(MapWithRowIndex mapWithRowIndex, Point coordinate) {
    super(mapWithRowIndex, WeatherValue.class);
    this.coordinate = coordinate;
  }

  public Point getCoordinate() {
    return coordinate;
  }

  @Override
  public <Q extends Quantity<Q>> ComparableQuantity<Q> getQuantity(String field, Unit<Q> unit) {
    return getField(field).isEmpty() ? null : super.getQuantity(field, unit);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    TimeBasedWeatherValueData that = (TimeBasedWeatherValueData) o;
    return coordinate.equals(that.coordinate);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (coordinate != null ? coordinate.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TimeBasedWeatherValueData{"
        + "fieldsToAttributes="
        + getFieldsToValues()
        + ", targetClass="
        + getTargetClass()
        + ", coordinate="
        + coordinate
        + '}';
  }
}
