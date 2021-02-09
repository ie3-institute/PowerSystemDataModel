/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.timeseries.TimeSeriesContainer;

@Deprecated
public interface TimeSeriesSource {
  /**
   * Acquire all available time series
   *
   * @return A container with all relevant time series
   */
  TimeSeriesContainer getTimeSeries();
}
