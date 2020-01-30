/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.models.UniqueEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Internal API Interface for Entities that can be build without any dependencies on other complex
 * pojos
 *
 * @version 0.1
 * @since 28.01.20
 */
public abstract class SimpleEntityFactory<T extends UniqueEntity> extends EntityFactory<T> {

  public SimpleEntityFactory(Class<? extends T>... classes) {
    super(classes);
  }

  @Override
  public Optional<T> getEntity(EntityData entityData) {
    if (!classes.contains(entityData.getEntityClass()))
      throw new FactoryException(
          "Cannot process "
              + entityData.getEntityClass().getSimpleName()
              + ".class with this factory!");

    final List<Set<String>> allFields = getFields(entityData);

    SimpleEntityData simpleEntityData = getSimpleEntityData(entityData);
    validateParameters(simpleEntityData, allFields.toArray(Set[]::new));

    // build the model
    Optional<T> result = Optional.empty();
    try {

      result = Optional.of(buildModel(simpleEntityData));

    } catch (Exception e) {
      log.error(
          "An error occurred when creating instance of "
              + entityData.getEntityClass().getSimpleName()
              + ".class.",
          e);
    }
    return result;
  }

  protected SimpleEntityData getSimpleEntityData(EntityData entityData) {
    if (!(entityData instanceof SimpleEntityData)) {
      throw new FactoryException(
          "Invalid entity data "
              + entityData.getClass().getSimpleName()
              + " provided. Please use 'SimpleEntityData' for 'SimpleEntityFactory'!");
    } else {
      return (SimpleEntityData) entityData;
    }
  }
}
