/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

public class TimeSeriesUtil {
  /** Private Constructor as this class is not meant to be instantiated */
  private TimeSeriesUtil() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  /**
   * Trims a time series to the given time interval
   *
   * @param timeSeries the time series to trim
   * @param timeInterval the interval to trim the data to
   * @param <V> Type of value carried wit the time series
   * @return Trimmed time series
   */
  public static <V extends Value> IndividualTimeSeries<V> trimTimeSeriesToInterval(
      IndividualTimeSeries<V> timeSeries, ClosedInterval<ZonedDateTime> timeInterval) {
    return new IndividualTimeSeries<>(
        timeSeries.getUuid(),
        timeSeries.getEntries().stream()
            .parallel()
            .filter(value -> timeInterval.includes(value.getTime()))
            .collect(Collectors.toSet()));
  }
}
