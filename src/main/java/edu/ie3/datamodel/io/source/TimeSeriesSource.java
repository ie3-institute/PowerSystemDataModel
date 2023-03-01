/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.utils.TimeSeriesUtils;

import java.util.*;

/**
 * The interface definition of a source, that is able to provide one specific time series for one
 * model
 */
public class TimeSeriesSource<V extends Value> implements DataSource {
  protected UUID timeSeriesUuid;
  public TimeSeriesSource(UUID timeSeriesUuid) { this.timeSeriesUuid = timeSeriesUuid; }

  /**
   * Checks whether the given column scheme can be used with time series.
   *
   * @param scheme the column scheme to check
   * @return whether the scheme is accepted or not
   * @deprecated since 3.0. Use {@link TimeSeriesUtils#isSchemeAccepted(ColumnScheme)}
   *     instead.
   */
  @Deprecated(since = "3.0", forRemoval = true)
  public static boolean isSchemeAccepted(edu.ie3.datamodel.io.csv.timeseries.ColumnScheme scheme) {
    return EnumSet.of(
                    edu.ie3.datamodel.io.csv.timeseries.ColumnScheme.ACTIVE_POWER,
                    edu.ie3.datamodel.io.csv.timeseries.ColumnScheme.APPARENT_POWER,
                    edu.ie3.datamodel.io.csv.timeseries.ColumnScheme.ENERGY_PRICE,
                    edu.ie3.datamodel.io.csv.timeseries.ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND,
                    edu.ie3.datamodel.io.csv.timeseries.ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND,
                    edu.ie3.datamodel.io.csv.timeseries.ColumnScheme.HEAT_DEMAND)
            .contains(scheme);
  }
}
