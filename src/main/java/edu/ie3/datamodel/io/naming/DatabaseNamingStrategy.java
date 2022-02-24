/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;

/** A naming strategy for database entities */
public class DatabaseNamingStrategy {

  private static final String TIME_SERIES_PREFIX = "time_series_";

  public String getTimeSeriesPrefix() {
    return TIME_SERIES_PREFIX;
  }

  public String getTimeSeriesEntityName(ColumnScheme columnScheme) {
    return TIME_SERIES_PREFIX + columnScheme.getScheme();
  }
}
