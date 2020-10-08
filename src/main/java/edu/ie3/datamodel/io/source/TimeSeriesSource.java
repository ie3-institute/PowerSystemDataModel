/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.timeseries.mapping.TimeSeriesMapping;
import java.util.Set;

public interface TimeSeriesSource {
  /**
   * Receive a set of time series mapping entries from participant uuid to time series uuid.
   *
   * @return A set of time series mapping entries from participant uuid to time series uuid
   */
  Set<TimeSeriesMapping.Entry> getMapping();
}
