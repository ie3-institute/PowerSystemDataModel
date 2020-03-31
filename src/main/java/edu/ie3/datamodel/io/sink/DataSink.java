/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sink;

import edu.ie3.datamodel.io.Destination;
import edu.ie3.datamodel.io.connectors.DataConnector;
import java.util.Collection;

/**
 * Describes a class that manages data persistence
 *
 * @param <D> Type of destination definition for a piece of data
 * @param <T> Type of data, the sink is supposed to handle
 */
public interface DataSink<D extends Destination, T> {

  /** @return the connector of this sink */
  DataConnector getDataConnector();

  void persist(D destination, T data);

  void persistAll(D destination, Collection<T> data);
}
