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
import java.util.TreeSet;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

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
    final List<Set<String>> allFields =
        getFields(simpleEntityData).stream()
            .map(
                set -> {
                  Set<String> treeSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                  treeSet.addAll(set);
                  return treeSet;
                })
            .collect(Collectors.toList());

    validateParameters(
        simpleEntityData, allFields.toArray((IntFunction<Set<String>[]>) Set[]::new));

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

  private void isValidClass(Class<? extends UniqueEntity> clazz) {
    if (!classes.contains(clazz))
      throw new FactoryException(
          "Cannot process " + clazz.getSimpleName() + ".class with this factory!");
  }
}
