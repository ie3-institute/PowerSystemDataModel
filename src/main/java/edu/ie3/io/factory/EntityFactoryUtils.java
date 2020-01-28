/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.models.UniqueEntity;
import java.util.Optional;

/** Utility class that supports retrieving information for entity factories */
public class EntityFactoryUtils {

  private EntityFactoryUtils() {
    throw new IllegalStateException("This is an Utility Class and not meant to be instantiated");
  }

  /**
   * Search the factory for an entity based on its class
   *
   * @param clazz the class a factory is needed for
   * @param emClasses the enum classes which should be included in the search for the factory
   * @return {@link Optional< EntityFactory >} either empty or with the factory (if located in the
   *     provided enums)
   */
  public static Optional<EntityFactory<?>> getFactory(
      Class<? extends UniqueEntity> clazz, Class<? extends EntityFactory<?>>... emClasses) {
    for (Class<? extends EntityFactory<?>> entityMapperClass : emClasses) {
      for (EntityFactory<?> emc : entityMapperClass.getEnumConstants()) {
        if (clazz.equals(emc.clazz())) {
          return Optional.of(emc);
        }
      }
    }
    return Optional.empty();
  }
}
