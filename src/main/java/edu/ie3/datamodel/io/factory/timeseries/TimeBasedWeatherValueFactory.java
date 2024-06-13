/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.util.TimeUtil;
import java.time.format.DateTimeFormatter;

/**
 * Abstract factory to handle the conversion from "flat" field to value mapping onto actual {@link
 * TimeBasedValueFactory} with {@link WeatherValue}
 */
public abstract class TimeBasedWeatherValueFactory
    extends TimeBasedValueFactory<TimeBasedWeatherValueData, WeatherValue> {
  protected static final String COORDINATE_ID = "coordinateId";

  protected TimeBasedWeatherValueFactory() {
    super(WeatherValue.class);
  }

  protected TimeBasedWeatherValueFactory(DateTimeFormatter dateTimeFormatter) {
    super(WeatherValue.class, dateTimeFormatter);
  }

  protected TimeBasedWeatherValueFactory(TimeUtil timeUtil) {
    super(WeatherValue.class, timeUtil);
  }

  /**
   * Return the field name for the coordinate id
   *
   * @return the field name for the coordinate id
   */
  public String getCoordinateIdFieldString() {
    return COORDINATE_ID;
  }
}
