/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.io.naming.timeseries.ColumnScheme.*;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import java.util.EnumSet;
import java.util.Set;

public class TimeSeriesUtils {
  private static final Set<ColumnScheme> ACCEPTED_COLUMN_SCHEMES =
      EnumSet.of(
          ACTIVE_POWER,
          APPARENT_POWER,
          ENERGY_PRICE,
          APPARENT_POWER_AND_HEAT_DEMAND,
          ACTIVE_POWER_AND_HEAT_DEMAND,
          HEAT_DEMAND);

  private TimeSeriesUtils() {
    // prevent initialization
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
