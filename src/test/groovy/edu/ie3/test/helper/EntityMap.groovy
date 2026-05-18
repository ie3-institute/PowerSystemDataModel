/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.helper

import edu.ie3.datamodel.models.UniqueEntity

import java.util.function.Function
import java.util.stream.Collectors

class EntityMap {
  static <E extends UniqueEntity> Map<UUID, E> map(Collection<E> entities) {
    entities.stream().collect(Collectors.toMap(UniqueEntity::getUuid, Function.identity()))
  }
}
