package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.connectors.DatabaseConnector;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.factory.input.*;
import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputEntityData;
import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputFactory;
import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputEntityData;
import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputFactory;
import edu.ie3.datamodel.io.factory.input.participant.ChpInputEntityData;
import edu.ie3.datamodel.io.factory.input.participant.ChpInputFactory;
import edu.ie3.datamodel.io.factory.input.participant.HpInputEntityData;
import edu.ie3.datamodel.io.factory.input.participant.SystemParticipantTypedEntityData;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.connector.ConnectorInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import edu.ie3.datamodel.models.input.system.ChpInput;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.models.input.system.type.ChpTypeInput;
import edu.ie3.datamodel.models.input.system.type.HpTypeInput;
import edu.ie3.datamodel.models.input.system.type.SystemParticipantTypeInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class FunctionalDataSource {

    protected static final Logger log = LoggerFactory.getLogger(FunctionalDataSource.class);

    // field names
    protected static final String OPERATOR = "operator";
    protected static final String NODE_A = "nodeA";
    protected static final String NODE_B = "nodeB";
    protected static final String NODE = "node";
    protected static final String TYPE = "type";
    protected static final String FIELDS_TO_VALUES_MAP = "fieldsToValuesMap";

    public DatabaseConnector connector;

    //--------------------------------------------------------------------------------------
    public abstract <T extends InputEntity> Stream<Map<String, String>> getSourceData(Class<T> entityClass);

    //--------------------------------------------------------------------------------------------
    public <T extends InputEntity> Set<T> buildEntities(
            Class<T> entityClass,
            EntityFactory<? extends InputEntity, SimpleEntityData> factory
    ) {
        return getSourceData(entityClass)
                .map(
                        fieldsToAttributes -> {
                            SimpleEntityData data = new SimpleEntityData(fieldsToAttributes, entityClass);
                            return (Optional<T>) factory.get(data);
                        })
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public<T extends AssetInput> Set<T> buildNodeInputEntities(
            Class<T> entityClass,
            EntityFactory<T, AssetInputEntityData> factory,
            Collection<OperatorInput> operators
    ) {
        return assetInputEntityDataStream(entityClass, operators)
                .map(factory::get)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public <T extends SystemParticipantInput, A extends SystemParticipantTypeInput> Set<T> buildSystemParticipantEntities(
            Class<T> entityClass,
            EntityFactory<T, SystemParticipantTypedEntityData<A>> factory,
            Collection<NodeInput> nodes,
            Collection<OperatorInput> operators,
            Collection<A> types,
            ConcurrentHashMap<Class<? extends UniqueEntity>, LongAdder> nonBuildEntities
    ) {
        return typedSystemParticipantEntityStream(entityClass, factory, nodes, operators, types)
                .filter(isPresentCollectIfNot(entityClass, nonBuildEntities))
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public <T extends ConnectorInput, A extends AssetTypeInput> Set<T> buildTypedEntities(
            Class<T> entityClass,
            EntityFactory<T, TypedConnectorInputEntityData<A>> factory,
            Collection<NodeInput> nodes,
            Collection<OperatorInput> operators,
            Collection<A> types,
            ConcurrentHashMap<Class<? extends UniqueEntity>, LongAdder> nonBuildEntities
    ) {
        return typedEntityStream(entityClass, factory, nodes, operators, types)
                .filter(isPresentCollectIfNot(entityClass, nonBuildEntities))
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public <T extends ConnectorInput, A extends AssetTypeInput> Set<T> buildTypedEntities(
            Class<T> entityClass,
            EntityFactory<T, TypedConnectorInputEntityData<A>> factory,
            Collection<NodeInput> nodes,
            Collection<OperatorInput> operators,
            Collection<A> types
    ) {
        return typedEntityStream(entityClass, factory, nodes, operators, types)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
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


    public Set<Transformer3WInput> buildTransformer3WEntities(
            Transformer3WInputFactory transformer3WInputFactory,
            Collection<NodeInput> nodes,
            Collection<Transformer3WTypeInput> transformer3WTypeInputs,
            Collection<OperatorInput> operators,
            ConcurrentHashMap<Transformer3WInput, LongAdder> nonBuildEntities
    ) {
        return buildTransformer3WEntityData(
                buildTypedConnectorEntityData(
                        buildUntypedConnectorInputEntityData(assetInputEntityDataStream(Transformer3WInput.class, operators), nodes),
                        transformer3WTypeInputs),
                nodes)
                //.filter(isPresentCollectIfNot(Transformer3WInput.class, nonBuildEntities))
                .map(dataOpt -> dataOpt.flatMap(transformer3WInputFactory::get))
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public Set<Transformer3WInput> buildTransformer3WEntities(
            Transformer3WInputFactory transformer3WInputFactory,
            Collection<NodeInput> nodes,
            Collection<Transformer3WTypeInput> transformer3WTypeInputs,
            Collection<OperatorInput> operators
    ) {
        return buildTransformer3WEntityData(
                buildTypedConnectorEntityData(
                        buildUntypedConnectorInputEntityData(assetInputEntityDataStream(Transformer3WInput.class, operators), nodes),
                        transformer3WTypeInputs),
                nodes)
                .map(dataOpt -> dataOpt.flatMap(transformer3WInputFactory::get))
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public Set<ChpInput> buildChpInputEntities(
            ChpInputFactory factory,
            Collection<NodeInput> nodes,
            Collection<OperatorInput> operators,
            Collection<ChpTypeInput> chpTypes,
            Collection<ThermalBusInput> thermalBuses,
            Collection<ThermalStorageInput> thermalStorages,
            ConcurrentHashMap<Class<? extends UniqueEntity>, LongAdder> nonBuildEntities
    ) {
        return chpInputStream(factory, nodes, operators, chpTypes, thermalBuses, thermalStorages)
                .filter(isPresentCollectIfNot(ChpInput.class, nonBuildEntities))
                .map(Optional::get)
                .collect(Collectors.toSet());
    }


    // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

    public abstract <V extends Value> IndividualTimeSeries<V> buildIndividualTimeSeries (
            UUID timeSeriesUuid,
            String filePath,
            Function<Map<String, String>, Optional<TimeBasedValue<V>>> fieldToValueFunction
    );

    public <T extends ConnectorInput, A extends AssetTypeInput> Set<T> buildUntypedConnectorInputEntities(
            Class<T> entityClass,
            EntityFactory<T, ConnectorInputEntityData> factory,
            Collection<NodeInput> nodes,
            Collection<OperatorInput> operators,
            ConcurrentHashMap<Class<? extends UniqueEntity>, LongAdder> nonBuildEntities
    ) {
        return untypedConnectorInputEntityStream(entityClass, factory, nodes, operators)
                .filter(isPresentCollectIfNot(entityClass, nonBuildEntities))
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    public <T extends ConnectorInput, A extends AssetTypeInput> Set<T> buildUntypedConnectorInputEntities(
            Class<T> entityClass,
            EntityFactory<T, ConnectorInputEntityData> factory,
            Collection<NodeInput> nodes,
            Collection<OperatorInput> operators
    ) {
        return untypedConnectorInputEntityStream(entityClass, factory, nodes, operators)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

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
        return getSourceData(entityClass)
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

    protected Stream<Optional<Transformer3WInputEntityData>> buildTransformer3WEntityData(
            Stream<Optional<TypedConnectorInputEntityData<Transformer3WTypeInput>>> typedConnectorEntityDataStream,
            Collection<NodeInput> nodes) {
        return typedConnectorEntityDataStream
                .parallel()
                .map(
                        typedEntityDataOpt ->
                                typedEntityDataOpt.flatMap(typeEntityData -> addThirdNode(typeEntityData, nodes)));
    }


    /**
     * Enriches the given untyped entity data with the equivalent asset type. If this is not possible,
     * an empty Optional is returned
     *
     * @param noTypeConnectorEntityDataStream Stream of untyped entity data
     * @param availableTypes Yet available asset types
     * @param <T> Type of the asset type
     * @return Stream of option to enhanced data
     */
    protected <T extends AssetTypeInput>
    Stream<Optional<TypedConnectorInputEntityData<T>>> buildTypedConnectorEntityData(
            Stream<Optional<ConnectorInputEntityData>> noTypeConnectorEntityDataStream,
            Collection<T> availableTypes) {
        return noTypeConnectorEntityDataStream
                .parallel()
                .map(
                        noTypeEntityDataOpt ->
                                noTypeEntityDataOpt.flatMap(
                                        noTypeEntityData -> findAndAddType(noTypeEntityData, availableTypes)));
    }



    /**
     * Converts a stream of {@link AssetInputEntityData} in connection with a collection of known
     * {@link NodeInput}s to a stream of {@link ConnectorInputEntityData}.
     *
     * @param assetInputEntityDataStream Input stream of {@link AssetInputEntityData}
     * @param nodes A collection of known nodes
     * @return A stream on option to matching {@link ConnectorInputEntityData}
     */
    public Stream<Optional<ConnectorInputEntityData>> buildUntypedConnectorInputEntityData(
            Stream<AssetInputEntityData> assetInputEntityDataStream, Collection<NodeInput> nodes) {
        return assetInputEntityDataStream
                .parallel()
                .map(
                        assetInputEntityData ->
                                buildUntypedConnectorInputEntityData(assetInputEntityData, nodes));
    }




    /**
     * Converts a single given {@link AssetInputEntityData} in connection with a collection of known
     * {@link NodeInput}s to {@link ConnectorInputEntityData}. If this is not possible, an empty
     * option is given back.
     *
     * @param assetInputEntityData Input entity data to convert
     * @param nodes A collection of known nodes
     * @return An option to matching {@link ConnectorInputEntityData}
     */
    protected Optional<ConnectorInputEntityData> buildUntypedConnectorInputEntityData(
            AssetInputEntityData assetInputEntityData, Collection<NodeInput> nodes) {
        // get the raw data
        Map<String, String> fieldsToAttributes = assetInputEntityData.getFieldsToValues();

        // get the two connector nodes
        String nodeAUuid = fieldsToAttributes.get(NODE_A);
        String nodeBUuid = fieldsToAttributes.get(NODE_B);
        Optional<NodeInput> nodeA = findFirstEntityByUuid(nodeAUuid, nodes);
        Optional<NodeInput> nodeB = findFirstEntityByUuid(nodeBUuid, nodes);

        // if nodeA or nodeB are not present we return an empty element and log a
        // warning
        if (nodeA.isEmpty() || nodeB.isEmpty()) {
            String debugString =
                    Stream.of(
                                    new AbstractMap.SimpleEntry<>(nodeA, NODE_A + ": " + nodeAUuid),
                                    new AbstractMap.SimpleEntry<>(nodeB, NODE_B + ": " + nodeBUuid))
                            .filter(entry -> entry.getKey().isEmpty())
                            .map(AbstractMap.SimpleEntry::getValue)
                            .collect(Collectors.joining("\n"));

            logSkippingWarning(
                    assetInputEntityData.getTargetClass().getSimpleName(),
                    fieldsToAttributes.get("uuid"),
                    fieldsToAttributes.get("id"),
                    debugString);
            return Optional.empty();
        }

        // remove fields that are passed as objects to constructor
        fieldsToAttributes.keySet().removeAll(new HashSet<>(Arrays.asList(NODE_A, NODE_B)));

        return Optional.of(
                new ConnectorInputEntityData(
                        fieldsToAttributes,
                        assetInputEntityData.getTargetClass(),
                        assetInputEntityData.getOperatorInput(),
                        nodeA.get(),
                        nodeB.get()));
    }


    /**
     * Enriches a given stream of {@link NodeAssetInputEntityData} optionals with a type of {@link
     * SystemParticipantTypeInput} based on the provided collection of types and the fields to values
     * mapping that inside the already provided {@link NodeAssetInputEntityData} instance.
     *
     * @param nodeAssetEntityDataStream the data stream of {@link NodeAssetInputEntityData} optionals
     * @param types the types that should be used for enrichment and to build {@link
     *     SystemParticipantTypedEntityData} from
     * @param <T> the type of the provided entity types as well as the type parameter of the resulting
     *     {@link SystemParticipantTypedEntityData}
     * @return a stream of optional {@link SystemParticipantTypedEntityData} instances or empty
     *     optionals if the type couldn't be found
     */
    private <T extends SystemParticipantTypeInput>
    Stream<Optional<SystemParticipantTypedEntityData<T>>> buildTypedSystemParticipantEntityData(
            Stream<Optional<NodeAssetInputEntityData>> nodeAssetEntityDataStream,
            Collection<T> types) {
        return nodeAssetEntityDataStream
                .parallel()
                .map(
                        nodeAssetInputEntityDataOpt ->
                                nodeAssetInputEntityDataOpt.flatMap(
                                        nodeAssetInputEntityData ->
                                                buildTypedSystemParticipantEntityData(nodeAssetInputEntityData, types)));
    }

    private <T extends SystemParticipantTypeInput>
    Optional<SystemParticipantTypedEntityData<T>> buildTypedSystemParticipantEntityData(
            NodeAssetInputEntityData nodeAssetInputEntityData, Collection<T> types) {
        return getAssetType(
                types,
                nodeAssetInputEntityData.getFieldsToValues(),
                nodeAssetInputEntityData.getClass().getSimpleName())
                .map(
                        // if the optional is present, transform and return to the data,
                        // otherwise return an empty optional
                        assetType -> {
                            Map<String, String> fieldsToAttributes = nodeAssetInputEntityData.getFieldsToValues();

                            // remove fields that are passed as objects to constructor
                            fieldsToAttributes.keySet().remove(TYPE);

                            return new SystemParticipantTypedEntityData<>(
                                    fieldsToAttributes,
                                    nodeAssetInputEntityData.getTargetClass(),
                                    nodeAssetInputEntityData.getOperatorInput(),
                                    nodeAssetInputEntityData.getNode(),
                                    assetType);
                        });
    }

    private Stream<Optional<ChpInputEntityData>> buildChpEntityData(
            Stream<Optional<SystemParticipantTypedEntityData<ChpTypeInput>>> typedEntityDataStream,
            Collection<ThermalStorageInput> thermalStorages,
            Collection<ThermalBusInput> thermalBuses) {

        return typedEntityDataStream
                .parallel()
                .map(
                        typedEntityDataOpt ->
                                typedEntityDataOpt.flatMap(
                                        typedEntityData ->
                                                buildChpEntityData(typedEntityData, thermalStorages, thermalBuses)));
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-


    /**
     * Constructs a stream of {@link SystemParticipantInput} entities wrapped in {@link Optional}s.
     *
     * @param entityClass the class of the entities that should be built
     * @param factory the corresponding factory that is capable of building this entities
     * @param nodes the nodes that should be considered for these entities
     * @param operators the operators that should be considered for these entities
     * @param types the types that should be considered for these entities
     * @param <T> the type of the resulting entity
     * @param <A> the type of the type model of the resulting entity
     * @return a stream of optionals being either empty or holding an instance of a {@link
     *     SystemParticipantInput} of the requested entity class
     */
    private <T extends SystemParticipantInput, A extends SystemParticipantTypeInput>
    Stream<Optional<T>> typedSystemParticipantEntityStream(
            Class<T> entityClass,
            EntityFactory<T, SystemParticipantTypedEntityData<A>> factory,
            Collection<NodeInput> nodes,
            Collection<OperatorInput> operators,
            Collection<A> types) {
        return buildTypedSystemParticipantEntityData(
                nodeAssetInputEntityDataStream(
                        assetInputEntityDataStream(entityClass, operators), nodes),
                types)
                .map(dataOpt -> dataOpt.flatMap(factory::get));
    }

    private Stream<Optional<ChpInput>> chpInputStream(
            ChpInputFactory factory,
            Collection<NodeInput> nodes,
            Collection<OperatorInput> operators,
            Collection<ChpTypeInput> types,
            Collection<ThermalBusInput> thermalBuses,
            Collection<ThermalStorageInput> thermalStorages) {
        return buildChpEntityData(
                buildTypedEntityData(
                        nodeAssetInputEntityDataStream(
                                assetInputEntityDataStream(ChpInput.class, operators), nodes),
                        types),
                thermalStorages,
                thermalBuses)
                .map(dataOpt -> dataOpt.flatMap(factory::get));
    }


    private <T extends ConnectorInput, A extends AssetTypeInput>
    Stream<Optional<T>> typedEntityStream(
            Class<T> entityClass,
            EntityFactory<T, TypedConnectorInputEntityData<A>> factory,
            Collection<NodeInput> nodes,
            Collection<OperatorInput> operators,
            Collection<A> types
    ) {
        return buildTypedConnectorEntityData(
                buildUntypedConnectorInputEntityData(
                        assetInputEntityDataStream(entityClass, operators), nodes),
                types)
                .map(dataOpt -> dataOpt.flatMap(factory::get));
    }

    public <T extends ConnectorInput> Stream<Optional<T>> untypedConnectorInputEntityStream(
            Class<T> entityClass,
            EntityFactory<T, ConnectorInputEntityData> factory,
            Set<NodeInput> nodes,
            Set<OperatorInput> operators
    ) {
        return buildUntypedConnectorInputEntityData(
                assetInputEntityDataStream(entityClass, operators), nodes)
                .map(dataOpt -> dataOpt.flatMap(factory::get));
    }

    private <T extends ConnectorInput> Stream<Optional<T>> untypedConnectorInputEntityStream(
            Class<T> entityClass,
            EntityFactory<T, ConnectorInputEntityData> factory,
            Collection<NodeInput> nodes,
            Collection<OperatorInput> operators
    ) {
        return untypedConnectorInputEntityStream(entityClass, factory, new HashSet<NodeInput>(nodes), new HashSet<OperatorInput>(operators));
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
                .map(
                        assetInputEntityData -> {

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
                                            node.get()));
                        });
    }

    //----------------------------------------------------------------------


    /**
     * Enriches the third node to the already typed entity data of a three winding transformer. If no
     * matching node can be found, return an empty Optional.
     *
     * @param typeEntityData Already typed entity data
     * @param nodes Yet available nodes
     * @return An option to the enriched data
     */
    protected Optional<Transformer3WInputEntityData> addThirdNode(
            TypedConnectorInputEntityData<Transformer3WTypeInput> typeEntityData,
            Collection<NodeInput> nodes) {

        // get the raw data
        Map<String, String> fieldsToAttributes = typeEntityData.getFieldsToValues();

        // get nodeC of the transformer
        String nodeCUuid = fieldsToAttributes.get("nodeC");
        Optional<NodeInput> nodeC = findFirstEntityByUuid(nodeCUuid, nodes);

        // if nodeC is not present we return an empty element and
        // log a warning
        if (nodeC.isEmpty()) {
            logSkippingWarning(
                    typeEntityData.getTargetClass().getSimpleName(),
                    fieldsToAttributes.get("uuid"),
                    fieldsToAttributes.get("id"),
                    "nodeC: " + nodeCUuid);
            return Optional.empty();
        }

        // remove fields that are passed as objects to constructor
        fieldsToAttributes.keySet().remove("nodeC");

        return Optional.of(
                new Transformer3WInputEntityData(
                        fieldsToAttributes,
                        typeEntityData.getTargetClass(),
                        typeEntityData.getOperatorInput(),
                        typeEntityData.getNodeA(),
                        typeEntityData.getNodeB(),
                        nodeC.get(),
                        typeEntityData.getType()));
    }

    //----------------------------------------------------------------------------------------

    /*
    public <T extends AssetTypeInput> Stream<Optional<TypedConnectorInputEntityData<T>>> buildTypedConnectorEntities() { return null; }
     */


    /*
    public <T extends AssetTypeInput> Stream<Optional<TypedConnectorInputEntityData<T>>> buildTypedConnectorEntities(
            Stream<Optional<ConnectorInputEntityData>> noTypeConnectorEntityDataStream,
            Collection<T> availableTypes) {
        return noTypeConnectorEntityDataStream
                .parallel()
                .map(
                        noTypeEntityDataOpt ->
                                noTypeEntityDataOpt.flatMap(
                                        noTypeEntityData -> findAndAddType(noTypeEntityData, availableTypes)));
    }
     */

    //--------------------------------------------------------------------------------------
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

    // ------------------------------------------------------------------------------------------------




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
     * @param operators the collections of {@link OperatorInput}s that should be searched in
     * @param operatorUuid the operator uuid that is requested
     * @return either the first found instancen of {@link OperatorInput} or {@link
     *     OperatorInput#NO_OPERATOR_ASSIGNED}
     */
    private OperatorInput getFirstOrDefaultOperator(
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

    protected String saveMapGet(Map<String, String> map, String key, String mapName) {
        return Optional.ofNullable(map.get(key))
                .orElse(
                        "Key '"
                                + key
                                + "' not found"
                                + (mapName.isEmpty() ? "!" : " in map '" + mapName + "'!"));
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
            Class<? extends UniqueEntity> entityClass, LongAdder noOfInvalidElements) {

        log.error(
                "{} entities of type '{}' are missing required elements!",
                noOfInvalidElements,
                entityClass.getSimpleName());
    }


    //-=-=- Graphic Source -=-=-=-=-

    /**
     * Builds a stream of {@link NodeGraphicInputEntityData} instances that can be consumed by a
     * {@link NodeGraphicInputFactory} to build instances of {@link NodeGraphicInput} entities. This
     * method depends on corresponding instances of {@link NodeInput} entities that are represented by
     * a corresponding {@link NodeGraphicInput} entity. The determination of matching {@link
     * NodeInput} and {@link NodeGraphicInput} entities is carried out by the UUID of the {@link
     * NodeInput} entity. Hence it is crucial to only pass over collections that are pre-checked for
     * the uniqueness of the UUIDs of the nodes they contain. No further sanity checks are included in
     * this method. If no UUID of a {@link NodeInput} entity can be found for a {@link
     * NodeGraphicInputEntityData} instance, an empty optional is included in the stream and warning
     * is logged.
     *
     * @param nodes a set of nodes with unique uuids
     * @return a stream of optional {@link NodeGraphicInput} entities
     */

    /*
    private Stream<Optional<NodeGraphicInputEntityData>> buildNodeGraphicEntityData(
            Set<NodeInput> nodes) {
        return buildStreamWithFieldsToAttributesMap(NodeGraphicInput.class, connector)
                .map(fieldsToAttributes -> buildNodeGraphicEntityData(fieldsToAttributes, nodes));
    }

     */

/*
    private Optional<NodeGraphicInputEntityData> buildNodeGraphicEntityData(
            Map<String, String> fieldsToAttributes, Set<NodeInput> nodes) {

        // get the node of the entity
        String nodeUuid = fieldsToAttributes.get(NODE);
        Optional<NodeInput> node = findFirstEntityByUuid(nodeUuid, nodes);

        // if the node is not present we return an empty element and
        // log a warning
        if (node.isEmpty()) {
            logSkippingWarning(
                    NodeGraphicInput.class.getSimpleName(),
                    fieldsToAttributes.get("uuid"),
                    "no id (graphic entities don't have one)",
                    NODE + ": " + nodeUuid);
            return Optional.empty();
        }

        // remove fields that are passed as objects to constructor
        fieldsToAttributes.keySet().remove(NODE);

        return Optional.of(new NodeGraphicInputEntityData(fieldsToAttributes, node.get()));
    }

 */

    /**
     * Builds a stream of {@link LineGraphicInputEntityData} instances that can be consumed by a
     * {@link LineGraphicInputFactory} to build instances of {@link LineGraphicInput} entities. This
     * method depends on corresponding instances of {@link LineInput} entities that are represented by
     * a corresponding {@link LineGraphicInput} entity. The determination of matching {@link
     * LineInput} and {@link LineGraphicInput} entities is carried out by the UUID of the {@link
     * LineInput} entity. Hence it is crucial to only pass over collections that are pre-checked for
     * the uniqueness of the UUIDs of the nodes they contain. No further sanity checks are included in
     * this method. If no UUID of a {@link LineInput} entity can be found for a {@link
     * LineGraphicInputEntityData} instance, an empty optional is included in the stream and warning
     * is logged.
     *
     * @param lines a set of lines with unique uuids
     * @return a stream of optional {@link LineGraphicInput} entities
     */
    /*
    private Stream<Optional<LineGraphicInputEntityData>> buildLineGraphicEntityData(
            Set<LineInput> lines) {
        return buildStreamWithFieldsToAttributesMap(LineGraphicInput.class, connector)
                .map(fieldsToAttributes -> buildLineGraphicEntityData(fieldsToAttributes, lines));
    }

     */

    /*
    private Optional<LineGraphicInputEntityData> buildLineGraphicEntityData(
            Map<String, String> fieldsToAttributes, Set<LineInput> lines) {

        // get the node of the entity
        String lineUuid = fieldsToAttributes.get("line");
        Optional<LineInput> line = findFirstEntityByUuid(lineUuid, lines);

        // if the node is not present we return an empty element and
        // log a warning
        if (line.isEmpty()) {
            logSkippingWarning(
                    LineGraphicInput.class.getSimpleName(),
                    fieldsToAttributes.get("uuid"),
                    "no id (graphic entities don't have one)",
                    "line: " + lineUuid);
            return Optional.empty();
        }

        // remove fields that are passed as objects to constructor
        fieldsToAttributes.keySet().remove("line");

        return Optional.of(new LineGraphicInputEntityData(fieldsToAttributes, line.get()));
    }

     */

    //-=-=- ThermalSource -=-=-
    /*
    private Stream<Optional<ThermalUnitInputEntityData>> buildThermalUnitInputEntityData(
            AssetInputEntityData assetInputEntityData, Collection<ThermalBusInput> thermalBuses) {

        // get the raw data
        Map<String, String> fieldsToAttributes = assetInputEntityData.getFieldsToValues();

        // get the thermal bus input for this chp unit
        String thermalBusUuid = fieldsToAttributes.get("thermalbus");
        Optional<ThermalBusInput> thermalBus =
                thermalBuses.stream()
                        .filter(storage -> storage.getUuid().toString().equalsIgnoreCase(thermalBusUuid))
                        .findFirst();

        // remove fields that are passed as objects to constructor
        fieldsToAttributes.keySet().removeAll(new HashSet<>(Collections.singletonList("thermalbus")));

        // if the type is not present we return an empty element and
        // log a warning
        if (thermalBus.isEmpty()) {
            logSkippingWarning(
                    assetInputEntityData.getTargetClass().getSimpleName(),
                    fieldsToAttributes.get("uuid"),
                    fieldsToAttributes.get("id"),
                    "thermalBus: " + thermalBusUuid);
            return Stream.of(Optional.empty());
        }

        return Stream.of(
                Optional.of(
                        new ThermalUnitInputEntityData(
                                assetInputEntityData.getFieldsToValues(),
                                assetInputEntityData.getTargetClass(),
                                assetInputEntityData.getOperatorInput(),
                                thermalBus.get())));
    }

     */

    //-=-=- SystemParticipantSource -=-=-

    /**
     * Constructs a stream of {@link SystemParticipantInput} entities wrapped in {@link Optional}s.
     *
     * @param entityClass the class of the entities that should be built
     * @param factory the corresponding factory that is capable of building this entities
     * @param nodes the nodes that should be considered for these entities
     * @param operators the operators that should be considered for these entities
     * @param types the types that should be considered for these entities
     * @param <T> the type of the resulting entity
     * @param <A> the type of the type model of the resulting entity
     * @return a stream of optionals being either empty or holding an instance of a {@link
     *     SystemParticipantInput} of the requested entity class
     */

    /*
    private <T extends SystemParticipantInput, A extends SystemParticipantTypeInput>
    Stream<Optional<T>> typedEntityStream(
            Class<T> entityClass,
            EntityFactory<T, SystemParticipantTypedEntityData<A>> factory,
            Set<NodeInput> nodes,
            Set<OperatorInput> operators,
            Set<A> types) {
        return buildTypedEntityData(
                nodeAssetInputEntityDataStream(
                        assetInputEntityDataStream(entityClass, operators), nodes),
                types)
                .map(dataOpt -> dataOpt.flatMap(factory::get));
    }

     */

    /*
    private Stream<Optional<ChpInput>> chpInputStream(
      Set<NodeInput> nodes,
      Set<OperatorInput> operators,
      Set<ChpTypeInput> types,
      Set<ThermalBusInput> thermalBuses,
      Set<ThermalStorageInput> thermalStorages) {
    return buildChpEntityData(
            buildTypedEntityData(
                nodeAssetInputEntityDataStream(
                    assetInputEntityDataStream(ChpInput.class, operators), nodes),
                types),
            thermalStorages,
            thermalBuses)
        .map(dataOpt -> dataOpt.flatMap(chpInputFactory::get));
  }
     */

    /*
  private Stream<Optional<HpInput>> hpInputStream(
      Set<NodeInput> nodes,
      Set<OperatorInput> operators,
      Set<HpTypeInput> types,
      Set<ThermalBusInput> thermalBuses) {
    return buildHpEntityData(
            buildTypedEntityData(
                nodeAssetInputEntityDataStream(
                    assetInputEntityDataStream(HpInput.class, operators), nodes),
                types),
            thermalBuses)
        .map(dataOpt -> dataOpt.flatMap(hpInputFactory::get));
  }

     */


    /**
     * Enriches a given stream of {@link NodeAssetInputEntityData} optionals with a type of {@link
     * SystemParticipantTypeInput} based on the provided collection of types and the fields to values
     * mapping that inside the already provided {@link NodeAssetInputEntityData} instance.
     *
     * @param nodeAssetEntityDataStream the data stream of {@link NodeAssetInputEntityData} optionals
     * @param types the types that should be used for enrichment and to build {@link
     *     SystemParticipantTypedEntityData} from
     * @param <T> the type of the provided entity types as well as the type parameter of the resulting
     *     {@link SystemParticipantTypedEntityData}
     * @return a stream of optional {@link SystemParticipantTypedEntityData} instances or empty
     *     optionals if the type couldn't be found
     */

    private <T extends SystemParticipantTypeInput>
    Stream<Optional<SystemParticipantTypedEntityData<T>>> buildTypedEntityData(
            Stream<Optional<NodeAssetInputEntityData>> nodeAssetEntityDataStream,
            Collection<T> types) {
        return nodeAssetEntityDataStream
                .parallel()
                .map(
                        nodeAssetInputEntityDataOpt ->
                                nodeAssetInputEntityDataOpt.flatMap(
                                        nodeAssetInputEntityData ->
                                                buildTypedEntityData(nodeAssetInputEntityData, types)));
    }

    private <T extends SystemParticipantTypeInput>
    Optional<SystemParticipantTypedEntityData<T>> buildTypedEntityData(
            NodeAssetInputEntityData nodeAssetInputEntityData, Collection<T> types) {
        return getAssetType(
                types,
                nodeAssetInputEntityData.getFieldsToValues(),
                nodeAssetInputEntityData.getClass().getSimpleName())
                .map(
                        // if the optional is present, transform and return to the data,
                        // otherwise return an empty optional
                        assetType -> {
                            Map<String, String> fieldsToAttributes = nodeAssetInputEntityData.getFieldsToValues();

                            // remove fields that are passed as objects to constructor
                            fieldsToAttributes.keySet().remove(TYPE);

                            return new SystemParticipantTypedEntityData<>(
                                    fieldsToAttributes,
                                    nodeAssetInputEntityData.getTargetClass(),
                                    nodeAssetInputEntityData.getOperatorInput(),
                                    nodeAssetInputEntityData.getNode(),
                                    assetType);
                        });
    }



    /**
     * Enriches a given stream of {@link SystemParticipantTypedEntityData} optionals with a type of
     * {@link ThermalBusInput} based on the provided collection of buses and the fields to values
     * mapping inside the already provided {@link SystemParticipantTypedEntityData} instance.
     *
     * @param typedEntityDataStream the data stream of {@link SystemParticipantTypedEntityData}
     *     optionals
     * @param thermalBuses the thermal buses that should be used for enrichment and to build {@link
     *     HpInputEntityData}
     * @return stream of optional {@link HpInputEntityData} instances or empty optionals if they
     *     thermal bus couldn't be found
     */
   /*
    private Stream<Optional<HpInputEntityData>> buildHpEntityData(
            Stream<Optional<SystemParticipantTypedEntityData<HpTypeInput>>> typedEntityDataStream,
            Collection<ThermalBusInput> thermalBuses) {

        return typedEntityDataStream
                .parallel()
                .map(
                        typedEntityDataOpt ->
                                typedEntityDataOpt.flatMap(
                                        typedEntityData -> buildHpEntityData(typedEntityData, thermalBuses)));
    }

    */

    /*
    private Optional<HpInputEntityData> buildHpEntityData(
            SystemParticipantTypedEntityData<HpTypeInput> typedEntityData,
            Collection<ThermalBusInput> thermalBuses) {
        // get the raw data
        Map<String, String> fieldsToAttributes = typedEntityData.getFieldsToValues();

        // get the thermal bus input for this chp unit and try to built the entity data
        Optional<HpInputEntityData> hpInputEntityDataOpt =
                Optional.ofNullable(fieldsToAttributes.get(THERMAL_BUS))
                        .flatMap(
                                thermalBusUuid ->
                                        thermalBuses.stream()
                                                .filter(
                                                        storage ->
                                                                storage.getUuid().toString().equalsIgnoreCase(thermalBusUuid))
                                                .findFirst()
                                                .map(
                                                        thermalBus -> {

                                                            // remove fields that are passed as objects to constructor
                                                            fieldsToAttributes.keySet().remove(THERMAL_BUS);

                                                            return new HpInputEntityData(
                                                                    fieldsToAttributes,
                                                                    typedEntityData.getOperatorInput(),
                                                                    typedEntityData.getNode(),
                                                                    typedEntityData.getTypeInput(),
                                                                    thermalBus);
                                                        }));

        // if the requested entity is not present we return an empty element and
        // log a warning
        if (hpInputEntityDataOpt.isEmpty()) {
            logSkippingWarning(
                    typedEntityData.getTargetClass().getSimpleName(),
                    saveMapGet(fieldsToAttributes, "uuid", FIELDS_TO_VALUES_MAP),
                    saveMapGet(fieldsToAttributes, "id", FIELDS_TO_VALUES_MAP),
                    "thermalBus: " + saveMapGet(fieldsToAttributes, THERMAL_BUS, FIELDS_TO_VALUES_MAP));
        }

        return hpInputEntityDataOpt;
    }

     */

    /**
     * Enriches a given stream of {@link SystemParticipantTypedEntityData} optionals with a type of
     * {@link ThermalBusInput} and {@link ThermalStorageInput} based on the provided collection of
     * buses, storages and the fields to values mapping inside the already provided {@link
     * SystemParticipantTypedEntityData} instance.
     *
     * @param typedEntityDataStream the data stream of {@link SystemParticipantTypedEntityData}
     *     optionals
     * @param thermalStorages the thermal storages that should be used for enrichment and to build
     *     {@link ChpInputEntityData}
     * @param thermalBuses the thermal buses that should be used for enrichment and to build {@link
     *     ChpInputEntityData}
     * @return stream of optional {@link ChpInputEntityData}instances or empty optionals if they
     *     thermal bus couldn't be found
     */
    /*

     */



    //-=-=- ResultEntitySource -=-=-

    /*

  private <T extends ResultEntity> Set<T> getResultEntities(
      Class<T> entityClass, SimpleEntityFactory<? extends ResultEntity> factory) {
    return simpleEntityDataStream(entityClass)
        .map(
            entityData ->
                factory.get(entityData).flatMap(loadResult -> cast(entityClass, loadResult)))
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

    private <T extends ResultEntity> Optional<T> cast(
      Class<T> entityClass, ResultEntity resultEntity) {
    if (resultEntity.getClass().equals(entityClass)) {
      // safe here as a) type is checked and b) csv data stream already filters non-fitting input
      // data
      return Optional.of(entityClass.cast(resultEntity));
    } else {
      return Optional.empty();
    }
  }
     */





}
