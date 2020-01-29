/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.models.UniqueEntity;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Internal API Interface for EntityFactories
 *
 * @version 0.1
 * @since 28.01.20
 */
public abstract class EntityFactoryImpl<T extends UniqueEntity> {
  public final Logger log = LogManager.getLogger(this.getClass());

  private List<Class<? extends T>> classes;

  public EntityFactoryImpl(Class<? extends T>... classes) {
    this.classes = Arrays.asList(classes);
  }

  public List<Class<? extends T>> classes() {
    return classes;
  }

  public Optional<? extends T> getEntity(EntityData entityData) {
    if (!classes.contains(entityData.getEntityClass()))
      throw new FactoryException(
          "Cannot process "
              + entityData.getEntityClass().getSimpleName()
              + ".class with this factory!");

    final List<Set<String>> allFields = getFields(entityData);

    SimpleEntityData simpleEntityData = getSimpleEntityData(entityData);
    validParameters(simpleEntityData, allFields.toArray(Set[]::new));

    // build the model
    Optional<? extends T> result = Optional.empty();
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

  protected abstract List<Set<String>> getFields(EntityData entityData);

  protected abstract T buildModel(EntityData simpleEntityData);

  protected static SimpleEntityData getSimpleEntityData(EntityData entityData) {
    if (!(entityData instanceof SimpleEntityData)) {
      throw new FactoryException(
          "Invalid entity data "
              + entityData.getClass().getSimpleName()
              + " provided. Please use 'SimpleEntityData' for 'SimpleEntityFactory'!");
    } else {
      return (SimpleEntityData) entityData;
    }
  }

  protected static int validParameters(
      SimpleEntityData simpleEntityData, Set<String>... fieldSets) {

    Map<String, String> fieldsToAttributes = simpleEntityData.getFieldsToValues();

    // get all sets that match the fields to attributes
    List<Set<String>> validFieldSets =
        Arrays.stream(fieldSets)
            .filter(x -> x.equals(fieldsToAttributes.keySet()))
            .collect(Collectors.toList());

    if (validFieldSets.size() == 1) {
      // if we can identify a unique parameter set for a constructor, we take it and return the
      // index
      Set<String> validFieldSet = validFieldSets.get(0);
      return Arrays.asList(fieldSets).indexOf(validFieldSet);
    } else {
      // build the exception string with extensive debug information
      String providedFieldMapString =
          fieldsToAttributes.keySet().stream()
              .map(key -> key + " -> " + fieldsToAttributes.get(key))
              .collect(Collectors.joining(","));

      String providedKeysString = "[" + String.join(", ", fieldsToAttributes.keySet()) + "]";

      StringBuilder possibleOptions = new StringBuilder();
      for (int i = 0; i < fieldSets.length; i++) {
        Set<String> fieldSet = fieldSets[i];
        String option = i + ": [" + String.join(", ", fieldSet) + "]\n";
        possibleOptions.append(option);
      }
      throw new FactoryException(
          "The provided fields "
              + providedKeysString
              + " with data {"
              + providedFieldMapString
              + "}"
              + " are invalid for instance of "
              + simpleEntityData.getEntityClass().getSimpleName()
              + ". \nThe following fields to be passed to a constructor of "
              + simpleEntityData.getEntityClass().getSimpleName()
              + " are possible:\n"
              + possibleOptions.toString());
    }
  }

  protected static <E> Set<E> newSet(E... items) {
    return new HashSet<>(Arrays.asList(items));
  }

  protected static <E> Set<E> enhanceSet(Set<E> set, E... more) {
    return Stream.concat(Arrays.stream(more), set.stream()).collect(Collectors.toSet());
  }
}
