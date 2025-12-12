/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.io.naming.EntityFieldNames.UUID_FIELD_NAME;

import edu.ie3.datamodel.exceptions.FailedValidationException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.csv.CsvDataSource;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.UniqueInputEntity;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.Success;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
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
  protected static final Logger log = LoggerFactory.getLogger(EntitySource.class);

  // file system for build-in entities
  private static FileSystem jarFileSystem = null;

  // convenience collectors

  protected static <T extends UniqueEntity> Collector<T, ?, Map<UUID, T>> toMap() {
    return Collectors.toMap(UniqueEntity::getUuid, Function.identity());
  }

  protected static <T extends Entity> Collector<T, ?, Set<T>> toSet() {
    return Collectors.toSet();
  }

  protected EntitySource() {}

  /**
   * Method for validating a given {@link EntitySource}.
   *
   * @throws ValidationException - if an error occurred while validating the source
   */
  public abstract void validate() throws ValidationException;

  @SafeVarargs
  protected static <C extends Entity> void validate(
      DataSource dataSource, Class<? extends C>... entityClasses) throws FailedValidationException {
    Try.scanStream(
            Stream.of(entityClasses)
                .map(clazz -> validate(clazz, dataSource, new SourceValidator<>())),
            "Validation",
            FailedValidationException::new)
        .getOrThrow();
  }

  /**
   * Method for validating a single source.
   *
   * @param entityClass class to be validated
   * @param dataSource source for the fields
   * @param validator used to validate
   * @param <C> type of the class
   */
  protected static <C extends Entity> Try<Void, ValidationException> validate(
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
                    .orElse(Success.empty()));
  }

  /**
   * Method to get a source for the build in entities.
   *
   * @param subdirectory from the resource folder
   * @return a new {@link CsvDataSource}
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
   * Universal method to get a {@link Entity} stream.
   *
   * @param entityClass class of the entity
   * @param dataSource source for the entity
   * @param buildFunction function to build the given entity data
   * @return a set of {@link Entity}s
   * @param <E> type of entity
   * @throws SourceException - if an error happen during reading
   */
  protected static <E extends Entity> Stream<E> getEntities(
      Class<E> entityClass, DataSource dataSource, BuildFunction<E> buildFunction)
      throws SourceException {
    return unpack(buildEntity(entityClass, dataSource, buildFunction), entityClass);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  /**
   * Returns a stream of {@link EntityData} that can be used to build instances of several subtypes
   * of {@link Entity} by a corresponding that consumes this data.
   *
   * @param entityClass the entity class that should be build
   * @param dataSource source for the data
   * @return a stream of the entity data wrapped in a {@link Try}
   */
  protected static Stream<Try<EntityData, SourceException>> buildEntity(
      Class<? extends Entity> entityClass, DataSource dataSource) throws SourceException {
    return dataSource
        .getSourceData(entityClass)
        .map(fieldsToAttributes -> new Success<>(new EntityData(fieldsToAttributes, entityClass)));
  }

  protected static <E extends Entity> Stream<Try<E, SourceException>> buildEntity(
      Class<E> entityClass, DataSource dataSource, BuildFunction<E> fcn) throws SourceException {
    return buildEntity(entityClass, dataSource).map(fcn);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  protected static BuildFunction<UniqueEntity> uniqueEntityBuilder =
      entityData ->
          entityData
              .flatMap(data -> Try.of(() -> data.getUUID(UUID_FIELD_NAME), SourceException.class))
              .map(uuid -> (UniqueEntity) new UniqueInputEntity(uuid) {})
              .transformF(
                  exception ->
                      new SourceException("Could not build UniqueEntity due to: ", exception));

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  /**
   * Method to unpack a stream of tries.
   *
   * @param inputStream given stream
   * @param className name of the class of the entity
   * @return a stream of entities
   * @param <S> type of entity
   * @param <E> type of exception
   * @throws SourceException - if an error occurred during reading
   */
  protected static <S, E extends Exception> Stream<S> unpack(
      Stream<Try<S, E>> inputStream, String className) throws SourceException {
    return Try.scanStream(inputStream, className, SourceException::new).getOrThrow();
  }

  /**
   * Method to unpack a stream of tries.
   *
   * @param inputStream given stream
   * @param clazz class of the entity
   * @return a stream of entities
   * @param <S> type of entity
   * @param <E> type of exception
   * @throws SourceException - if an error occurred during reading
   */
  protected static <S, E extends Exception> Stream<S> unpack(
      Stream<Try<S, E>> inputStream, Class<S> clazz) throws SourceException {
    return Try.scanStream(inputStream, clazz.getSimpleName(), SourceException::new).getOrThrow();
  }

  protected static <T> T extractWithDefault(
      EntityData data, String fieldName, Map<UUID, T> entities, T defaultEntity) {
    return extractEntity(data, fieldName, entities).getOrElse(() -> defaultEntity);
  }

  protected static <T> T extractFunction(EntityData data, String fieldName, Map<UUID, T> entities)
      throws SourceException {
    return extractEntity(data, fieldName, entities).getOrThrow();
  }

  /**
   * Method to extract an entity.
   *
   * @param fieldName name of the field
   * @param entities map: uuid to {@link Entity}
   * @return an enrichment
   * @param <R> type of entity
   */
  protected static <R> Try<R, SourceException> extractEntity(
      EntityData data, String fieldName, Map<UUID, R> entities) {
    return Try.of(() -> data.getUUID(fieldName), SourceException.class)
        .flatMap(entityUuid -> extractEntity(entityUuid, entities))
        .transformF(
            exception ->
                new SourceException(
                    "Extracting UUID for field '"
                        + fieldName
                        + "' failed. Caused by: "
                        + exception.getMessage()));
  }

  /**
   * Method to extract an {@link Entity} from a given map.
   *
   * @param uuid of the entity
   * @param entityMap map: uuid to entity
   * @return a try of the {@link Entity}
   * @param <T> type of entity
   */
  protected static <T> Try<T, SourceException> extractEntity(UUID uuid, Map<UUID, T> entityMap) {
    // We either find a matching entity for given UUID, thus return a success
    // ... or find no matching entity, returning a failure.
    return Try.from(
        Optional.ofNullable(entityMap.get(uuid)),
        () -> new SourceException("Entity with uuid " + uuid + " was not provided."));
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  // functional interfaces

  @FunctionalInterface
  protected interface BuildFunction<R>
      extends Function<Try<EntityData, SourceException>, Try<R, SourceException>> {

    default <U> BuildFunction<U> with(
        Try.TryFunction<Pair<EntityData, R>, U, SourceException> function) {
      return (Try<EntityData, SourceException> t) ->
          t.zip(apply(t)).map(function, SourceException.class);
    }
  }
}
