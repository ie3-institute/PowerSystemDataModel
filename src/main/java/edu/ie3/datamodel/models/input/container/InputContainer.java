/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.models.input.UniqueInputEntity;
import java.io.Serializable;
import java.util.List;

/** Represents an aggregation of different entities */
public interface InputContainer<T extends UniqueInputEntity> extends Serializable {

  /** @return unmodifiable List of all entities */
  List<T> allEntitiesAsList();

  /** Returns an input container copy builder */
  InputContainerCopyBuilder<T> copy();

  /**
   * Abstract class for all builder that build child containers of interface {@link
   * edu.ie3.datamodel.models.input.container.InputContainer}
   *
   * @version 3.1
   * @since 14.02.23
   */
  interface InputContainerCopyBuilder<T extends UniqueInputEntity> {

    /** Returns the altered {@link InputContainer} */
    InputContainer<T> build() throws ValidationException;
  }
}
