/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
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
  Optional<Set<String>> getSourceFields(Class<? extends Entity> entityClass) throws SourceException;

  /** Creates a stream of maps that represent the rows in the database */
  Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass)
      throws SourceException;

  /**
   * @param entityClass class of the source
   * @return a list of sets of fields that needs to be unique for the source.
   */
  // TODO: May be replaced by Factory#getUniqueFields()
  default List<Set<String>> getUniqueFields(Class<? extends Entity> entityClass) {
    if (InputEntity.class.isAssignableFrom(entityClass)) {
      return List.of(Set.of("uuid"));
    } else if (AssetInput.class.isAssignableFrom(entityClass)) {
      return List.of(Set.of("uuid"), Set.of("id"));
    } else if (ResultEntity.class.isAssignableFrom(entityClass)) {
      return List.of(Set.of("time", "inputModel"));
    } else if (TimeBasedValue.class.isAssignableFrom(entityClass)) {
      return List.of(Set.of("time"));
    } else {
      return List.of();
    }
  }
}
