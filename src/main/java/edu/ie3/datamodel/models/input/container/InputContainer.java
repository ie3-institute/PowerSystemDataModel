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

/** Represents an aggregation of different entities @param <T> the type parameter */
public interface InputContainer<T extends UniqueInputEntity> extends Serializable {

  /**
   * All entities as list list.
   *
   * @return unmodifiable List of all entities
   */
  List<T> allEntitiesAsList();

  /** Returns an input container copy builder @return the input container copy builder */
  InputContainerCopyBuilder<T> copy();

  /**
   * Abstract class for all builder that build child containers of interface {@link
   * edu.ie3.datamodel.models.input.container.InputContainer}*
   *
   * @param <T> the type parameter
   * @version 3.1
   * @since 14.02.23
   */
  abstract class InputContainerCopyBuilder<T extends UniqueInputEntity> {

    /**
     * Returns the altered {@link InputContainer} @return the input container
     *
     * @throws ValidationException the validation exception
     */
    public abstract InputContainer<T> build() throws ValidationException;

    /**
     * This instance input container copy builder.
     *
     * @return the input container copy builder
     */
    protected abstract InputContainerCopyBuilder<T> thisInstance();
  }
}
