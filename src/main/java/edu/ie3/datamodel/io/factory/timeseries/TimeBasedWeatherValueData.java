/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import java.util.Map;
import org.locationtech.jts.geom.Point;

public class TimeBasedWeatherValueData extends EntityData {

  private final Point coordinate;

  /**
   * Creates a new TimeBasedEntryData object
   *
   * @param fieldsToAttributes attribute map: field name -> value
   * @param coordinate coordinate for this WeatherValue
   */
  public TimeBasedWeatherValueData(Map<String, String> fieldsToAttributes, Point coordinate) {
    super(fieldsToAttributes, TimeBasedValue.class);
    this.coordinate = coordinate;
  }

  public Point getCoordinate() {
    return coordinate;
  }
}
