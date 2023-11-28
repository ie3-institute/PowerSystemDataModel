/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.ConnectorInputEntityData;
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.TypedConnectorInputEntityData;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Class that provides all functionalities to build entities */
public abstract class EntitySource {

  protected static final Logger log = LoggerFactory.getLogger(EntitySource.class);

  // field names
  protected static final String OPERATOR = "operator";
  protected static final String NODE = "node";
  protected static final String TYPE = "type";
  protected static final String FIELDS_TO_VALUES_MAP = "fieldsToValuesMap";

  DataSource dataSource;

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  protected String buildSkippingMessage(
      String entityDesc, String entityUuid, String entityId, String missingElementsString) {
    return "Skipping "
        + entityDesc
        + " with uuid "
        + entityUuid
        + " and id "
        + entityId
        + ". Not all required entities found or map is missing entity key!\nMissing elements:\n"
        + missingElementsString;
  }

  /**
   * Method for retrieving an element from a map. If the map doesn't contain the key an error
   * message is build and returned instead.
   *
   * <p>Should not be used for other purposes than creating error messages.
   *
   * @param map with value
   * @param key for the value
   * @param mapName name of the map used for the error message
   * @return either the value or an error message
   */
  protected String safeMapGet(Map<String, String> map, String key, String mapName) {
    return Optional.ofNullable(map.get(key))
        .orElse(
            "Key '"
                + key
                + "' not found"
                + (mapName.isEmpty() ? "!" : " in map '" + mapName + "'!"));
  }

  /**
   * Returns an {@link Optional} of the first {@link UniqueEntity} element of this collection
   * matching the provided UUID or an empty {@code Optional} if no matching entity can be found.
   *
   * @param entityUuid uuid of the entity that should be looked for
   * @param entities collection of entities that should be
   * @param <T> type of the entity that will be returned, derived from the provided collection
   * @return either an optional containing the first entity that has the provided uuid or an empty
   *     optional if no matching entity with the provided uuid can be found
   */
  protected <T extends UniqueEntity> Optional<T> findFirstEntityByUuid(
      UUID entityUuid, Collection<T> entities) {
    return entities.stream()
        .parallel()
        .filter(uniqueEntity -> uniqueEntity.getUuid().equals(entityUuid))
        .findFirst();
  }

  /**
   * Checks if the requested type of asset can be found in the provided collection of types based on
   * the provided fields to values mapping. The provided fields to values mapping needs to have one
   * and only one field with key {@link #TYPE} and a corresponding UUID value. If the type can be
   * found in the provided collection based on the UUID it is returned wrapped in a {@link Success}.
   * Otherwise, a {@link Failure} is returned and a warning is logged.
   *
   * @param types a collection of types that should be used for searching
   * @param fieldsToAttributes the field name to value mapping incl. the key {@link #TYPE}
   * @param skippedClassString debug string of the class that will be skipping
   * @param <T> the type of the resulting type instance
   * @return a {@link Success} containing the type or a {@link Failure} if the type cannot be found
   */
  protected <T extends AssetTypeInput> Try<T, SourceException> getAssetType(
      Collection<T> types, Map<String, String> fieldsToAttributes, String skippedClassString) {

    Optional<T> assetType =
        Optional.ofNullable(fieldsToAttributes.get(TYPE))
            .flatMap(typeUuid -> findFirstEntityByUuid(UUID.fromString(typeUuid), types));

    // if the type is not present we return an empty element and
    // log a warning
    if (assetType.isEmpty()) {
      String skippingMessage =
          buildSkippingMessage(
              skippedClassString,
              safeMapGet(fieldsToAttributes, "uuid", FIELDS_TO_VALUES_MAP),
              safeMapGet(fieldsToAttributes, "id", FIELDS_TO_VALUES_MAP),
              TYPE + ": " + safeMapGet(fieldsToAttributes, TYPE, FIELDS_TO_VALUES_MAP));
      return new Failure<>(new SourceException("Failure due to: " + skippingMessage));
    }
    return new Success<>(assetType.get());
  }

  /**
   * Finds the required asset type and if present, adds it to the untyped entity data
   *
   * @param untypedEntityData Untyped entity data to enrich
   * @param availableTypes Yet available asset types
   * @param <T> Type of the asset type
   * @return {@link Try} to enhanced data
   */
  protected <T extends AssetTypeInput>
      Try<TypedConnectorInputEntityData<T>, SourceException> findAndAddType(
          ConnectorInputEntityData untypedEntityData, Collection<T> availableTypes) {
    Try<T, SourceException> assetTypeOption =
        getAssetType(
            availableTypes,
            untypedEntityData.getFieldsToValues(),
            untypedEntityData.getClass().getSimpleName());
    return assetTypeOption.map(assetType -> addTypeToEntityData(untypedEntityData, assetType));
  }

  /**
   * Enriches the given, untyped entity data with the provided asset type
   *
   * @param untypedEntityData Untyped entity data to enrich
   * @param assetType Asset type to add
   * @param <T> Type of the asset type
   * @return The enriched entity data
   */
  protected <T extends AssetTypeInput> TypedConnectorInputEntityData<T> addTypeToEntityData(
      ConnectorInputEntityData untypedEntityData, T assetType) {
    Map<String, String> fieldsToAttributes = untypedEntityData.getFieldsToValues();

    // remove fields that are passed as objects to constructor
    fieldsToAttributes.keySet().remove(TYPE);

    // build result object
    return new TypedConnectorInputEntityData<>(
        fieldsToAttributes,
        untypedEntityData.getTargetClass(),
        untypedEntityData.getOperatorInput(),
        untypedEntityData.getNodeA(),
        untypedEntityData.getNodeB(),
        assetType);
  }

  /**
   * Returns either the first instance of a {@link OperatorInput} in the provided collection of or
   * {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   *
   * @param operators the collections of {@link OperatorInput}s that should be searched in
   * @param operatorUuid the operator uuid that is requested
   * @return either the first found instancen of {@link OperatorInput} or {@link
   *     OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  protected OperatorInput getFirstOrDefaultOperator(
      Collection<OperatorInput> operators,
      Optional<UUID> operatorUuid,
      String entityClassName,
      String requestEntityUuid) {
    if (operatorUuid.isEmpty()) {
      log.warn(
          "Input source for class '{}' is missing the 'operator' field. "
              + "This is okay, but you should consider fixing the file by adding the field. "
              + "Defaulting to 'NO OPERATOR ASSIGNED'",
          entityClassName);
      return OperatorInput.NO_OPERATOR_ASSIGNED;
    } else {
      return findFirstEntityByUuid(operatorUuid.get(), operators)
          .orElseGet(
              () -> {
                log.debug(
                    "Cannot find operator with uuid '{}' for element '{}' and uuid '{}'. Defaulting to 'NO OPERATOR ASSIGNED'.",
                    operatorUuid,
                    entityClassName,
                    requestEntityUuid);
                return OperatorInput.NO_OPERATOR_ASSIGNED;
              });
    }
  }

  /**
   * Returns a stream of tries of {@link NodeAssetInputEntityData} that can be used to build
   * instances of several subtypes of {@link UniqueEntity} by a corresponding {@link EntityFactory}
   * that consumes this data. param assetInputEntityDataStream
   *
   * @param assetInputEntityDataStream a stream consisting of {@link AssetInputEntityData} that is
   *     enriched with {@link NodeInput} data
   * @param nodes a collection of {@link NodeInput} entities that should be used to build the data
   * @return stream of the entity data wrapped in a {@link Try}
   */
  protected Stream<Try<NodeAssetInputEntityData, SourceException>> nodeAssetInputEntityDataStream(
      Stream<AssetInputEntityData> assetInputEntityDataStream, Collection<NodeInput> nodes) {
    return assetInputEntityDataStream
        .parallel()
        .map(
            assetInputEntityData -> {
              // get the raw data
              Map<String, String> fieldsToAttributes = assetInputEntityData.getFieldsToValues();
              // get the node of the entity
              UUID nodeUuid = UUID.fromString(fieldsToAttributes.get(NODE));
              Optional<NodeInput> node = findFirstEntityByUuid(nodeUuid, nodes);

              // if the node is not present we return an empty element and
              // log a warning
              if (node.isEmpty()) {
                String skippingMessage =
                    buildSkippingMessage(
                        assetInputEntityData.getTargetClass().getSimpleName(),
                        fieldsToAttributes.get("uuid"),
                        fieldsToAttributes.get("id"),
                        NODE + ": " + nodeUuid);
                return new Failure<>(new SourceException("Failure due to: " + skippingMessage));
              }

              // remove fields that are passed as objects to constructor
              fieldsToAttributes.keySet().remove(NODE);

              return new Success<>(
                  new NodeAssetInputEntityData(
                      fieldsToAttributes,
                      assetInputEntityData.getTargetClass(),
                      assetInputEntityData.getOperatorInput(),
                      node.get()));
            });
  }

  /**
   * Returns a stream of optional {@link AssetInputEntityData} that can be used to build instances
   * of several subtypes of {@link UniqueEntity} by a corresponding {@link EntityFactory} that
   * consumes this data.
   *
   * @param entityClass the entity class that should be build
   * @param operators a collection of {@link OperatorInput} entities that should be used to build
   *     the data
   * @param <T> type of the entity that should be build
   * @return stream of the entity data wrapped in a {@link Try}
   */
  protected <T extends AssetInput> Stream<AssetInputEntityData> assetInputEntityDataStream(
      Class<T> entityClass, Collection<OperatorInput> operators) {
    return dataSource
        .getSourceData(entityClass)
        .map(
            fieldsToAttributes ->
                assetInputEntityDataStream(entityClass, fieldsToAttributes, operators));
  }

  protected <T extends AssetInput> AssetInputEntityData assetInputEntityDataStream(
      Class<T> entityClass,
      Map<String, String> fieldsToAttributes,
      Collection<OperatorInput> operators) {

    // get the operator of the entity
    Optional<UUID> operatorUuid =
        Optional.ofNullable(fieldsToAttributes.get(OPERATOR))
            .filter(s -> !s.isBlank())
            .map(UUID::fromString);
    OperatorInput operator =
        getFirstOrDefaultOperator(
            operators,
            operatorUuid,
            entityClass.getSimpleName(),
            safeMapGet(fieldsToAttributes, "uuid", FIELDS_TO_VALUES_MAP));

    // remove fields that are passed as objects to constructor
    fieldsToAttributes.keySet().removeAll(new HashSet<>(Collections.singletonList(OPERATOR)));

    return new AssetInputEntityData(fieldsToAttributes, entityClass, operator);
  }

  /**
   * Returns a stream of {@link SimpleEntityData} for result entity classes, using a
   * fields-to-attributes map.
   *
   * @param entityClass the entity class that should be build
   * @param <T> Type of the {@link ResultEntity} to expect
   * @return stream of {@link SimpleEntityData}
   */
  protected <T extends ResultEntity> Stream<SimpleEntityData> simpleEntityDataStream(
      Class<T> entityClass) {
    return dataSource
        .getSourceData(entityClass)
        .map(fieldsToAttributes -> new SimpleEntityData(fieldsToAttributes, entityClass));
  }

  protected <T extends AssetInput> Stream<Try<T, FactoryException>> assetInputEntityStream(
      Class<T> entityClass,
      EntityFactory<T, AssetInputEntityData> factory,
      Collection<OperatorInput> operators) {
    return assetInputEntityDataStream(entityClass, operators).map(factory::get);
  }

  /**
   * Returns a stream of {@link Try} entities that can be build by using {@link
   * NodeAssetInputEntityData} and their corresponding factory.
   *
   * @param entityClass the entity class that should be build
   * @param factory the factory that should be used for the building process
   * @param nodes a collection of {@link NodeInput} entities that should be used to build the
   *     entities
   * @param operators a collection of {@link OperatorInput} entities should be used to build the
   *     entities
   * @param <T> Type of the {@link AssetInput} to expect
   * @return stream of tries of the entities that has been built by the factory
   */
  protected <T extends AssetInput> Stream<Try<T, FactoryException>> nodeAssetEntityStream(
      Class<T> entityClass,
      EntityFactory<T, NodeAssetInputEntityData> factory,
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators) {
    return nodeAssetInputEntityDataStream(assetInputEntityDataStream(entityClass, operators), nodes)
        .map(factory::get);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  public <T extends AssetInput> Set<Try<T, FactoryException>> buildNodeAssetEntities(
      Class<T> entityClass,
      EntityFactory<T, NodeAssetInputEntityData> factory,
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators) {
    return nodeAssetEntityStream(entityClass, factory, nodes, operators)
        .collect(Collectors.toSet());
  }

  public <T extends AssetInput> Set<Try<T, FactoryException>> buildAssetInputEntities(
      Class<T> entityClass,
      EntityFactory<T, AssetInputEntityData> factory,
      Collection<OperatorInput> operators) {
    return assetInputEntityStream(entityClass, factory, operators).collect(Collectors.toSet());
  }

  @SuppressWarnings("unchecked")
  public <T extends InputEntity> Set<Try<T, FactoryException>> buildEntities(
      Class<T> entityClass, EntityFactory<? extends InputEntity, SimpleEntityData> factory) {
    return dataSource
        .getSourceData(entityClass)
        .map(
            fieldsToAttributes -> {
              SimpleEntityData data = new SimpleEntityData(fieldsToAttributes, entityClass);
              return (Try<T, FactoryException>) factory.get(data);
            })
        .collect(Collectors.toSet());
  }
}
