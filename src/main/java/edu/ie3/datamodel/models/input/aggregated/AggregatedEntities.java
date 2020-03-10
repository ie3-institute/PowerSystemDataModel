/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.aggregated;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.utils.ValidationUtils;
import java.util.Collection;
import java.util.List;

/** Represents an aggregation of different entities */
public interface AggregatedEntities {

  /** Adds an entity to the aggregated entities if its type is compatible */
  void add(UniqueEntity entity);

  /**
   * Adds all elements of a collection of entities to the aggregated entities if their type is
   * compatible
   */
  default void addAll(Collection<? extends UniqueEntity> entities) {
    for (UniqueEntity entity : entities) {
      add(entity);
    }
  }

  /** @return unmodifiable List of all entities */
  List<UniqueEntity> allEntitiesAsList();

  /** checks all values using {@link ValidationUtils} */
  boolean areValuesValid();
}
