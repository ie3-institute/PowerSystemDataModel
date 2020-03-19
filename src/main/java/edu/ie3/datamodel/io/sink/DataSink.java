/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sink;

import edu.ie3.datamodel.io.connectors.DataConnector;
import edu.ie3.datamodel.models.UniqueEntity;
import java.util.Collection;

/** Describes a class that manages data persistence */
public interface DataSink {

  /** @return the connector of this sink */
  DataConnector getDataConnector();

  void persist(UniqueEntity entity);

  void persistAll(Collection<? extends UniqueEntity> entities);
}
