/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;

import java.util.Map;
import java.util.UUID;

/**
 * Describes a class that is used to establish a connection to a data location. This location can
 * either be a file or database.
 */
public interface DataConnector {

  void shutdown();
}
