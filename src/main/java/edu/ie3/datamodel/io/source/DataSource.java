/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.UniqueEntity;
import java.util.*;
import java.util.stream.Stream;

/** Interface that include functionalities for data sources */
public interface DataSource {

  /** Creates a stream of maps that represent the rows in the database */
  Stream<Map<String, String>> getSourceData(Class<? extends UniqueEntity> entityClass);
}
