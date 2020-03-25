/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.utils.ValidationUtils;
import java.util.List;

/** Represents an aggregation of different entities */
public interface InputContainer {

  /** @return unmodifiable List of all entities */
  List<UniqueEntity> allEntitiesAsList();

  /** checks all values using {@link ValidationUtils} */
  void validate();
}
