/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import static edu.ie3.datamodel.io.naming.timeseries.ColumnScheme.*;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.source.TimeSeriesSource;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TimeSeriesUtils {
  private static final Set<ColumnScheme> ACCEPTED_COLUMN_SCHEMES =
      EnumSet.of(
          ACTIVE_POWER,
          APPARENT_POWER,
          ENERGY_PRICE,
          APPARENT_POWER_AND_HEAT_DEMAND,
          ACTIVE_POWER_AND_HEAT_DEMAND,
          HEAT_DEMAND);

  /** Private Constructor as this class is not meant to be instantiated */
  private TimeSeriesUtils() {
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

  /**
   * Returns set of column schemes that are accepted with {@link TimeSeriesSource}
   *
   * @return set of accepted column schemes
   */
  public static Set<ColumnScheme> getAcceptedColumnSchemes() {
    return ACCEPTED_COLUMN_SCHEMES;
  }

  /**
   * Checks whether the given column scheme can be used with time series.
   *
   * @param scheme the column scheme to check
   * @return whether the scheme is accepted or not
   */
  public static boolean isSchemeAccepted(ColumnScheme scheme) {
    return ACCEPTED_COLUMN_SCHEMES.contains(scheme);
  }
}
