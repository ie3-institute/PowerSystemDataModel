/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.models.UniqueEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Internal API Interface for EntityFactories
 *
 * @version 0.1
 * @since 28.01.20
 */
abstract class EntityFactory<T extends UniqueEntity, D extends EntityData> {
  public final Logger log = LogManager.getLogger(this.getClass());

  protected final List<Class<? extends T>> classes;

  public EntityFactory(Class<? extends T>... classes) {
    this.classes = Arrays.asList(classes);
  }

  public abstract Optional<T> getEntity(D entityData);

  //  public abstract Optional<Map<String, String>> getNormalizedEntityFieldValues(T entity);

  protected abstract List<Set<String>> getFields(D entityData);

  protected abstract T buildModel(D simpleEntityData);

  public List<Class<? extends T>> classes() {
    return classes;
  }

  protected <E> Set<E> newSet(E... items) {
    return new HashSet<>(Arrays.asList(items));
  }

  protected <E> Set<E> expandSet(Set<E> set, E... more) {
    return Stream.concat(Arrays.stream(more), set.stream()).collect(Collectors.toSet());
  }

  protected int validateParameters(EntityData simpleEntityData, Set<String>... fieldSets) {

    // check if the biggest set (assumed: set with all parameters) contains all fields available in
    // the class
    try {
      Set<String> realFieldSet =
          Arrays.stream(
                  Introspector.getBeanInfo(simpleEntityData.getEntityClass(), Object.class)
                      .getPropertyDescriptors())
              .filter(pd -> Objects.nonNull(pd.getReadMethod()))
              .map(FeatureDescriptor::getName)
              .collect(Collectors.toSet());
      boolean manualFieldsAreValid =
          Arrays.stream(fieldSets)
              .max(Comparator.comparing(Set::size))
              .map(biggestManualProvidedSet -> biggestManualProvidedSet.equals(realFieldSet))
              .orElse(false);
      if (!manualFieldsAreValid) {
        // build debug string
        String implementedFields = getFieldsString(realFieldSet).toString();
        String manualFieldSets = getFieldsString(fieldSets).toString();

        throw new FactoryException(
            "\nCannot proceed as the names of the implemented real fields differ from the "
                + "ones provided. \nPlease ensure that the factory field name strings match the implementation field names! "
                + "\nMaybe the class implementation changed?\nImplemented real fields:\n"
                + implementedFields
                + "All provided factory fields:\n"
                + manualFieldSets);
      }
    } catch (IntrospectionException e) {
      throw new FactoryException(
          "Unable to extract field names from "
              + simpleEntityData.getEntityClass().getSimpleName()
              + ".class! ");
    }
    Map<String, String> fieldsToValues = simpleEntityData.getFieldsToValues();

    // get all sets that match the fields to attributes
    List<Set<String>> validFieldSets =
        Arrays.stream(fieldSets)
            .filter(x -> x.equals(fieldsToValues.keySet()))
            .collect(Collectors.toList());

    if (validFieldSets.size() == 1) {
      // if we can identify a unique parameter set for a constructor, we take it and return the
      // index
      Set<String> validFieldSet = validFieldSets.get(0);
      return Arrays.asList(fieldSets).indexOf(validFieldSet);
    } else {
      // build the exception string with extensive debug information
      String providedFieldMapString =
          fieldsToValues.keySet().stream()
              .map(key -> key + " -> " + fieldsToValues.get(key))
              .collect(Collectors.joining(","));

      String providedKeysString = "[" + String.join(", ", fieldsToValues.keySet()) + "]";

      String possibleOptions = getFieldsString(fieldSets).toString();

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
              + possibleOptions);
    }
  }

  private StringBuilder getFieldsString(Set<String>... fieldSets) {
    StringBuilder possibleOptions = new StringBuilder();
    for (int i = 0; i < fieldSets.length; i++) {
      Set<String> fieldSet = fieldSets[i];
      String option = i + ": [" + String.join(", ", fieldSet) + "]\n";
      possibleOptions.append(option);
    }
    return possibleOptions;
  }
}
