/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.sink;

import edu.ie3.dataconnection.dataconnectors.DataConnector;

/** Describes a class that manages data persistence */
public interface DataSink {

  /** @return the connector of this sink */
  DataConnector getDataConnector();
}
