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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

  protected abstract List<Set<String>> getFields(D entityData);

  protected abstract T buildModel(D simpleEntityData);

  public List<Class<? extends T>> classes() {
    return classes;
  }

  protected TreeSet<String> newSet(String... items) {
    TreeSet<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    set.addAll(Arrays.asList(items));
    return set;
  }

  protected TreeSet<String> expandSet(Set<String> set, String... more) {
    TreeSet<String> newSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    newSet.addAll(set);
    newSet.addAll(Arrays.asList(more));
    return newSet;
  }

  protected int validateParameters(EntityData simpleEntityData, Set<String>... fieldSets) {

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
      // FIXME indexOf ist langsam
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
}
