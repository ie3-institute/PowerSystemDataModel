/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.utils.ValidationUtils;
import java.io.Serializable;
import java.util.List;

/** Represents an aggregation of different entities */
public interface InputContainer<T extends InputEntity> extends Serializable {

  /** @return unmodifiable List of all entities */
  List<T> allEntitiesAsList();

  /** checks all values using {@link ValidationUtils} */
  void validate();
}
