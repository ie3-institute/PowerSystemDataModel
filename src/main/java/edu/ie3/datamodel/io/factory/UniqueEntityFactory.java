/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory;

import edu.ie3.datamodel.models.Entity;

/**
 * Universal factory class for creating entities with unique fields uuid and id.
 *
 * @param <T> Type of entity that this factory can create. Can be a subclass of the entities that
 *     this factory creates.
 * @param <D> Type of data class that is required for entity creation
 */
public abstract class UniqueEntityFactory<T extends Entity, D extends EntityData>
    extends EntityFactory<T, D> {

  protected static final String UUID = "uuid";

  protected static final String ID = "id";

  @SafeVarargs
  protected UniqueEntityFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
  }
}
