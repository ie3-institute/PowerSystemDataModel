/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory;

import edu.ie3.datamodel.models.Entity;
import java.util.Map;

/**
 * Universal factory class for creating entities.
 *
 * @param <T> Type of entity that this factory can create. Can be a subclass of the entities that
 *     this factory creates.
 */
public abstract class EntityFactory<T extends Entity, R extends Entity>
    extends Factory<T, Map<String, String>, R> {
  /**
   * Constructor for an EntityFactory for given classes
   *
   * @param allowedClasses exactly the classes that this factory is allowed and able to build
   */
  @SafeVarargs
  protected EntityFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
  }
}
