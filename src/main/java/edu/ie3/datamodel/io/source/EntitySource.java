/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.FailedValidationException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.utils.QuadFunction;
import edu.ie3.datamodel.utils.TriFunction;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.Failure;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all entity sources. This class provides some functionalities that are common among
 * sources.
 */
public abstract class EntitySource {
  protected static final Logger log = LoggerFactory.getLogger(EntitySource.class);

  // default collectors

  protected static <T extends UniqueEntity> Collector<T, ?, Map<UUID, T>> toMap() {
    return Collectors.toMap(UniqueEntity::getUuid, Function.identity());
  }

  protected static <T extends Entity> Set<T> toSet(Stream<T> stream) {
    return stream.collect(Collectors.toSet());
  }

  protected EntitySource() {}

  /**
   * Method for validating a given {@link EntitySource}.
   *
   * @throws ValidationException - if an error occurred while validating the source
   */
  public abstract void validate() throws ValidationException;

  /**
   * Method for validating a single source.
   *
   * @param entityClass class to be validated
   * @param dataSource source for the fields
   * @param validator used to validate
   * @param <C> type of the class
   */
  protected final <C extends Entity> Try<Void, ValidationException> validate(
      Class<? extends C> entityClass, DataSource dataSource, SourceValidator<C> validator) {
    return validate(entityClass, () -> dataSource.getSourceFields(entityClass), validator);
  }

  /**
   * Method for validating a single source.
   *
   * @param entityClass class to be validated
   * @param sourceFields supplier for source fields
   * @param validator used to validate
   * @param <C> type of the class
   */
  protected final <C> Try<Void, ValidationException> validate(
      Class<? extends C> entityClass,
      Try.TrySupplier<Optional<Set<String>>, SourceException> sourceFields,
      SourceValidator<C> validator) {
    return Try.of(sourceFields, SourceException.class)
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
                fieldsOpt
                    .map(fields -> validator.validate(fields, entityClass))
                    .orElse(Try.Success.empty()));
  }

  /**
   * Returns a stream of {@link EntityData} that can be used to build instances of several subtypes
   * of {@link Entity} by a corresponding {@link EntityFactory} that consumes this data.
   *
   * @param entityClass the entity class that should be build
   * @param dataSource source for the data
   * @return a stream of the entity data wrapped in a {@link Try}
   */
  protected static Stream<Try<EntityData, SourceException>> buildEntityData(
      Class<? extends Entity> entityClass, DataSource dataSource) {
    return Try.of(() -> dataSource.getSourceData(entityClass), SourceException.class)
        .convert(
            data ->
                data.map(
                    fieldsToAttributes ->
                        new Try.Success<>(new EntityData(fieldsToAttributes, entityClass))),
            exception -> Stream.of(Failure.of(exception)));
  }

  /**
   * Returns a stream of {@link EntityData} that can be used to build instances of several subtypes
   * of {@link Entity} by a corresponding {@link EntityFactory} that consumes this data.
   *
   * @param entityClass class of the entity
   * @param dataSource source for the data
   * @param fcn to convert {@link EntityData} to {@link E}
   * @return an entity data
   * @param <E> type of entity data
   */
  protected static <E extends EntityData> Stream<Try<E, SourceException>> buildEntityData(
      Class<? extends Entity> entityClass,
      DataSource dataSource,
      Function<Try<EntityData, SourceException>, Try<E, SourceException>> fcn) {
    return buildEntityData(entityClass, dataSource).map(fcn);
  }

  /**
   * Universal method to get a map: uuid to {@link UniqueEntity}.
   *
   * @param entityClass subclass of {@link UniqueEntity}
   * @param dataSource source for the data
   * @param factory to build the entity
   * @return a map: uuid to {@link UniqueEntity}
   * @param <E> type of entity
   * @throws SourceException - if an error happen during reading
   */
  @SuppressWarnings("unchecked")
  protected static <E extends UniqueEntity> Map<UUID, E> getEntities(
      Class<E> entityClass,
      DataSource dataSource,
      EntityFactory<? extends UniqueEntity, EntityData> factory)
      throws SourceException {
    return unpackMap(
        buildEntityData(entityClass, dataSource)
            .map(data -> (Try<E, FactoryException>) factory.get(data)),
        entityClass);
  }

  /**
   * Universal method to get a {@link Entity} stream.
   *
   * @param entityClass class of the entity
   * @param dataSource source for the entity
   * @param factory to build the entity
   * @param fcn function to enrich the given entity data
   * @return a set of {@link Entity}s
   * @param <E> type of entity
   * @param <D> type of entity data
   * @throws SourceException - if an error happen during reading
   */
  protected static <E extends Entity, D extends EntityData> Stream<E> getEntities(
      Class<E> entityClass,
      DataSource dataSource,
      EntityFactory<E, D> factory,
      Function<Try<EntityData, SourceException>, Try<D, SourceException>> fcn)
      throws SourceException {
    return unpack(buildEntityData(entityClass, dataSource, fcn).map(factory::get), entityClass);
  }

  protected static <S extends UniqueEntity> Map<UUID, S> unpackMap(
      Stream<Try<S, FactoryException>> inputStream, Class<S> entityClass) throws SourceException {
    return unpack(inputStream, entityClass).collect(toMap());
  }

  protected static <S, E extends Exception> Stream<S> unpack(
      Stream<Try<S, E>> inputStream, Class<S> clazz) throws SourceException {
    return Try.scanStream(inputStream, clazz.getSimpleName())
        .transformF(SourceException::new)
        .getOrThrow();
  }

  /**
   * Method to build an {@link Enrichment}.
   *
   * @param entityData data containing complex entities
   * @param fieldName name of the field
   * @param entities map: uuid to {@link Entity}
   * @param defaultEntity entity to use if no other entity was found
   * @return an enrichment with fallback value
   * @param <E> type of entity data
   * @param <R> type of entity
   */
  protected static <E extends EntityData, R extends Entity>
      Enrichment<R> buildEnrichmentWithDefault(
          Try<E, SourceException> entityData,
          String fieldName,
          Map<UUID, R> entities,
          R defaultEntity) {
    return buildEnrichment(entityData, fieldName, entities).orDefault(defaultEntity);
  }

  /**
   * Method to build an {@link Enrichment}.
   *
   * @param entityData data containing complex entities
   * @param fieldName name of the field
   * @param entities map: uuid to {@link Entity}
   * @return an enrichment
   * @param <E> type of entity data
   * @param <R> type of entity
   */
  protected static <E extends EntityData, R extends Entity> Enrichment<R> buildEnrichment(
      Try<E, SourceException> entityData, String fieldName, Map<UUID, R> entities) {
    return new Enrichment<>(
        fieldName,
        entityData.flatMap(
            data ->
                Try.of(() -> data.getUUID(fieldName), FactoryException.class)
                    .transformF(
                        exception ->
                            new SourceException(
                                "Extracting UUID field "
                                    + fieldName
                                    + " from entity data "
                                    + entityData
                                    + " failed.",
                                exception))
                    .flatMap(entityUuid -> getEntity(entityUuid, entities))));
  }

  /**
   * Method to extract an {@link Entity} from a given map.
   *
   * @param uuid of the entity
   * @param entityMap map: uuid to entity
   * @return a try of the {@link Entity}
   * @param <T> type of entity
   */
  protected static <T> Try<T, SourceException> getEntity(UUID uuid, Map<UUID, T> entityMap) {
    return Optional.ofNullable(entityMap.get(uuid))
        // We either find a matching entity for given UUID, thus return a success
        .map(entity -> Try.of(() -> entity, SourceException.class))
        // ... or find no matching entity, returning a failure.
        .orElse(
            new Failure<>(new SourceException("Entity with uuid " + uuid + " was not provided.")));
  }

  /**
   * Method to enrich an {@link EntityData} with an entities. Mostly used with {@link
   * EnrichFunction}.
   *
   * @param entityData to enrich
   * @param enrichment for enriching
   * @param fcn to build the returned {@link EntityData}
   * @return a new entity data
   * @param <E> type of entity data
   * @param <T> type of entity
   * @param <R> type of returned entity data
   */
  protected static <E extends EntityData, T extends Entity, R extends EntityData>
      Try<R, SourceException> enrich(
          Try<E, SourceException> entityData, Enrichment<T> enrichment, BiFunction<E, T, R> fcn) {
    return entityData.flatMap(
        data ->
            enrichment.map(
                (fieldName, entity) -> {
                  Map<String, String> fieldsToAttributes = data.getFieldsToValues();

                  // remove fields that are passed as objects to constructor
                  fieldsToAttributes.keySet().remove(fieldName);

                  return fcn.apply(data, entity);
                }));
  }

  /**
   * Method to enrich an {@link EntityData} with two entities. Mostly used with {@link
   * BiEnrichFunction}.
   *
   * @param entityData to enrich
   * @param enrichment1 first enrichment
   * @param enrichment2 second enrichment
   * @param fcn to build the returned {@link EntityData}
   * @return a new entity data
   * @param <E> type of entity data
   * @param <T1> type of first entity
   * @param <T2> type of second entity
   * @param <R> type of returned entity data
   */
  protected static <
          E extends EntityData, T1 extends Entity, T2 extends Entity, R extends EntityData>
      Try<R, SourceException> biEnrich(
          Try<E, SourceException> entityData,
          Enrichment<T1> enrichment1,
          Enrichment<T2> enrichment2,
          TriFunction<E, T1, T2, R> fcn) {
    return entityData.flatMap(
        data ->
            enrichment1
                .entity
                .zip(enrichment2.entity)
                .map(
                    zippedData -> {
                      Map<String, String> fieldsToAttributes = data.getFieldsToValues();

                      // remove fields that are passed as objects to constructor
                      fieldsToAttributes.keySet().remove(enrichment1.fieldName);
                      fieldsToAttributes.keySet().remove(enrichment2.fieldName);

                      return fcn.apply(data, zippedData.getKey(), zippedData.getValue());
                    }));
  }

  // functional interfaces

  /**
   * Function for enriching an {@link EntityData} with an {@link Entity}.
   *
   * @param <E> type of entity data
   * @param <T> type of entity
   * @param <R> type of returned entity data
   */
  @FunctionalInterface
  protected interface EnrichFunction<E extends EntityData, T extends Entity, R extends EntityData>
      extends BiFunction<Try<E, SourceException>, Map<UUID, T>, Try<R, SourceException>> {}

  /**
   * Function for enriching an {@link EntityData} with two {@link Entity}.
   *
   * @param <E> type of entity data
   * @param <T1> type of first entity
   * @param <T2> type of second entity
   * @param <R> type of returned entity data
   */
  @FunctionalInterface
  protected interface BiEnrichFunction<
          E extends EntityData, T1 extends Entity, T2 extends Entity, R extends EntityData>
      extends TriFunction<
          Try<E, SourceException>, Map<UUID, T1>, Map<UUID, T2>, Try<R, SourceException>> {}

  /**
   * Function for enriching an {@link EntityData} with three {@link Entity}.
   *
   * @param <E> type of entity data
   * @param <T1> type of first entity
   * @param <T2> type of second entity
   * @param <T3> type of third entity
   * @param <R> type of returned entity data
   */
  @FunctionalInterface
  protected interface TriEnrichFunction<
          E extends EntityData,
          T1 extends Entity,
          T2 extends Entity,
          T3 extends Entity,
          R extends EntityData>
      extends QuadFunction<
          Try<E, SourceException>,
          Map<UUID, T1>,
          Map<UUID, T2>,
          Map<UUID, T3>,
          Try<R, SourceException>> {}

  /**
   * Container class for enriching an {@link EntityData}.
   *
   * @param fieldName name of the field
   * @param entity try of the entity
   * @param <T> type of the entity
   */
  protected record Enrichment<T extends Entity>(String fieldName, Try<T, SourceException> entity) {

    /**
     * Replaces a {@link Failure} with the given entity
     *
     * @param defaultEntity given entity
     * @return a new {@link Enrichment}
     */
    public Enrichment<T> orDefault(T defaultEntity) {
      return new Enrichment<>(fieldName, entity.orElse(() -> Try.Success.of(defaultEntity)));
    }

    /**
     * Method to map the entity while also using the field name.
     *
     * @param mapper function
     * @return a new {@link Try}
     * @param <R> type of entity
     */
    public <R> Try<R, SourceException> map(BiFunction<String, T, R> mapper) {
      return entity.map(data -> mapper.apply(fieldName, data));
    }
  }
}
