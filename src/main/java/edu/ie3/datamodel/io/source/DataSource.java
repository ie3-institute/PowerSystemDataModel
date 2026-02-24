/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.utils.CollectionUtils.*;

import edu.ie3.datamodel.exceptions.FailedValidationException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.naming.FieldNaming;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.utils.CollectionUtils;
import edu.ie3.datamodel.utils.Try;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Interface that include functionalities for data sources in the database table, csv file etc. */
public interface DataSource {

  Logger log = LoggerFactory.getLogger(DataSource.class);

  /**
   * Method to retrieve the fields found in the source.
   *
   * @param entityClass class of the source
   * @return an option for the found fields
   */
  Optional<Set<String>> getSourceFields(Class<? extends Entity> entityClass) throws SourceException;

  /** Creates a stream of maps that represent the rows in the database */
  Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass)
      throws SourceException;

  /**
   * Method for validating a data source.
   *
   * @param entityClass that should be buildable from the source data
   * @return either an exception wrapped by a {@link Try.Failure} or an empty success
   */
  default Try<Void, ValidationException> validate(Class<? extends Entity> entityClass)
      throws SourceException {
    return Try.of(() -> getSourceFields(entityClass), SourceException.class)
        .transformF(
            se ->
                (ValidationException)
                    new FailedValidationException(
                        "Validation for entity "
                            + entityClass
                            + " failed because of an error related to its source.",
                        se))
        .flatMap(
            fieldsOpt ->
                fieldsOpt.map(fields -> validate(fields, entityClass)).orElse(Try.Success.empty()));
  }

  /**
   * Method for validating a data source.
   *
   * @param actualFields fields that were found in the source data
   * @param entityClass that should be buildable from the source data
   * @return either an exception wrapped by a {@link Try.Failure} or an empty success
   */
  static <C> Try<Void, ValidationException> validate(
      Set<String> actualFields, Class<C> entityClass) {
    return validate(
        actualFields,
        entityClass,
        FieldNaming.getMandatoryFields(entityClass),
        FieldNaming.getOptionalFields(entityClass),
        FieldNaming.getUnsupportedFields(entityClass));
  }

  /**
   * Method for validating a data source.
   *
   * @param actualFields fields that were found in the source data
   * @param entityClass that should be buildable from the source data
   * @param mandatoryFields a list of mandatory field combinations
   * @param optionalFields a set of optional fields
   * @param unsupportedFields a set of unsupported fields
   * @return either an exception wrapped by a {@link Try.Failure} or an empty success
   */
  static Try<Void, ValidationException> validate(
      Set<String> actualFields,
      Class<?> entityClass,
      List<Set<String>> mandatoryFields,
      Set<String> optionalFields,
      Set<String> unsupportedFields) {
    if (mandatoryFields.isEmpty()) {
      return Try.Failure.of(
          new FailedValidationException(
              "Could not validate the source because no mandatory fields were provided!"));
    }

    Set<String> harmonizedOptionalFields = toCamelCase(optionalFields);
    Set<String> harmonizedActualFields = toCamelCase(actualFields);

    // check if the actual set is equal to at least one set of mandatory fields
    // allows snake, camel and mixed cases
    List<Set<String>> validSchemes =
        mandatoryFields.stream()
            .map(CollectionUtils::toCamelCase)
            .filter(harmonizedActualFields::containsAll)
            .toList();

    if (validSchemes.isEmpty()) {
      // build the exception string with extensive debug information
      String providedKeysString = "[" + String.join(", ", actualFields) + "]";

      String possibleOptions =
          getFieldsString(getAllFieldCombinations(mandatoryFields, harmonizedOptionalFields))
              .toString();

      return Try.Failure.of(
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
      if (!unsupportedFields.isEmpty()) {
        // check for unsupported fields
        Set<String> harmonizedUnsupportedFields = toCamelCase(unsupportedFields);

        Set<String> unsupported =
            harmonizedActualFields.stream()
                .filter(harmonizedUnsupportedFields::contains)
                .collect(Collectors.toSet());

        if (!unsupported.isEmpty()) {
          log.warn(
              "Found some unsupported fields for entity class of '{}': {}",
              entityClass.getSimpleName(),
              unsupported);
        }
      }

      // find all unused fields
      Set<String> unused = getUnusedFields(harmonizedActualFields, validSchemes);

      if (!unused.isEmpty()) {
        log.info(
            "The following additional fields were found for entity class of '{}': {}",
            entityClass.getSimpleName(),
            unused);
      }

      return Try.Success.empty();
    }
  }

  private static List<Set<String>> getAllFieldCombinations(
      List<Set<String>> mandatoryFields, Set<String> optionalFields) {
    List<Set<String>> allFieldSets = new ArrayList<>();

    for (Set<String> mandatoryFieldSet : mandatoryFields) {
      List<Set<String>> fieldSets = new ArrayList<>();
      fieldSets.add(mandatoryFieldSet);

      for (String optional : optionalFields) {
        List<Set<String>> tmp = new ArrayList<>(fieldSets);

        for (Set<String> set : fieldSets) {
          tmp.add(expandSet(set, optional));
        }

        fieldSets = tmp;
      }

      allFieldSets.addAll(fieldSets);
    }

    return allFieldSets;
  }

  private static StringBuilder getFieldsString(List<Set<String>> fieldSets) {
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

  /**
   * Method to find and return additional fields that were found in a source and are not used by the
   * data model. This method will return the minimal unused fields among all field sets, meaning
   * that the set of actual fields is compared to the field set with the least unused fields.
   *
   * @param actualFields found in the source
   * @param validFieldSets that contains at least all fields found in the source
   * @return a set of unused fields
   */
  private static Set<String> getUnusedFields(
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
}
