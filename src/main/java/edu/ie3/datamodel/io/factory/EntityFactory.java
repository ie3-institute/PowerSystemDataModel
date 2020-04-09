/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.util.TimeTools;
import java.time.ZoneId;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Universal factory class for creating entities with {@link EntityData} data objects.
 *
 * @param <T> Type of entity that this factory can create. Can be a subclass of the entities that
 *     this factory creates.
 * @param <D> Type of data class that is required for entity creation
 * @version 0.1
 * @since 28.01.20
 */
public abstract class EntityFactory<T extends UniqueEntity, D extends EntityData> {
  public final Logger log = LogManager.getLogger(this.getClass());

  protected final List<Class<? extends T>> classes;

  /**
   * Constructor for an EntityFactory for given classes
   *
   * @param allowedClasses exactly the classes that this factory is allowed and able to build
   */
  public EntityFactory(Class<? extends T>... allowedClasses) {
    this.classes = Arrays.asList(allowedClasses);
    TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss");
  }

  /**
   * Builds entity with data from given EntityData object after doing all kinds of checks on the
   * data
   *
   * @param data EntityData (or subclass) containing the data
   * @return An entity wrapped in Option if successful, an empty option otherwise
   */
  public Optional<T> getEntity(D data) {
    isValidClass(data.getEntityClass());

    // magic: case-insensitive get/set calls on set strings
    final List<Set<String>> allFields = getFields(data);

    validateParameters(data, allFields.stream().toArray((IntFunction<Set<String>[]>) Set[]::new));

    try {
      // build the model
      return Optional.of(buildModel(data));
    } catch (FactoryException e) {
      // only catch FactoryExceptions, as more serious exceptions should be handled elsewhere
      log.error(
          "An error occurred when creating instance of "
              + data.getEntityClass().getSimpleName()
              + ".class.",
          e);
    }
    return Optional.empty();
  }

  private void isValidClass(Class<? extends UniqueEntity> entityClass) {
    if (!classes.contains(entityClass))
      throw new FactoryException(
          "Cannot process "
              + entityClass.getSimpleName()
              + ".class with this factory!\nThis factory can only process the following classes:\n - "
              + classes.stream().map(Class::getSimpleName).collect(Collectors.joining("\n - ")));
  }

  /**
   * Returns list of sets of attribute names that the entity requires to be built. At least one of
   * these sets needs to be delivered for entity creation to be successful.
   *
   * @param data EntityData (or subclass) containing the data
   * @return list of possible attribute sets
   */
  protected abstract List<Set<String>> getFields(D data);

  /**
   * Builds entity with data from given EntityData object. Throws {@link FactoryException} if
   * something goes wrong.
   *
   * @param data EntityData (or subclass) containing the data
   * @return entity created from data
   * @throws FactoryException if the model cannot be build
   */
  protected abstract T buildModel(D data);

  public List<Class<? extends T>> classes() {
    return classes;
  }

  /**
   * Creates a new set of attribute names from given list of attributes. This method should always
   * be used when returning attribute sets, i.e. through {@link #getFields(EntityData)}.
   *
   * @param attributes attribute names
   * @return new set exactly containing attribute names
   */
  protected TreeSet<String> newSet(String... attributes) {
    TreeSet<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    set.addAll(Arrays.asList(attributes));
    return set;
  }

  /**
   * Expands a set of attributes with further attributes. This method should always be used when
   * returning attribute sets, i.e. through {@link #getFields(EntityData)}.
   *
   * @param attributeSet set of attributes to expand
   * @param more attribute names to expand given set with
   * @return new set exactly containing given attribute set plus additional attributes
   */
  protected TreeSet<String> expandSet(Set<String> attributeSet, String... more) {
    TreeSet<String> newSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    newSet.addAll(attributeSet);
    newSet.addAll(Arrays.asList(more));
    return newSet;
  }

  /**
   * Validates the factory specific constructor parameters in two ways. 1) the biggest set of the
   * provided field sets is compared against fields the class implements. If this test passes then
   * we know for sure that the field names at least in the biggest constructor are equal to the
   * provided factory strings 2) if 1) passes, the provided entity data (which is equal to the data
   * e.g. read from the outside) is compared to all available constructor parameters provided by the
   * fieldSets Array. If we find exactly one constructor, that matches the field names we can
   * proceed. Otherwise a detailed exception message is thrown.
   *
   * @param data the entity containing at least the entity class as well a mapping of the provided
   *     field name strings to its value (e.g. a headline of a csv -> column values)
   * @param fieldSets a set containing all available constructor combinations as field names
   * @return the index of the set in the fieldSets array that fits the provided entity data
   */
  protected int validateParameters(D data, Set<String>... fieldSets) {
    Map<String, String> fieldsToValues = data.getFieldsToValues();

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
              + data.getEntityClass().getSimpleName()
              + ". \nThe following fields to be passed to a constructor of '"
              + data.getEntityClass().getSimpleName()
              + "' are possible (NOT case-sensitive!):\n"
              + possibleOptions);
    }
  }

  private static StringBuilder getFieldsString(Set<String>... fieldSets) {
    StringBuilder possibleOptions = new StringBuilder();
    for (int i = 0; i < fieldSets.length; i++) {
      Set<String> fieldSet = fieldSets[i];
      String option = i + ": [" + String.join(", ", fieldSet) + "]\n";
      possibleOptions.append(option);
    }
    return possibleOptions;
  }
}
