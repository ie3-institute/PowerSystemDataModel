/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.source.SourceValidator;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.*;
import edu.ie3.util.StringUtils;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract factory class, that is able to transfer specific "flat" information in to actual model
 * class instances.
 *
 * @param <C> Type of the intended target class.
 * @param <D> Type of the "flat" information.
 * @param <R> Type of the intended return type (might differ slightly from target class (cf. {@link
 *     edu.ie3.datamodel.io.factory.timeseries.TimeBasedValueFactory})).
 */
public abstract class Factory<C, D extends FactoryData, R> implements SourceValidator {
  public static final Logger log = LoggerFactory.getLogger(Factory.class);

  private final List<Class<? extends C>> supportedClasses;

  @SafeVarargs
  protected Factory(Class<? extends C>... supportedClasses) {
    this.supportedClasses = Arrays.asList(supportedClasses);
  }

  public List<Class<? extends C>> getSupportedClasses() {
    return supportedClasses;
  }

  /**
   * Builds entity with data from given EntityData object after doing all kinds of checks on the
   * data
   *
   * @param data EntityData (or subclass) containing the data
   * @return An entity wrapped in a {@link Success} if successful, or an exception wrapped in a
   *     {@link Failure}
   */
  public Try<R, FactoryException> get(D data) {
    isSupportedClass(data.getTargetClass());

    try {
      // build the model
      return Success.of(buildModel(data));
    } catch (Exception e) {
      return Failure.of(
          new FactoryException(
              "An error occurred when creating instance of "
                  + data.getTargetClass().getSimpleName()
                  + ".class.",
              e));
    }
  }

  /**
   * Builds entity with data from given EntityData object after doing all kinds of checks on the
   * data
   *
   * @param data EntityData (or subclass) containing the data wrapped in a {@link Try}
   * @return An entity wrapped in a {@link Success} if successful, or an exception wrapped in a
   *     {@link Failure}
   */
  public Try<R, FactoryException> get(Try<D, ?> data) {
    return data.transformF(FactoryException::new).flatMap(this::get);
  }

  /**
   * Builds model with data from given {@link FactoryData} object. Throws {@link FactoryException}
   * if something goes wrong.
   *
   * @param data {@link FactoryData} (or subclass) containing the data
   * @return model created from data
   * @throws FactoryException if the model cannot be build
   */
  protected abstract R buildModel(D data);

  /**
   * Checks, if the specific given class can be handled by this factory.
   *
   * @param desiredClass Class that should be built
   */
  private void isSupportedClass(Class<?> desiredClass) {
    if (!supportedClasses.contains(desiredClass))
      throw new FactoryException(
          "Cannot process "
              + desiredClass.getSimpleName()
              + ".class with this factory!\nThis factory can only process the following classes:\n - "
              + supportedClasses.stream()
                  .map(Class::getSimpleName)
                  .collect(Collectors.joining("\n - ")));
  }

  /**
   * Returns list of sets of attribute names that the entity requires to be built. At least one of
   * these sets needs to be delivered for entity creation to be successful.
   *
   * @return list of possible attribute sets
   */
  protected abstract List<Set<String>> getFields(Class<?> entityClass);

  /**
   * Method for validating the found fields.
   *
   * @param foundFields that were found
   * @param entityClass of the build data
   */
  public void validate(Set<String> foundFields, Class<?> entityClass) {
    List<Set<String>> fieldSets =
        getFields(entityClass).stream().map(Factory::toSnakeCase).toList();
    Set<String> harmonizedFoundFields = toSnakeCase(foundFields);

    // comparing the found fields to a list of possible fields (allows additional fields)
    // if not all fields were found in a set, this set is filtered out
    // all other fields are saved as a list
    // allows snake, camel and mixed cases
    List<Set<String>> validFieldSets =
        fieldSets.stream()
            .filter(s -> foundFields.containsAll(s) || harmonizedFoundFields.containsAll(s))
            .toList();

    if (validFieldSets.isEmpty()) {
      // build the exception string with extensive debug information
      String providedKeysString = "[" + String.join(", ", foundFields) + "]";

      String possibleOptions = getFieldsString(fieldSets).toString();

      throw new FactoryException(
          "The provided fields "
              + providedKeysString
              + " are invalid for instance of "
              + entityClass.getSimpleName()
              + ". \nThe following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of '"
              + entityClass.getSimpleName()
              + "' are possible (NOT case-sensitive!):\n"
              + possibleOptions);
    }
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
   *     field name strings to its value (e.g. a headline of a csv to column values)
   * @param fieldSets a set containing all available constructor combinations as field names
   * @return the index of the set in the fieldSets array that fits the provided entity data
   */
  protected int validateParameters(D data, Set<String>... fieldSets) {
    Map<String, String> fieldsToValues = data.getFieldsToValues();

    // get all sets that match the fields to attributes
    List<Set<String>> validFieldSets =
        Arrays.stream(fieldSets).filter(x -> x.equals(fieldsToValues.keySet())).toList();

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
              .collect(Collectors.joining(",\n"));

      String providedKeysString = "[" + String.join(", ", fieldsToValues.keySet()) + "]";

      String possibleOptions = getFieldsString(List.of(fieldSets)).toString();

      throw new FactoryException(
          "The provided fields "
              + providedKeysString
              + " with data \n{"
              + providedFieldMapString
              + "}"
              + " are invalid for instance of "
              + data.getTargetClass().getSimpleName()
              + ". \nThe following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of '"
              + data.getTargetClass().getSimpleName()
              + "' are possible (NOT case-sensitive!):\n"
              + possibleOptions);
    }
  }

  protected static StringBuilder getFieldsString(List<Set<String>> fieldSets) {
    StringBuilder possibleOptions = new StringBuilder();
    for (int i = 0; i < fieldSets.size(); i++) {
      Set<String> fieldSet = fieldSets.get(i);
      String option =
          i
              + ": ["
              + String.join(", ", fieldSet)
              + "] or ["
              + String.join(", ", toCamelCase(fieldSet))
              + "]\n";
      possibleOptions.append(option);
    }
    return possibleOptions;
  }

  /**
   * Creates a new set of attribute names from given list of attributes. This method should always
   * be used when returning attribute sets, i.e. through {@link #getFields(Class)}.
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
   * returning attribute sets, i.e. through getting the needed fields. The set maintains a
   * lexicographic order, that is case-insensitive.
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

  private static Set<String> toSnakeCase(Set<String> set) {
    TreeSet<String> newSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    newSet.addAll(set.stream().map(StringUtils::camelCaseToSnakeCase).toList());
    return newSet;
  }

  private static Set<String> toCamelCase(Set<String> set) {
    TreeSet<String> newSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    newSet.addAll(set.stream().map(StringUtils::snakeCaseToCamelCase).toList());
    return newSet;
  }
}
