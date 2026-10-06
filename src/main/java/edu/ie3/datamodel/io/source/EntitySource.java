/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.FailedValidationException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.csv.CsvDataSource;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.utils.Try;
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

  /**
   * Method to validate the given entity classes.
   *
   * @param dataSource data source to use for the validation
   * @param classes to validate
   */
  @SafeVarargs
  protected static void validate(DataSource dataSource, Class<? extends Entity>... classes)
      throws FailedValidationException {
    Try.scanStream(
            Arrays.stream(classes).map(c -> validate(c, dataSource)),
            "Void",
            FailedValidationException::new)
        .getOrThrow();
  }

  /**
   * Method for validating a single source.
   *
   * @param entityClass class to be validated
   * @param dataSource source for the fields
   * @param <C> type of the class
   */
  protected static <C extends Entity> Try<Void, ValidationException> validate(
      Class<? extends C> entityClass, DataSource dataSource) {
    return validate(entityClass, () -> dataSource.getSourceFields(entityClass));
  }

  /**
   * Method for validating a single source.
   *
   * @param clazz class to be validated
   * @param sourceFields supplier for source fields
   * @param <C> type of the class
   */
  protected static <C> Try<Void, ValidationException> validate(
      Class<? extends C> clazz,
      Try.TrySupplier<Optional<Set<String>>, SourceException> sourceFields) {
    return Try.of(sourceFields, SourceException.class)
        .transformF(
            se ->
                (ValidationException)
                    new FailedValidationException(
                        "Validation for class "
                            + clazz
                            + " failed because of an error related to its source.",
                        se))
        .flatMap(
            fieldsOpt ->
                fieldsOpt
                    .map(fields -> DataSource.validate(fields, clazz))
                    .orElse(Try.Success.empty()));
  }

  /**
   * Method to get a source for the build in entities.
   *
   * @param clazz class used to access the resources
   * @param subdirectory from the resource folder
   * @return a new {@link CsvDataSource}
   */
  protected static CsvDataSource getBuildInSource(Class<?> clazz, String subdirectory)
      throws SourceException {
    try {
      URL url = clazz.getResource(subdirectory);

      if (url == null) {
        String message = "Resources not found for: " + subdirectory;
        log.error(message);
        throw new SourceException(message);
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
   * @param entityClass subclass of {@link UniqueEntity}
   * @param dataSource source for the data
   * @param factory to build the entity
   * @return a map: uuid to {@link UniqueEntity}
   * @param <E> type of entity
   * @throws SourceException - if an error happen during reading
   */
  @SuppressWarnings("unchecked")
  protected static <E extends UniqueEntity, R extends UniqueEntity> Map<UUID, R> getEntityMap(
      Class<R> entityClass, DataSource dataSource, EntityFactory<E, R> factory)
      throws SourceException {
    return getEntities(entityClass, dataSource, factory).collect(toMap());
  }

  /**
   * Universal method to get a {@link Entity} stream.
   *
   * @param entityClass class of the entity
   * @param dataSource source for the entity
   * @param factory to build the entity
   * @return a set of {@link Entity}s
   * @param <E> type of base entity
   * @param <R> type of returned entity
   * @throws SourceException - if an error happen during reading
   */
  protected static <E extends Entity, R extends Entity> Stream<R> getEntities(
      Class<R> entityClass, DataSource dataSource, EntityFactory<E, R> factory)
      throws SourceException {
    return unpack(read(entityClass, dataSource).map(factory::get), entityClass);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  /**
   * @param entityClass
   * @param dataSource
   * @return
   * @throws SourceException
   */
  protected static Stream<Try<Map<String, String>, SourceException>> read(
      Class<? extends Entity> entityClass, DataSource dataSource) throws SourceException {
    return dataSource.getSourceData(entityClass).map(Try.Success::new);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

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
}
