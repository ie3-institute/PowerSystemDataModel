package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.ConnectorInputEntityData;
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.TypedConnectorInputEntityData;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.result.ResultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class that provides all functionalities to build entities
 */
public abstract class EntitySource {

    protected static final Logger log = LoggerFactory.getLogger(EntitySource.class);

    // field names
    protected static final String OPERATOR = "operator";
    protected static final String NODE_A = "nodeA";
    protected static final String NODE_B = "nodeB";
    protected static final String NODE = "node";
    protected static final String TYPE = "type";
    protected static final String FIELDS_TO_VALUES_MAP = "fieldsToValuesMap";

    FunctionalDataSource dataSource;

    // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    /**
     * Returns a predicate that can be used to filter optionals of {@link UniqueEntity}s and keep
     * track on the number of elements that have been empty optionals. This filter let only pass
     * optionals that are non-empty. Example usage:
     *
     * <pre>{@code
     * Collection.stream().filter(isPresentCollectIfNot(NodeInput.class, new ConcurrentHashMap<>()))
     * }</pre>
     *
     * @param entityClass entity class that should be used as they key in the provided counter map
     * @param invalidElementsCounterMap a map that counts the number of empty optionals and maps it to
     *     the provided entity clas
     * @param <T> the type of the entity
     * @return a predicate that can be used to filter and count empty optionals
     */
    protected <T extends UniqueEntity> Predicate<Optional<T>> isPresentCollectIfNot(
            Class<? extends UniqueEntity> entityClass,
            ConcurrentHashMap<Class<? extends UniqueEntity>, LongAdder> invalidElementsCounterMap) {
        return o -> {
            if (o.isPresent()) {
                return true;
            } else {
                invalidElementsCounterMap.computeIfAbsent(entityClass, k -> new LongAdder()).increment();
                return false;
            }
        };
    }

    protected void printInvalidElementInformation(
            Class<? extends UniqueEntity> entityClass,
            LongAdder noOfInvalidElements) {
        log.error(
                "{} entities of type '{}' are missing required elements!",
                noOfInvalidElements,
                entityClass.getSimpleName());
    }


    protected void logSkippingWarning(
            String entityDesc, String entityUuid, String entityId, String missingElementsString) {
        log.warn(
                "Skipping '{}' with uuid '{}' and id '{}'. Not all required entities found or map is missing entity key!\nMissing elements:\n{}",
                entityDesc,
                entityUuid,
                entityId,
                missingElementsString);
    }


    protected String saveMapGet(Map<String, String> map, String key, String mapName) {
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
            String entityUuid, Collection<T> entities) {
        return entities.stream()
                .parallel()
                .filter(uniqueEntity -> uniqueEntity.getUuid().toString().equalsIgnoreCase(entityUuid))
                .findFirst();
    }


    /**
     * Checks if the requested type of an asset can be found in the provided collection of types based
     * on the provided fields to values mapping. The provided fields to values mapping needs to have
     * one and only one field with key {@link #TYPE} and a corresponding UUID value. If the type can
     * be found in the provided collection based on the UUID it is returned wrapped in an optional.
     * Otherwise an empty optional is returned and a warning is logged.
     *
     * @param types a collection of types that should be used for searching
     * @param fieldsToAttributes the field name to value mapping incl. the key {@link #TYPE}
     * @param skippedClassString debug string of the class that will be skipping
     * @param <T> the type of the resulting type instance
     * @return either an optional containing the type or an empty optional if the type cannot be found
     */
    protected <T extends AssetTypeInput> Optional<T> getAssetType(
            Collection<T> types, Map<String, String> fieldsToAttributes, String skippedClassString) {

        Optional<T> assetType =
                Optional.ofNullable(fieldsToAttributes.get(TYPE))
                        .flatMap(typeUuid -> findFirstEntityByUuid(typeUuid, types));

        // if the type is not present we return an empty element and
        // log a warning
        if (assetType.isEmpty()) {
            logSkippingWarning(
                    skippedClassString,
                    saveMapGet(fieldsToAttributes, "uuid", FIELDS_TO_VALUES_MAP),
                    saveMapGet(fieldsToAttributes, "id", FIELDS_TO_VALUES_MAP),
                    TYPE + ": " + saveMapGet(fieldsToAttributes, TYPE, FIELDS_TO_VALUES_MAP));
        }
        return assetType;
    }


    /**
     * Finds the required asset type and if present, adds it to the untyped entity data
     *
     * @param untypedEntityData Untyped entity data to enrich
     * @param availableTypes Yet available asset types
     * @param <T> Type of the asset type
     * @return Option to enhanced data
     */
    protected <T extends AssetTypeInput> Optional<TypedConnectorInputEntityData<T>> findAndAddType(
            ConnectorInputEntityData untypedEntityData, Collection<T> availableTypes) {
        Optional<T> assetTypeOption =
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
     * @param operators    the collections of {@link OperatorInput}s that should be searched in
     * @param operatorUuid the operator uuid that is requested
     * @return either the first found instancen of {@link OperatorInput} or {@link
     * OperatorInput#NO_OPERATOR_ASSIGNED}
     */
    protected OperatorInput getFirstOrDefaultOperator(
            Collection<OperatorInput> operators,
            String operatorUuid,
            String entityClassName,
            String requestEntityUuid) {
        if (operatorUuid == null) {
            log.warn(
                    "Input file for class '{}' is missing the 'operator' field. "
                            + "This is okay, but you should consider fixing the file by adding the field. "
                            + "Defaulting to 'NO OPERATOR ASSIGNED'",
                    entityClassName);
            return OperatorInput.NO_OPERATOR_ASSIGNED;
        } else {
            return operatorUuid.trim().isEmpty()
                    ? OperatorInput.NO_OPERATOR_ASSIGNED
                    : findFirstEntityByUuid(operatorUuid, operators)
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
     * Returns a stream of optional {@link NodeAssetInputEntityData} that can be used to build
     * instances of several subtypes of {@link UniqueEntity} by a corresponding {@link EntityFactory}
     * that consumes this data. param assetInputEntityDataStream
     *
     * @param assetInputEntityDataStream a stream consisting of {@link AssetInputEntityData} that is
     *     enriched with {@link NodeInput} data
     * @param nodes a collection of {@link NodeInput} entities that should be used to build the data
     * @return stream of optionals of the entity data or empty optionals of the node required for the
     *     data cannot be found
     */
    protected Stream<Optional<NodeAssetInputEntityData>> nodeAssetInputEntityDataStream(
            Stream<AssetInputEntityData> assetInputEntityDataStream, Collection<NodeInput> nodes) {
        return assetInputEntityDataStream
                .parallel()
                .map(assetInputEntityData -> {
                    // get the raw data
                    Map<String, String> fieldsToAttributes = assetInputEntityData.getFieldsToValues();
                    // get the node of the entity
                    String nodeUuid = fieldsToAttributes.get(NODE);
                    Optional<NodeInput> node = findFirstEntityByUuid(nodeUuid, nodes);

                    // if the node is not present we return an empty element and
                    // log a warning
                    if (node.isEmpty()) {
                        logSkippingWarning(
                        assetInputEntityData.getTargetClass().getSimpleName(),
                        fieldsToAttributes.get("uuid"),
                        fieldsToAttributes.get("id"),
                        NODE + ": " + nodeUuid);
                        return Optional.empty();
                    }

                    // remove fields that are passed as objects to constructor
                    fieldsToAttributes.keySet().remove(NODE);

                    return Optional.of(
                        new NodeAssetInputEntityData(
                        fieldsToAttributes,
                        assetInputEntityData.getTargetClass(),
                        assetInputEntityData.getOperatorInput(),
                        node.get()));});
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
     * @return stream of optionals of the entity data or empty optionals of the operator required for
     *     the data cannot be found
     */
    protected <T extends AssetInput> Stream<AssetInputEntityData> assetInputEntityDataStream(
            Class<T> entityClass,
            Collection<OperatorInput> operators
    ) {
        return dataSource.getSourceData(entityClass)
                .map(
                        fieldsToAttributes ->
                                assetInputEntityDataStream(entityClass, fieldsToAttributes, operators)
                );
    }

    protected <T extends AssetInput> AssetInputEntityData assetInputEntityDataStream(
            Class<T> entityClass,
            Map<String, String> fieldsToAttributes,
            Collection<OperatorInput> operators) {

        // get the operator of the entity
        String operatorUuid = fieldsToAttributes.get(OPERATOR);
        OperatorInput operator =
                getFirstOrDefaultOperator(
                        operators,
                        operatorUuid,
                        entityClass.getSimpleName(),
                        saveMapGet(fieldsToAttributes, "uuid", FIELDS_TO_VALUES_MAP));

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

    protected <T extends AssetInput> Stream<Optional<T>> assetInputEntityStream(
            Class<T> entityClass,
            EntityFactory<T, AssetInputEntityData> factory,
            Collection<OperatorInput> operators
    ) {
        return assetInputEntityDataStream(entityClass, operators).map(factory::get);
    }

    /**
     * Returns a stream of optional entities that can be build by using {@link
     * NodeAssetInputEntityData} and their corresponding factory.
     *
     * @param entityClass the entity class that should be build
     * @param factory the factory that should be used for the building process
     * @param nodes a collection of {@link NodeInput} entities that should be used to build the
     *     entities
     * @param operators a collection of {@link OperatorInput} entities should be used to build the
     *     entities
     * @param <T> Type of the {@link AssetInput} to expect
     * @return stream of optionals of the entities that has been built by the factor or empty
     *     optionals if the entity could not have been build
     */
    protected <T extends AssetInput> Stream<Optional<T>> nodeAssetEntityStream(
            Class<T> entityClass,
            EntityFactory<T, NodeAssetInputEntityData> factory,
            Collection<NodeInput> nodes,
            Collection<OperatorInput> operators) {
        return nodeAssetInputEntityDataStream(assetInputEntityDataStream(entityClass, operators), nodes)
                .map(dataOpt -> dataOpt.flatMap(factory::get));
    }

    public <T extends AssetInput> Set<T> buildNodeAssetEntities(
            Class<T> entityClass,
            EntityFactory<T, NodeAssetInputEntityData> factory,
            Collection<NodeInput> nodes,
            Collection<OperatorInput> operators,
            ConcurrentHashMap<Class<? extends UniqueEntity>, LongAdder> nonBuildEntities
    ) {
        return nodeAssetEntityStream(entityClass, factory, nodes, operators)
                .filter(isPresentCollectIfNot(entityClass, nonBuildEntities))
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public <T extends AssetInput> Set<T> buildNodeAssetEntities(
            Class<T> entityClass,
            EntityFactory<T, NodeAssetInputEntityData> factory,
            Collection<NodeInput> nodes,
            Collection<OperatorInput> operators
    ) {
        return nodeAssetEntityStream(entityClass, factory, nodes, operators)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public <T extends AssetInput> Set<T> buildAssetInputEntities(
            Class<T> entityClass,
            EntityFactory<T, AssetInputEntityData> factory,
            Collection<OperatorInput> operators
    ) {
        return assetInputEntityStream(entityClass, factory, operators)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public <T extends InputEntity> Set<T> buildEntities(
            Class<T> entityClass,
            EntityFactory<? extends InputEntity, SimpleEntityData> factory
    ) {
        return dataSource
                .getSourceData(entityClass)
                .map(
                        fieldsToAttributes -> {
                            SimpleEntityData data = new SimpleEntityData(fieldsToAttributes, entityClass);
                            return (Optional<T>) factory.get(data);
                        })
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }
}
