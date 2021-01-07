/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory;

import edu.ie3.datamodel.models.UniqueEntity;

/**
 * Internal API Interface for Entities that can be build without any dependencies on other complex
 * pojos
 *
 * @version 0.1
 * @since 28.01.20
 */
public abstract class SimpleEntityFactory<T extends UniqueEntity>
    extends EntityFactory<T, SimpleEntityData> {

  public SimpleEntityFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
  }
}
