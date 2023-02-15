/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.input.InputEntity;
import java.io.Serializable;
import java.util.List;

/** Represents an aggregation of different entities */
public interface InputContainer<T extends InputEntity> extends Serializable {

  /** @return unmodifiable List of all entities */
  List<T> allEntitiesAsList();

  /** Returns an input container copy builder */
  InputContainerCopyBuilder<T, ? extends InputContainer<T>> copy();

  /**
   * Abstract class for all builder that build child containers of interface {@link
   * edu.ie3.datamodel.models.input.container.InputContainer}
   *
   * @version 3.1
   * @since 14.02.23
   */
  abstract class InputContainerCopyBuilder<R extends InputEntity, E extends InputContainer<R>> {
    protected List<R> entities;

    /**
     * Constructor for {@link InputContainerCopyBuilder}.
     *
     * @param container that should be copied
     */
    protected InputContainerCopyBuilder(E container) {
      this.entities = container.allEntitiesAsList();
    }

    /**
     * Method to alter the list of entities directly.
     *
     * @param entities altered list of {@link InputEntity}'s
     * @return child instance of {@link InputContainerCopyBuilder}
     */
    public InputContainerCopyBuilder<R, E> entities(List<R> entities) {
      this.entities = entities;
      return childInstance();
    }

    /** @return child instance of {@link InputContainerCopyBuilder} */
    protected abstract InputContainerCopyBuilder<R, E> childInstance();

    /** @return the altered {@link InputContainer} */
    abstract InputContainer<R> build();
  }
}
