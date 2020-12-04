/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import static edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory.*;

import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.util.TimeUtil;
import java.time.ZoneId;
import java.util.*;

/**
 * Abstract factory to handle the conversion from "flat" field to value mapping onto actual {@link
 * TimeBasedValueFactory} with {@link WeatherValue}
 */
public abstract class TimeBasedWeatherValueFactory
    extends TimeBasedValueFactory<TimeBasedWeatherValueData, WeatherValue> {
  protected static final String UUID = "uuid";
  protected static final String TIME = "time";

  protected final TimeUtil timeUtil;

  public TimeBasedWeatherValueFactory() {
    this("yyyy-MM-dd'T'HH:mm:ss[.S[S][S]]'Z'");
  }

  public TimeBasedWeatherValueFactory(String timePattern) {
    this(new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, timePattern));
  }

  public TimeBasedWeatherValueFactory(TimeUtil timeUtil) {
    super(WeatherValue.class);
    this.timeUtil = timeUtil;
  }

  /**
   * Return the field name for the coordinate id
   *
   * @return the field name for the coordinate id
   */
  public abstract String getCoordinateIdFieldString();

  @Override
  protected List<Set<String>> getFields(TimeBasedWeatherValueData data) {
    Set<String> minConstructorParams =
        newSet(
            UUID,
            TIME,
            DIFFUSE_IRRADIATION,
            DIRECT_IRRADIATION,
            TEMPERATURE,
            WIND_DIRECTION,
            WIND_VELOCITY);
    return Collections.singletonList(minConstructorParams);
  }
}
