/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.value.Value;

/**
 * The interface definition of a source, that is able to provide one specific time series for one
 * model
 */
public interface TimeSeriesSource<V extends Value> extends DataSource {

  /**
   * Obtain the full time series
   *
   * @return the time series
   */
  IndividualTimeSeries<V> getTimeSeries();
}
