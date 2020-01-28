/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.models.UniqueEntity;
import java.util.Optional;

/**
 * Internal API Interface for EntityFactories
 *
 * @version 0.1
 * @since 28.01.20
 */
interface EntityFactory<T extends Enum<T>> {

  Class<? extends UniqueEntity>[] classes();

  Class<T> getDeclaringClass();

  T getRaw();

  Optional<? extends UniqueEntity> getEntity(EntityData entityData);
}
