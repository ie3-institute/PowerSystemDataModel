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
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.*;
import java.util.*;
import java.util.function.Function;
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

  protected final DataSource dataSource;

  protected EntitySource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  protected static String buildSkippingMessage(
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
  protected static String safeMapGet(Map<String, String> map, String key, String mapName) {
    return Optional.ofNullable(map.get(key))
        .orElse(
            "Key '"
                + key
                + "' not found"
                + (mapName.isEmpty() ? "!" : " in map '" + mapName + "'!"));
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
  protected static <T extends AssetTypeInput> Try<T, SourceException> getAssetType(
      Map<UUID, T> types, Map<String, String> fieldsToAttributes, String skippedClassString) {

    Optional<T> assetType =
        Optional.ofNullable(fieldsToAttributes.get(TYPE))
            .flatMap(typeUuid -> Optional.ofNullable(types.get(UUID.fromString(typeUuid))));

    // if the type is not present we return a failure
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

  // TODO
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
          ConnectorInputEntityData untypedEntityData, Map<UUID, T> availableTypes) {
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
      Map<UUID, OperatorInput> operators,
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
      return Optional.ofNullable(operators.get(operatorUuid.get()))
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
      Stream<AssetInputEntityData> assetInputEntityDataStream, Map<UUID, NodeInput> nodes) {
    return assetInputEntityDataStream
        .parallel()
        .map(
            assetInputEntityData -> {
              // get the raw data
              Map<String, String> fieldsToAttributes = assetInputEntityData.getFieldsToValues();
              // get the node of the entity
              UUID nodeUuid = UUID.fromString(fieldsToAttributes.get(NODE));
              Optional<NodeInput> node = Optional.ofNullable(nodes.get(nodeUuid));

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

              return new Success<>(new NodeAssetInputEntityData(assetInputEntityData, node.get()));
            });
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

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
  public <T extends AssetInput> Set<Try<T, FactoryException>> buildNodeAssetEntities(
      Class<T> entityClass,
      EntityFactory<T, NodeAssetInputEntityData> factory,
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes) {
    return nodeAssetEntityStream(entityClass, factory, operators, nodes)
        .collect(Collectors.toSet());
  }

  protected <T extends AssetInput> Stream<Try<T, FactoryException>> nodeAssetEntityStream(
      Class<T> entityClass,
      EntityFactory<T, NodeAssetInputEntityData> factory,
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes) {
    return nodeAssetInputEntityDataStream(assetInputEntityDataStream(entityClass, operators), nodes)
        .map(factory::get);
  }

  public <T extends AssetInput> Set<Try<T, FactoryException>> buildAssetInputEntities(
      Class<T> entityClass,
      EntityFactory<T, AssetInputEntityData> factory,
      Map<UUID, OperatorInput> operators) {
    return assetInputEntityStream(entityClass, factory, operators).collect(Collectors.toSet());
  }

  protected <T extends AssetInput> Stream<Try<T, FactoryException>> assetInputEntityStream(
      Class<T> entityClass,
      EntityFactory<T, AssetInputEntityData> factory,
      Map<UUID, OperatorInput> operators) {
    return assetInputEntityDataStream(entityClass, operators).map(factory::get);
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
      Class<T> entityClass, Map<UUID, OperatorInput> operators) {
    return dataSource
        .getSourceData(entityClass)
        .map(
            fieldsToAttributes ->
                createAssetInputEntityData(entityClass, fieldsToAttributes, operators));
  }

  protected <T extends AssetInput> AssetInputEntityData createAssetInputEntityData(
      Class<T> entityClass,
      Map<String, String> fieldsToAttributes,
      Map<UUID, OperatorInput> operators) {

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
   * @param <T> Type of the {@link UniqueEntity} to expect
   * @return stream of {@link SimpleEntityData}
   */
  protected <T extends UniqueEntity> Stream<SimpleEntityData> simpleEntityDataStream(
      Class<T> entityClass) {
    return dataSource
        .getSourceData(entityClass)
        .map(fieldsToAttributes -> new SimpleEntityData(fieldsToAttributes, entityClass));
  }

  protected static <S extends UniqueEntity> Map<UUID, S> unpackMap(
      Stream<Try<S, FactoryException>> inputStream, Class<S> entityClass) throws SourceException {
    return unpack(inputStream, entityClass)
        .collect(Collectors.toMap(UniqueEntity::getUuid, Function.identity()));
  }

  protected static <S extends UniqueEntity> Set<S> unpackSet(
      Stream<Try<S, FactoryException>> inputStream, Class<S> entityClass) throws SourceException {
    return unpack(inputStream, entityClass).collect(Collectors.toSet());
  }

  private static <S> Stream<S> unpack(
      Stream<Try<S, FactoryException>> inputStream, Class<S> entityClass) throws SourceException {
    return Try.scanStream(inputStream, entityClass.getSimpleName())
        .transformF(SourceException::new)
        .getOrThrow();
  }
}
