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
public abstract class SimpleEntityFactory<T extends UniqueEntity>
    extends EntityFactory<T, SimpleEntityData> {

  public SimpleEntityFactory(Class<? extends T>... classes) {
    super(classes);
  }

  @Override
  public Optional<T> getEntity(SimpleEntityData simpleEntityData) {
    if (!classes.contains(simpleEntityData.getEntityClass()))
      throw new FactoryException(
          "Cannot process "
              + simpleEntityData.getEntityClass().getSimpleName()
              + ".class with this factory!");

    final List<Set<String>> allFields = getFields(simpleEntityData);

    validateParameters(simpleEntityData, allFields.toArray(Set[]::new));

    // build the model
    Optional<T> result = Optional.empty();
    try {

      result = Optional.of(buildModel(simpleEntityData));

    } catch (Exception e) {
      log.error(
          "An error occurred when creating instance of "
              + simpleEntityData.getEntityClass().getSimpleName()
              + ".class.",
          e);
    }
    return result;
  }

  //    @Override
  //    protected abstract List<Set<String>> getFields(SimpleEntityData simpleEntityData);
  //
  //    @Override
  //    protected abstract T buildModel(SimpleEntityData simpleEntityData);
}
