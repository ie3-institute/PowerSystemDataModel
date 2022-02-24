/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;

/** A naming strategy for database entities */
public class DatabaseNamingStrategy {

  public String getTimeSeriesPrefix() {
    return "time_series_";
  }

  public String getTimeSeriesEntityName(ColumnScheme columnScheme) {
    return getTimeSeriesPrefix() + columnScheme.getScheme();
  }
}
