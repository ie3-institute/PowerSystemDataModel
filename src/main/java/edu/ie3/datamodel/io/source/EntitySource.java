/*
 * © 2023. TU Dortmund University,
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
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.function.TriFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Class that provides all functionalities to build entities */
public abstract class EntitySource {

  protected static final Logger log = LoggerFactory.getLogger(EntitySource.class);

  // field names
  protected static final String OPERATOR = "operator";
  protected static final String NODE = "node";
  protected static final String TYPE = "type";

  protected final DataSource dataSource;

  protected EntitySource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  public abstract void validate() throws ValidationException;

  /**
   * Method for validating a single source.
   *
   * @param entityClass class to be validated
   * @param validator used to validate
   * @param <C> type of the class
   */
  protected final <C extends Entity> Try<Void, ValidationException> validate(
      Class<? extends C> entityClass, SourceValidator<C> validator) {
    return Try.of(() -> dataSource.getSourceFields(entityClass), SourceException.class)
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
   * Enhances given entity data with an entity from the given entity map. The linked entity is
   * chosen by taking into account the UUID found by retrieving the field with given fieldName from
   * entityData.
   *
   * @param entityData The entity data to be enhanced, which also provides a link to another entity
   *     via UUID
   * @param fieldName The field name of the field that provides the UUID of the linked entity
   * @param linkedEntities A map of UUID to entities, of which one should be linked to given entity
   *     data
   * @param createEntityData The function that creates the resulting entity data given entityData
   *     and the linked entity
   * @param <E> Type of input entity data
   * @param <T> Type of the linked entity
   * @param <R> Type of resulting entity data that combines the given entityData and linked entity
   * @return {@link Try} to enhanced data
   */
  protected static <E extends EntityData, T extends Entity, R extends E>
      Try<R, SourceException> enrichEntityData(
          E entityData,
          String fieldName,
          Map<UUID, T> linkedEntities,
          BiFunction<E, T, R> createEntityData) {
    return getLinkedEntity(entityData, fieldName, linkedEntities)
        .map(
            linkedEntity -> {
              Map<String, String> fieldsToAttributes = entityData.getFieldsToValues();

              // remove fields that are passed as objects to constructor
              fieldsToAttributes.keySet().remove(fieldName);

              // build resulting entity data
              return createEntityData.apply(entityData, linkedEntity);
            });
  }

  /**
   * Enhances given entity data with two entities from the given entity maps. The linked entities
   * are chosen by taking into account the UUIDs found by retrieving the fields with given
   * fieldName1 and fieldName2 from entityData.
   *
   * @param entityData The entity data to be enhanced, which also provides links to two other
   *     entities via UUID
   * @param fieldName1 The field name of the field that provides the UUID of the first linked entity
   * @param linkedEntities1 The first map of UUID to entities, of which one should be linked to
   *     given entity data
   * @param fieldName2 The field name of the field that provides the UUID of the second linked
   *     entity
   * @param linkedEntities2 The second map of UUID to entities, of which one should be linked to
   *     given entity data
   * @param createEntityData The function that creates the resulting entity data given entityData
   *     and the linked entities
   * @param <E> Type of input entity data
   * @param <T1> Type of the first linked entity
   * @param <T2> Type of the second linked entity
   * @param <R> Type of resulting entity data that combines the given entityData and two linked
   *     entities
   * @return {@link Try} to enhanced data
   */
  protected static <
          E extends EntityData, T1 extends UniqueEntity, T2 extends UniqueEntity, R extends E>
      Try<R, SourceException> enrichEntityData(
          E entityData,
          String fieldName1,
          Map<UUID, T1> linkedEntities1,
          String fieldName2,
          Map<UUID, T2> linkedEntities2,
          TriFunction<E, T1, T2, R> createEntityData) {
    return getLinkedEntity(entityData, fieldName1, linkedEntities1)
        .flatMap(
            linkedEntity1 ->
                getLinkedEntity(entityData, fieldName2, linkedEntities2)
                    .map(
                        linkedEntity2 -> {
                          Map<String, String> fieldsToAttributes = entityData.getFieldsToValues();

                          // remove fields that are passed as objects to constructor
                          fieldsToAttributes.keySet().remove(fieldName1);
                          fieldsToAttributes.keySet().remove(fieldName2);

                          // build resulting entity data
                          return createEntityData.apply(entityData, linkedEntity1, linkedEntity2);
                        }));
  }

  /**
   * Checks if the linked entity can be found in the provided map of entities. The linked entities
   * are chosen by taking into account the UUIDs found by retrieving the fields with given
   * fieldName1 and fieldName2 from entityData.
   *
   * @param entityData The entity data of the entity that provides a link to another entity via UUID
   * @param fieldName The field name of the field that provides the UUID of the linked entity
   * @param linkedEntities A map of UUID to entities, of which one should be linked to given entity
   *     data
   * @param <T> the type of the resulting linked entity instance
   * @return a {@link Success} containing the entity or a {@link Failure} if the entity cannot be
   *     found
   */
  protected static <T extends Entity> Try<T, SourceException> getLinkedEntity(
      EntityData entityData, String fieldName, Map<UUID, T> linkedEntities) {

    return Try.of(() -> entityData.getUUID(fieldName), FactoryException.class)
        .transformF(
            exception ->
                new SourceException(
                    "Extracting UUID field "
                        + fieldName
                        + " from entity data "
                        + entityData.toString()
                        + " failed.",
                    exception))
        .flatMap(
            entityUuid ->
                getEntity(entityUuid, linkedEntities)
                    .transformF(
                        exception ->
                            new SourceException(
                                "Linked "
                                    + fieldName
                                    + " with UUID "
                                    + entityUuid
                                    + " was not found for entity "
                                    + entityData,
                                exception)));
  }

  /**
   * Enhances given entity data with an entity from the given entity map or the default value. The
   * linked entity is possibly chosen by taking into account the UUID found by retrieving the field
   * with given fieldName from entityData. If no entity is linked, the default value is used.
   *
   * @param entityData The entity data to be enhanced, which also might provide a link to another
   *     entity via UUID
   * @param fieldName The field name of the field that might provide the UUID of the linked entity
   * @param linkedEntities A map of UUID to entities, of which one should be linked to given entity
   *     data
   * @param defaultEntity The default linked entity to use, if no actual linked entity could be
   *     found
   * @param createEntityData The function that creates the resulting entity data given entityData
   *     and the linked entity (either retrieved from the map or the standard entity)
   * @param <E> Type of input entity data
   * @param <T> Type of the linked entity
   * @param <R> Type of resulting entity data that combines the given entityData and linked entity
   * @return {@link Try} to enhanced data
   */
  protected static <E extends EntityData, T extends UniqueEntity, R extends E>
      Try<R, SourceException> optionallyEnrichEntityData(
          E entityData,
          String fieldName,
          Map<UUID, T> linkedEntities,
          T defaultEntity,
          BiFunction<E, T, R> createEntityData) {
    return entityData
        .getFieldOptional(fieldName)
        .filter(s -> !s.isBlank())
        .map(
            // Entity data includes a non-empty UUID String for the desired entity
            uuidString ->
                Try.of(() -> UUID.fromString(uuidString), IllegalArgumentException.class)
                    .transformF(
                        iae ->
                            // Parsing error still results in a failure, ...
                            new SourceException(
                                String.format(
                                    "Exception while trying to parse UUID of field \"%s\" with value \"%s\"",
                                    fieldName, uuidString),
                                iae))
                    .flatMap(
                        entityUuid ->
                            getEntity(entityUuid, linkedEntities)
                                // ... as well as a provided entity UUID that does not match any
                                // given data
                                .transformF(
                                    exception ->
                                        new SourceException(
                                            "Linked "
                                                + fieldName
                                                + " with UUID "
                                                + entityUuid
                                                + " was not found for entity "
                                                + entityData,
                                            exception))))
        .orElseGet(
            () -> {
              // No UUID was given (column does not exist, or field is empty).
              // This is totally fine - we successfully return the default value
              log.debug(
                  "Input source for class {} is missing the '{}' field. "
                      + "Default value '{}' is used.",
                  entityData.getTargetClass().getSimpleName(),
                  fieldName,
                  defaultEntity);
              return new Try.Success<>(defaultEntity);
            })
        .map(
            linkedEntity -> {
              Map<String, String> fieldsToAttributes = entityData.getFieldsToValues();

              // remove fields that are passed as objects to constructor
              fieldsToAttributes.keySet().remove(fieldName);

              // build resulting entity data
              return createEntityData.apply(entityData, linkedEntity);
            });
  }

  private static <T> Try<T, SourceException> getEntity(UUID uuid, Map<UUID, T> entityMap) {
    return Optional.ofNullable(entityMap.get(uuid))
        // We either find a matching entity for given UUID, thus return a success
        .map(entity -> Try.of(() -> entity, SourceException.class))
        // ... or find no matching entity, returning a failure.
        .orElse(
            new Try.Failure<>(
                new SourceException("Entity with uuid " + uuid + " was not provided.")));
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
   * Returns a stream of {@link Try} entities that can be built by using {@link
   * NodeAssetInputEntityData} and their corresponding factory.
   *
   * @param entityClass the entity class that should be build
   * @param nodes a map of UUID to {@link NodeInput} entities that should be used to build the
   *     entities
   * @param operators a map of UUID to {@link OperatorInput} entities should be used to build the
   *     entities
   * @return stream of tries of the entities that has been built by the factory
   */
  protected Stream<Try<NodeAssetInputEntityData, SourceException>> buildNodeAssetEntityData(
      Class<? extends AssetInput> entityClass,
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes) {
    return nodeAssetInputEntityDataStream(buildAssetInputEntityData(entityClass, operators), nodes);
  }

  /**
   * Returns a stream of tries of {@link NodeAssetInputEntityData} that can be used to build
   * instances of several subtypes of {@link UniqueEntity} by a corresponding {@link EntityFactory}
   * that consumes this data.
   *
   * @param assetInputEntityDataStream a stream consisting of {@link AssetInputEntityData} that is
   *     enriched with {@link NodeInput} data
   * @param nodes a map of UUID to {@link NodeInput} entities that should be used to build the data
   * @return stream of the entity data wrapped in a {@link Try}
   */
  protected static Stream<Try<NodeAssetInputEntityData, SourceException>>
      nodeAssetInputEntityDataStream(
          Stream<Try<AssetInputEntityData, SourceException>> assetInputEntityDataStream,
          Map<UUID, NodeInput> nodes) {
    return assetInputEntityDataStream
        .parallel()
        .map(
            assetInputEntityDataTry ->
                assetInputEntityDataTry.flatMap(
                    assetInputEntityData ->
                        enrichEntityData(
                            assetInputEntityData, NODE, nodes, NodeAssetInputEntityData::new)));
  }

  /**
   * Returns a stream of optional {@link AssetInputEntityData} that can be used to build instances
   * of several subtypes of {@link UniqueEntity} by a corresponding {@link EntityFactory} that
   * consumes this data.
   *
   * @param entityClass the entity class that should be build
   * @param operators a map of UUID to {@link OperatorInput} entities that should be used to build
   *     the data
   * @return stream of the entity data wrapped in a {@link Try}
   */
  protected Stream<Try<AssetInputEntityData, SourceException>> buildAssetInputEntityData(
      Class<? extends AssetInput> entityClass, Map<UUID, OperatorInput> operators) {
    return assetInputEntityDataStream(buildEntityData(entityClass), operators);
  }

  /**
   * Returns a stream of tries of {@link AssetInputEntityData} that can be used to build instances
   * of several subtypes of {@link UniqueEntity} by a corresponding {@link EntityFactory} that
   * consumes this data.
   *
   * @param entityDataStream a stream consisting of {@link EntityData} that is enriched with {@link
   *     OperatorInput} data
   * @param operators map of UUID to {@link OperatorInput} entities that should be used to build the
   *     data
   * @return stream of the entity data wrapped in a {@link Try}
   */
  protected static Stream<Try<AssetInputEntityData, SourceException>> assetInputEntityDataStream(
      Stream<Try<EntityData, SourceException>> entityDataStream,
      Map<UUID, OperatorInput> operators) {
    return entityDataStream
        .parallel()
        .map(
            entityDataTry ->
                entityDataTry.flatMap(
                    entityData ->
                        optionallyEnrichEntityData(
                            entityData,
                            OPERATOR,
                            operators,
                            OperatorInput.NO_OPERATOR_ASSIGNED,
                            AssetInputEntityData::new)));
  }

  /**
   * Returns a stream of optional {@link EntityData} that can be used to build instances of several
   * subtypes of {@link UniqueEntity} by a corresponding {@link EntityFactory} that consumes this
   * data.
   *
   * @param entityClass the entity class that should be build
   * @return stream of the entity data wrapped in a {@link Try}
   */
  protected Stream<Try<EntityData, SourceException>> buildEntityData(
      Class<? extends Entity> entityClass) {
    return dataSource
        .getSourceData(entityClass)
        .map(fieldsToAttributes -> new Success<>(new EntityData(fieldsToAttributes, entityClass)));
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
