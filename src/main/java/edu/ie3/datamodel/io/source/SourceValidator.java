/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.FailedValidationException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.Failure;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceValidator<C> {
  public static final Logger log = LoggerFactory.getLogger(SourceValidator.class);

  private final List<Set<String>> fields;

  public SourceValidator() {
    fields = new ArrayList<>();
  }

  public SourceValidator(Fields fields) {
    this.fields = fields.fields;
  }

  /**
   * Method for validating a data source.
   *
   * @param actualFields fields that were found in the source data
   * @param entityClass that should be buildable from the source data
   * @return either an exception wrapped by a {@link Failure} or an empty success
   */
  public Try<Void, ValidationException> validate(
      Set<String> actualFields, Class<? extends C> entityClass) {
    List<Set<String>> fieldSets = getFields(entityClass);
    Set<String> harmonizedActualFields = toCamelCase(actualFields);

    // comparing the actual fields to a list of possible fields (allows additional fields)
    // if not all fields were found in a set, this set is filtered out
    // all other fields are saved as a list
    // allows snake, camel and mixed cases
    List<Set<String>> validFieldSets =
        fieldSets.stream().filter(harmonizedActualFields::containsAll).toList();

    if (validFieldSets.isEmpty()) {
      // build the exception string with extensive debug information
      String providedKeysString = "[" + String.join(", ", actualFields) + "]";

      String possibleOptions = getFieldsString(fieldSets).toString();

      return Failure.of(
          new FailedValidationException(
              "The provided fields "
                  + providedKeysString
                  + " are invalid for instance of '"
                  + entityClass.getSimpleName()
                  + "'. \nThe following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of '"
                  + entityClass.getSimpleName()
                  + "' are possible (NOT case-sensitive!):\n"
                  + possibleOptions));
    } else {
      // find all unused fields
      Set<String> unused = getUnusedFields(harmonizedActualFields, validFieldSets);

      if (!unused.isEmpty()) {
        log.info(
            "The following additional fields were found for entity class of '{}': {}",
            entityClass.getSimpleName(),
            unused);
      }

      return Try.Success.empty();
    }
  }

  /**
   * Returns list of sets of attribute names that the entity requires to be built. At least one of
   * these sets needs to be delivered for entity creation to be successful.
   *
   * @param entityClass class that can be used to specify the fields that are returned
   * @return list of possible attribute sets
   */
  protected List<Set<String>> getFields(Class<?> entityClass) {
    if (fields.isEmpty()) {
      MethodType type = MethodType.methodType(Fields.class);

      try {
        MethodHandle handle = MethodHandles.lookup().findStatic(entityClass, "getFields", type);
        Fields fields = (Fields) handle.invoke();
        return fields.fields;
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    } else {
      return fields;
    }
  }

  /**
   * Method to find and return additional fields that were found in a source and are not used by the
   * data model. This method will return the minimal unused fields among all field sets, meaning
   * that the set of actual fields is compared to the field set with the least unused fields.
   *
   * @param actualFields found in the source
   * @param validFieldSets that contains at least all fields found in the source
   * @return a set of unused fields
   */
  protected Set<String> getUnusedFields(
      Set<String> actualFields, List<Set<String>> validFieldSets) {
    // checking for additional fields
    // and returning the set with the least additional fields
    return validFieldSets.stream()
        .map(
            s -> {
              Set<String> set = new HashSet<>(actualFields);
              set.removeAll(s);
              return set;
            })
        .min(Comparator.comparing(Collection::size))
        .orElse(Collections.emptySet());
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
              + String.join(", ", toSnakeCase(fieldSet))
              + "]\n";
      possibleOptions.append(option);
    }
    return possibleOptions;
  }

  public record Fields(List<Set<String>> fields) {

    @SafeVarargs
    public Fields(Set<String>... sets) {
      this(Arrays.asList(sets));
    }

    public Fields(String... fields) {
      this(List.of(newSet(fields)));
    }

    public Fields add(String... fields) {
      List<Set<String>> expandedSets = new ArrayList<>();

      for (Set<String> set : this.fields) {
        expandedSets.add(expandSet(set, fields));
      }

      return new Fields(expandedSets);
    }

    public Fields addOptional(String field) {
      List<Set<String>> expandedSets = new ArrayList<>(fields);

      for (Set<String> set : fields) {
        expandedSets.add(expandSet(set, field));
      }

      return new Fields(expandedSets);
    }
  }

  /**
   * Creates a new set of attribute names from given list of attributes. This method should always
   * be used when returning attribute sets.
   *
   * @param attributes attribute names
   * @return new set exactly containing attribute names
   */
  public static TreeSet<String> newSet(String... attributes) {
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
  public static TreeSet<String> expandSet(Set<String> attributeSet, String... more) {
    TreeSet<String> newSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    newSet.addAll(attributeSet);
    newSet.addAll(Arrays.asList(more));
    return newSet;
  }

  public static Set<String> toSnakeCase(Set<String> set) {
    TreeSet<String> newSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    newSet.addAll(set.stream().map(edu.ie3.util.StringUtils::camelCaseToSnakeCase).toList());
    return newSet;
  }

  public static Set<String> toCamelCase(Set<String> set) {
    TreeSet<String> newSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    newSet.addAll(set.stream().map(edu.ie3.util.StringUtils::snakeCaseToCamelCase).toList());
    return newSet;
  }

  public static Set<String> toLowerCase(Set<String> set) {
    TreeSet<String> newSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    newSet.addAll(set.stream().map(String::toLowerCase).toList());
    return newSet;
  }
}
