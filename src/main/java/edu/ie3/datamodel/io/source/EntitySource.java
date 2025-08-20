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
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.csv.CsvDataSource;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.utils.QuadFunction;
import edu.ie3.datamodel.utils.TriFunction;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.Failure;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all entity sources. This class provides some functionalities that are common among
 * sources.
 */
public abstract class EntitySource {
  /** The constant log. */
  protected static final Logger log = LoggerFactory.getLogger(EntitySource.class);

  // file system for build-in entities
  private static FileSystem jarFileSystem = null;

  // convenience collectors

  /**
   * To map collector.
   *
   * @param <T> the type parameter
   * @return the collector
   */
  protected static <T extends UniqueEntity> Collector<T, ?, Map<UUID, T>> toMap() {
    return Collectors.toMap(UniqueEntity::getUuid, Function.identity());
  }

  /**
   * To set collector.
   *
   * @param <T> the type parameter
   * @return the collector
   */
  protected static <T extends Entity> Collector<T, ?, Set<T>> toSet() {
    return Collectors.toSet();
  }

  /** Instantiates a new Entity source. */
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
   * @param <C> type of the class
   * @param entityClass class to be validated
   * @param dataSource source for the fields
   * @param validator used to validate
   * @return Try object encapsulating success or failure of validation
   */
  protected static <C extends Entity> Try<Void, ValidationException> validate(
      Class<? extends C> entityClass, DataSource dataSource, SourceValidator<C> validator) {
    return validate(entityClass, () -> dataSource.getSourceFields(entityClass), validator);
  }

  /**
   * Method for validating a single source.
   *
   * @param <C> type of the class
   * @param entityClass class to be validated
   * @param sourceFields supplier for source fields
   * @param validator used to validate
   * @return Try object encapsulating success or failure of validation
   */
  protected static <C> Try<Void, ValidationException> validate(
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
   * Method to get a source for the built-in entities.
   *
   * @param clazz class from which resources will be loaded
   * @param subdirectory from the resource folder
   * @return a new {@link CsvDataSource}
   * @throws SourceException if there is an issue finding or loading resources
   */
  protected static CsvDataSource getBuildInSource(Class<?> clazz, String subdirectory)
      throws SourceException {
    try {
      URL url = clazz.getResource(subdirectory);

      if (url == null) {
        throw new SourceException("Resources not found for: " + subdirectory);
      }

      URI uri = url.toURI();
      CsvFileConnector connector;

      switch (url.getProtocol()) {
        case "file" -> connector = new CsvFileConnector(Path.of(uri));
        case "jar" -> {
          // handling resources in jar
          String[] array = uri.toString().split("!");

          if (jarFileSystem == null) {
            jarFileSystem = FileSystems.newFileSystem(URI.create(array[0]), Collections.emptyMap());
          }

          connector =
              new CsvFileConnector(jarFileSystem.getPath(array[1]), clazz::getResourceAsStream);
        }
        default ->
            throw new SourceException("Protocol " + url.getProtocol() + " is nor supported!");
      }

      return new CsvDataSource(",", connector, new FileNamingStrategy());
    } catch (URISyntaxException | IOException e) {
      throw new SourceException(e);
    }
  }

  /**
   * Universal method to get a map: uuid to {@link UniqueEntity}.
   *
   * @param <E> type of entity
   * @param entityClass subclass of {@link UniqueEntity}
   * @param dataSource source for the data
   * @param factory to build the entity
   * @return a map: uuid to {@link UniqueEntity}
   * @throws SourceException - if an error happen during reading
   */
  @SuppressWarnings("unchecked")
  protected static <E extends UniqueEntity> Map<UUID, E> getEntities(
      Class<E> entityClass,
      DataSource dataSource,
      EntityFactory<? extends UniqueEntity, EntityData> factory)
      throws SourceException {
    return unpack(
            buildEntityData(entityClass, dataSource)
                .map(data -> (Try<E, FactoryException>) factory.get(data)),
            entityClass)
        .collect(toMap());
  }

  /**
   * Universal method to get a {@link Entity} stream.
   *
   * @param <E> type of entity
   * @param <D> type of entity data
   * @param entityClass class of the entity
   * @param dataSource source for the entity
   * @param factory to build the entity
   * @param enrichFunction function to enrich the given entity data
   * @return a set of {@link Entity}s
   * @throws SourceException - if an error happen during reading
   */
  protected static <E extends Entity, D extends EntityData> Stream<E> getEntities(
      Class<E> entityClass,
      DataSource dataSource,
      EntityFactory<E, D> factory,
      WrappedFunction<EntityData, D> enrichFunction)
      throws SourceException {
    return unpack(
        buildEntityData(entityClass, dataSource, enrichFunction).map(factory::get), entityClass);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  /**
   * Returns a stream of {@link EntityData} that can be used to build instances of several subtypes
   * of {@link Entity} by a corresponding {@link EntityFactory} that consumes this data.
   *
   * @param entityClass the entity class that should be built
   * @param dataSource source for the data
   * @return a stream of entities wrapped in {@link Try}
   * @throws SourceException if there is an issue reading data
   */
  protected static Stream<Try<EntityData, SourceException>> buildEntityData(
      Class<? extends Entity> entityClass, DataSource dataSource) throws SourceException {
    return dataSource
        .getSourceData(entityClass)
        .map(
            fieldsToAttributes ->
                new Try.Success<>(new EntityData(fieldsToAttributes, entityClass)));
  }

  /**
   * Returns a stream of {@link EntityData} that can be used to build instances of several subtypes
   * of {@link Entity} by a corresponding {@link EntityFactory} that consumes this data.
   *
   * @param <E> type of entity data that extends {@link EntityData}
   * @param entityClass class of the entity
   * @param dataSource source for the data
   * @param converter function to convert {@link EntityData} to {@link E}
   * @return an entity data wrapped in {@link Try}
   * @throws SourceException if there is an issue reading data
   */
  protected static <E extends EntityData> Stream<Try<E, SourceException>> buildEntityData(
      Class<? extends Entity> entityClass,
      DataSource dataSource,
      WrappedFunction<EntityData, E> converter)
      throws SourceException {
    return buildEntityData(entityClass, dataSource).map(converter);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  /**
   * Method to build an enrich function.
   *
   * @param <E> type of entity data
   * @param <T> type of entity
   * @param <R> type of returned entity data
   * @param fieldName name of the field
   * @param entities map: uuid to {@link Entity}
   * @param defaultEntity entity that should be used if no other entity was extracted
   * @param buildingFcn to build the returned {@link EntityData}
   * @return an enrich function
   */
  protected static <E extends EntityData, T, R extends EntityData>
      WrappedFunction<E, R> enrichWithDefault(
          String fieldName,
          Map<UUID, T> entities,
          T defaultEntity,
          BiFunction<E, T, R> buildingFcn) {
    return entityData ->
        entityData
            .zip(
                extractFunction(entityData, fieldName, entities)
                    .orElse(() -> Try.Success.of(defaultEntity)))
            .map(enrichFunction(List.of(fieldName), buildingFcn));
  }

  /**
   * Method to build an enrich function.
   *
   * @param <E> type of entity data
   * @param <T> type of entity
   * @param <R> type of returned entity data
   * @param fieldName name of the field
   * @param entities map: uuid to {@link Entity}
   * @param buildingFcn to build the returned {@link EntityData}
   * @return an enrich function
   */
  protected static <E extends EntityData, T, R extends EntityData> WrappedFunction<E, R> enrich(
      String fieldName, Map<UUID, T> entities, BiFunction<E, T, R> buildingFcn) {
    return entityData ->
        entityData
            .zip(extractFunction(entityData, fieldName, entities))
            .map(enrichFunction(List.of(fieldName), buildingFcn));
  }

  /**
   * Method to build an enrich function.
   *
   * @param <E> type of entity data
   * @param <T1> type of the first entity
   * @param <T2> type of the second entity
   * @param <R> type of returned entity data
   * @param fieldName1 name of the first field
   * @param entities1 map: uuid to {@link Entity}
   * @param fieldName2 name of the second field
   * @param entities2 map: uuid to {@link Entity}
   * @param buildingFcn to build the returned {@link EntityData}
   * @return an enrich function
   */
  protected static <
          E extends EntityData, T1 extends Entity, T2 extends Entity, R extends EntityData>
      WrappedFunction<E, R> biEnrich(
          String fieldName1,
          Map<UUID, T1> entities1,
          String fieldName2,
          Map<UUID, T2> entities2,
          TriFunction<E, T1, T2, R> buildingFcn) {
    // adapting the provided function
    BiFunction<E, Pair<T1, T2>, R> adaptedBuildingFcn =
        (data, pair) -> buildingFcn.apply(data, pair.getKey(), pair.getValue());

    // extractor to get the needed entities
    WrappedFunction<E, Pair<T1, T2>> pairExtractor =
        data ->
            extractFunction(data, fieldName1, entities1)
                .zip(extractFunction(data, fieldName2, entities2));

    return entityData ->
        entityData
            .zip(pairExtractor)
            .map(enrichFunction(List.of(fieldName1, fieldName2), adaptedBuildingFcn));
  }

  /**
   * Method to build a function to create an {@link EntityData}.
   *
   * @param <E> type of given entity data
   * @param <T> type of entities
   * @param <R> type of returned entity data
   * @param fieldNames list with field names
   * @param buildingFcn to build the returned {@link EntityData}
   * @return an entity data
   */
  protected static <E extends EntityData, T, R extends EntityData>
      Function<Pair<E, T>, R> enrichFunction(
          List<String> fieldNames, BiFunction<E, T, R> buildingFcn) {
    return pair -> {
      E data = pair.getKey();
      T entities = pair.getValue();

      Map<String, String> fieldsToAttributes = data.getFieldsToValues();

      // remove fields that are passed as objects to constructor
      fieldNames.forEach(fieldsToAttributes.keySet()::remove);

      return buildingFcn.apply(data, entities);
    };
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  /**
   * Method to unpack a stream of tries.
   *
   * @param <S> type of entity
   * @param <E> type of exception
   * @param inputStream given stream
   * @param clazz class of the entity
   * @return a stream of entities
   * @throws SourceException - if an error occurred during reading
   */
  protected static <S, E extends Exception> Stream<S> unpack(
      Stream<Try<S, E>> inputStream, Class<S> clazz) throws SourceException {
    return Try.scanStream(inputStream, clazz.getSimpleName(), SourceException::new).getOrThrow();
  }

  /**
   * Method to extract an entity.
   *
   * @param <E> type of entity data
   * @param <R> type of entity
   * @param entityData data containing complex entities
   * @param fieldName name of the field
   * @param entities map: uuid to {@link Entity}
   * @return an enrichment
   */
  protected static <E extends EntityData, R> Try<R, SourceException> extractFunction(
      Try<E, SourceException> entityData, String fieldName, Map<UUID, R> entities) {
    return entityData.flatMap(
        data ->
            Try.of(() -> data.getUUID(fieldName), FactoryException.class)
                .flatMap(entityUuid -> extractFunction(entityUuid, entities))
                .transformF(
                    exception ->
                        new SourceException(
                            "Extracting UUID for field '"
                                + fieldName
                                + "' failed. Caused by: "
                                + exception.getMessage())));
  }

  /**
   * Method to extract an {@link Entity} from a given map.
   *
   * @param <T> type of entity
   * @param uuid of the entity
   * @param entityMap map: uuid to entity
   * @return a try of the {@link Entity}
   */
  protected static <T> Try<T, FactoryException> extractFunction(UUID uuid, Map<UUID, T> entityMap) {
    return Optional.ofNullable(entityMap.get(uuid))
        // We either find a matching entity for given UUID, thus return a success
        .map(entity -> Try.of(() -> entity, FactoryException.class))
        // ... or find no matching entity, returning a failure.
        .orElse(
            new Failure<>(new FactoryException("Entity with uuid " + uuid + " was not provided.")));
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  // functional interfaces

  /**
   * Wraps the function arguments with a try.
   *
   * @param <T> type of first argument
   * @param <R> type of second argument
   */
  @FunctionalInterface
  protected interface WrappedFunction<T, R>
      extends Function<Try<T, SourceException>, Try<R, SourceException>> {}

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
}
