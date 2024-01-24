/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.models.UniqueEntity;
import java.util.*;
import java.util.stream.Stream;

/** Interface that include functionalities for data sources in the database table, csv file etc. */
public interface DataSource {

  /**
   * Method to retrieve the fields found in the source.
   *
   * @param entityClass class of the source
   * @return an option for the found fields
   */
  Optional<Set<String>> getSourceFields(Class<? extends UniqueEntity> entityClass)
      throws SourceException;

  /** Creates a stream of maps that represent the rows in the database */
  Stream<Map<String, String>> getSourceData(Class<? extends UniqueEntity> entityClass);
}
