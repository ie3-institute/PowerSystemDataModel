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
import edu.ie3.datamodel.utils.Try;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base class for all entity sources. This class provides some functionalities that are common among
 * sources.
 */
public abstract class EntitySource {

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
   * Returns a stream of optional {@link EntityData} that can be used to build instances of several
   * subtypes of {@link Entity} by a corresponding {@link EntityFactory} that consumes this data.
   *
   * @param entityClass the entity class that should be build
   * @param dataSource source for the data
   * @return a stream of the entity data wrapped in a {@link Try}
   */
  protected Stream<Try<EntityData, SourceException>> buildEntityData(
      Class<? extends Entity> entityClass, DataSource dataSource) {
    return Try.of(() -> dataSource.getSourceData(entityClass), SourceException.class)
        .convert(
            data ->
                data.map(
                    fieldsToAttributes ->
                        new Try.Success<>(new EntityData(fieldsToAttributes, entityClass))),
            exception -> Stream.of(Try.Failure.of(exception)));
  }

  protected static <S extends UniqueEntity> Map<UUID, S> unpackMap(
      Stream<Try<S, FactoryException>> inputStream, Class<S> entityClass) throws SourceException {
    return unpack(inputStream, entityClass)
        .collect(Collectors.toMap(UniqueEntity::getUuid, Function.identity()));
  }

  protected static <S extends Entity> Set<S> unpackSet(
      Stream<Try<S, FactoryException>> inputStream, Class<S> entityClass) throws SourceException {
    return unpack(inputStream, entityClass).collect(Collectors.toSet());
  }

  protected static <S, E extends Exception> Stream<S> unpack(
      Stream<Try<S, E>> inputStream, Class<S> clazz) throws SourceException {
    return Try.scanStream(inputStream, clazz.getSimpleName())
        .transformF(SourceException::new)
        .getOrThrow();
  }
}
