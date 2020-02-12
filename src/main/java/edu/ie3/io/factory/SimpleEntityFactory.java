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
import java.util.function.IntFunction;

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

  @Override
  public Optional<T> getEntity(SimpleEntityData simpleEntityData) {
    isValidClass(simpleEntityData.getEntityClass());

    // magic: case-insensitive get/set calls on set strings
    final List<Set<String>> allFields = getFields(simpleEntityData);

    validateParameters(
        simpleEntityData, allFields.stream().toArray((IntFunction<Set<String>[]>) Set[]::new));

    try {
      // build the model
      return Optional.of(buildModel(simpleEntityData));

    } catch (FactoryException e) {
      log.error(
          "An error occurred when creating instance of "
              + simpleEntityData.getEntityClass().getSimpleName()
              + ".class.",
          e);
    }
    return Optional.empty();
  }

  private void isValidClass(Class<? extends UniqueEntity> clazz) {
    if (!classes.contains(clazz))
      throw new FactoryException(
          "Cannot process " + clazz.getSimpleName() + ".class with this factory!");
  }
}
