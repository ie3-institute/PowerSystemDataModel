/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.io.csv.timeseries.ColumnScheme.*;

import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.Optional;

/**
 * The interface definition of a source, that is able to provide one specific time series for one
 * model
 */
public interface TimeSeriesSource<V extends Value> extends DataSource {

  /**
   * Checks whether the given column scheme can be used with time series.
   *
   * @param scheme the column scheme to check
   * @return whether the scheme is accepted or not
   */
  static boolean isSchemeAccepted(ColumnScheme scheme) {
    return EnumSet.of(
            ACTIVE_POWER,
            APPARENT_POWER,
            ENERGY_PRICE,
            APPARENT_POWER_AND_HEAT_DEMAND,
            ACTIVE_POWER_AND_HEAT_DEMAND,
            HEAT_DEMAND)
        .contains(scheme);
  }

  /**
   * Obtain the full time series
   *
   * @return the time series
   */
  IndividualTimeSeries<V> getTimeSeries();

  /**
   * Get the time series for the given time interval. If the interval is bigger than the time series
   * itself, only the parts of the time series within the interval are handed back.
   *
   * @param timeInterval Desired time interval to cover
   * @return The parts of of interest of the time series
   */
  IndividualTimeSeries<V> getTimeSeries(ClosedInterval<ZonedDateTime> timeInterval);

  /**
   * Get the time series value for a specific time
   *
   * @param time The queried time
   * @return Option on a value for that time
   */
  Optional<V> getValue(ZonedDateTime time);
}
