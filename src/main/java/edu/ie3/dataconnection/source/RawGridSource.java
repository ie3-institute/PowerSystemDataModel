/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.source;

import edu.ie3.models.input.aggregated.AggregatedRawGridInput;

/** Describes a data source for raw grid data */
public interface RawGridSource extends DataSource {
  /** @return grid data as an aggregation of its elements */
  AggregatedRawGridInput getGridData();
}
