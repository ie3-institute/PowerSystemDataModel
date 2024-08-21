/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.DuplicateEntitiesException;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource.MappingEntry;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.IdCoordinateInput;
import edu.ie3.datamodel.models.result.CongestionResult;
import edu.ie3.datamodel.models.result.ModelResultEntity;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.Success;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Validation utils for checking the uniqueness of a given collection of entities. */
public class UniquenessValidationUtils extends ValidationUtils {

  // default field set supplier
  protected static final FieldSetSupplier<UniqueEntity> uuidFieldSupplier =
      entity -> Set.of(entity.getUuid());
  protected static final FieldSetSupplier<AssetInput> idFieldSupplier = e -> Set.of(e.getId());
  protected static final FieldSetSupplier<ModelResultEntity> modelResultFieldSupplier =
      entity -> Set.of(entity.getTime(), entity.getInputModel());
  protected static final FieldSetSupplier<CongestionResult> congestionResultFieldSupplier =
      entity -> Set.of(entity.getTime(), entity.getSubgrid());
  protected static final FieldSetSupplier<MappingEntry> mappingFieldSupplier =
      entity -> Set.of(entity.getEntity());
  protected static final FieldSetSupplier<IdCoordinateInput> idCoordinateSupplier =
      entity -> Set.of(entity.id(), entity.point());
  protected static final FieldSetSupplier<TimeBasedValue<WeatherValue>> weatherValueFieldSupplier =
      entity -> Set.of(entity.getTime(), entity.getValue().getCoordinate());

  /**
   * Checks the uniqueness of a collection of {@link UniqueEntity}.
   *
   * <p>Caution: Only the field {@code uuid} is checked for uniqueness here
   *
   * @param entities to be checked
   * @throws DuplicateEntitiesException if uniqueness is violated
   */
  public static void checkUniqueEntities(Collection<? extends UniqueEntity> entities)
      throws DuplicateEntitiesException {
    checkUniqueness(entities, uuidFieldSupplier).getOrThrow();
  }

  /**
   * Checks the uniqueness of a collection of {@link AssetInput}.
   *
   * @param entities to be checked
   * @throws DuplicateEntitiesException if uniqueness is violated
   */
  public static void checkAssetUniqueness(Collection<? extends AssetInput> entities)
      throws DuplicateEntitiesException {

    List<DuplicateEntitiesException> exceptions =
        Try.getExceptions(
            Try.ofVoid(() -> checkUniqueEntities(entities), DuplicateEntitiesException.class),
            checkUniqueness(entities, idFieldSupplier));

    if (!exceptions.isEmpty()) {
      throw new DuplicateEntitiesException("AssetInput", exceptions);
    }
  }

  /**
   * Checks the uniqueness of a collection of {@link CongestionResult}.
   *
   * @param entities to be checked
   * @throws DuplicateEntitiesException if uniqueness is violated
   */
  public static void checkCongestionResultUniqueness(
      Collection<? extends CongestionResult> entities) throws DuplicateEntitiesException {
    checkUniqueness(entities, congestionResultFieldSupplier).getOrThrow();
  }

  /**
   * Checks the uniqueness of a collection of {@link ModelResultEntity}.
   *
   * @param entities to be checked
   * @throws DuplicateEntitiesException if uniqueness is violated
   */
  public static void checkModelResultUniqueness(Collection<? extends ModelResultEntity> entities)
      throws DuplicateEntitiesException {
    checkUniqueness(entities, modelResultFieldSupplier).getOrThrow();
  }

  /**
   * Checks the uniqueness of a collection of {@link MappingEntry}.
   *
   * @param entities to be checked
   * @throws DuplicateEntitiesException if uniqueness is violated
   */
  public static void checkMappingEntryUniqueness(Collection<MappingEntry> entities)
      throws DuplicateEntitiesException {
    checkUniqueness(entities, mappingFieldSupplier).getOrThrow();
  }

  /**
   * Checks the uniqueness of a collection of
   *
   * @param entities to be checked
   * @throws DuplicateEntitiesException if uniqueness is violated
   */
  public static void checkIdCoordinateUniqueness(Collection<IdCoordinateInput> entities)
      throws DuplicateEntitiesException {
    checkUniqueness(entities, idCoordinateSupplier).getOrThrow();
  }

  /**
   * Checks the uniqueness of TimeBasedWeatherValues.
   *
   * @param entities to be checked
   * @throws DuplicateEntitiesException if uniqueness is violated
   */
  public static void checkWeatherUniqueness(Collection<TimeBasedValue<WeatherValue>> entities)
      throws DuplicateEntitiesException {
    checkUniqueness(entities, weatherValueFieldSupplier).getOrThrow();
  }

  /**
   * Checking the uniqueness for a given {@link Entity}.
   *
   * @param entities to be checked
   * @param supplier for the field set
   * @return a try object
   * @param <E> type of entity
   */
  private static <E extends Entity> Try<Void, DuplicateEntitiesException> checkUniqueness(
      Collection<? extends E> entities, FieldSetSupplier<E> supplier) {
    if (entities.size() < 2) {
      return Success.empty();
    }

    return entities.stream()
        .findAny()
        .map(
            entity -> {
              List<Set<Object>> elements = entities.stream().map(supplier::getFieldSets).toList();
              Set<Set<Object>> uniqueElements = new HashSet<>(elements);

              return Try.ofVoid(
                  elements.size() != uniqueElements.size(),
                  () -> buildDuplicationException(entity.getClass(), elements));
            })
        .orElse(Success.empty());
  }

  /**
   * Method for building a {@link DuplicateEntitiesException}.
   *
   * @param entityClass class of the entity
   * @param notUniqueElements list of not unique elements
   * @return a {@link DuplicateEntitiesException}
   */
  protected static DuplicateEntitiesException buildDuplicationException(
      Class<? extends Entity> entityClass, List<Set<Object>> notUniqueElements) {
    String fieldName =
        notUniqueElements.get(0).stream()
            .map(f -> f.getClass().getSimpleName())
            .collect(Collectors.joining("-"));

    // calculating the elements that violate the uniqueness
    Map<Set<Object>, Long> counts =
        notUniqueElements.stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    String duplicates =
        counts.entrySet().stream()
            .filter(e -> e.getValue() > 1)
            .map(m -> String.join("-", m.getKey().toString()))
            .collect(Collectors.joining(",\n"));

    return new DuplicateEntitiesException(
        "'"
            + entityClass.getSimpleName()
            + "' entities with duplicated "
            + fieldName
            + " key, but different field "
            + "values found! Affected primary keys: "
            + duplicates);
  }

  /**
   * Supplier for sets of fields that are required to be unique throughout the whole dataset. For
   * each set, the combination of all members of the set must be unique. This means that individual
   * members of the set are not required to be unique, but only their combination. A set can contain
   * only a single member. In this case the single field must be unique throughout the dataset.
   *
   * @param <E> type of entity
   */
  @FunctionalInterface
  protected interface FieldSetSupplier<E extends Entity> {
    Set<Object> getFieldSets(E entity);
  }
}
