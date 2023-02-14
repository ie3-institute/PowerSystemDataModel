/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.input.InputEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Represents an aggregation of different entities */
public interface InputContainer<T extends InputEntity> extends Serializable {

  /** @return unmodifiable List of all entities */
  List<T> allEntitiesAsList();

  InputContainerCopyBuilder<T, ? extends InputContainer<T>> copy();

  abstract class InputContainerCopyBuilder<R extends InputEntity, E extends InputContainer<R>> {
    protected List<R> entities;

    protected InputContainerCopyBuilder(E container) {
      this.entities = container.allEntitiesAsList();
    }

    protected InputContainerCopyBuilder<R, E> entities(Set<R> oldValue, Set<R> newValue) {
      List<R> entityList =
          new ArrayList<>(entities.stream().filter(value -> !oldValue.contains(value)).toList());
      entities.addAll(newValue.stream().toList());
      this.entities = List.copyOf(entityList);
      return childInstance();
    }

    protected abstract InputContainerCopyBuilder<R, E> childInstance();

    abstract InputContainer<R> build();
  }
}
