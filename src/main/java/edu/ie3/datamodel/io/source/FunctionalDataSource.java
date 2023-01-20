package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.connectors.DataConnector;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.ConnectorInputEntityData;
import edu.ie3.datamodel.io.factory.input.TypedConnectorInputEntityData;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.connector.ConnectorInput;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class FunctionalDataSource {

    DataConnector connector;

    /**
     * Extract data from source to stream
     * @param entityClass
     * @return
     * @param <T>
     */
    public abstract <T extends InputEntity> Stream<Map<String, String>> getSourceData(Class<T> entityClass);



    //--------------------------------------------------------------------------------------

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
            Class<T> entityClass, Collection<OperatorInput> operators
    ) {
        return getSourceData(entityClass)
                .map(fieldsToAttributes -> assetInputEntityDataStream(entityClass, fieldsToAttributes, operators));
    }

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

    public <T extends InputEntity, A extends AssetTypeInput> Set <T> buildTypedEntities(
            Class<T> entityClass,
            EntityFactory<T, TypedConnectorInputEntityData<A>> factory,
            Collection<NodeInput> nodes,
            Collection<OperatorInput> operators,
            Collection<A> types
    ) {
        return buildTypedConnectorEntities().map(dataOpt -> dataOpt.flatMap(factory::get));
    }


/*
    private <T extends ConnectorInput, A extends AssetTypeInput>
    Stream<Optional<T>> typedEntityStream(
            Class<T> entityClass,
            EntityFactory<T, TypedConnectorInputEntityData<A>> factory,
            Collection<NodeInput> nodes,
            Collection<OperatorInput> operators,
            Collection<A> types) {

        return buildTypedConnectorEntityData(
                buildUntypedConnectorInputEntityData(
                        assetInputEntityDataStream(entityClass, operators), nodes),
                types)
                .map(dataOpt -> dataOpt.flatMap(factory::get));
    }

 */

    public <T extends AssetTypeInput> Stream<Optional<TypedConnectorInputEntityData<T>>> buildTypedConnectorEntities(
            Stream<Optional<ConnectorInputEntityData>> noTypeConnectorEntityDataStream,
            Collection<T> availableTypes) {
        return noTypeConnectorEntityDataStream
                .parallel()
                .map(
                        noTypeEntityDataOpt ->
                                noTypeEntityDataOpt.flatMap(
                                        noTypeEntityData -> findAndAddType(noTypeEntityData, availableTypes)));





    /**
     * Enriches the given untyped entity data with the equivalent asset type. If this is not possible,
     * an empty Optional is returned
     *
     * @param noTypeConnectorEntityDataStream Stream of untyped entity data
     * @param availableTypes Yet available asset types
     * @param <T> Type of the asset type
     * @return Stream of option to enhanced data
     */

    /* s.o.
    private <T extends AssetTypeInput>
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

     */

    public <T extends InputEntity> Set<T> buildUntypedEntities() { return null; }

    private <T extends ConnectorInput> Stream<Optional<T>> untypedConnectorInputEntityStream(
            Class<T> entityClass,
            EntityFactory<T, ConnectorInputEntityData> factory,
            Set<NodeInput> nodes,
            Set<OperatorInput> operators) {

        return buildUntypedConnectorInputEntityData(
                assetInputEntityDataStream(entityClass, operators), nodes)
                .map(dataOpt -> dataOpt.flatMap(factory::get));
    }

    /**
     * Converts a stream of {@link AssetInputEntityData} in connection with a collection of known
     * {@link NodeInput}s to a stream of {@link ConnectorInputEntityData}.
     *
     * @param assetInputEntityDataStream Input stream of {@link AssetInputEntityData}
     * @param nodes A collection of known nodes
     * @return A stream on option to matching {@link ConnectorInputEntityData}
     */
    private Stream<Optional<ConnectorInputEntityData>> buildUntypedConnectorInputEntityData(
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
    private Optional<ConnectorInputEntityData> buildUntypedConnectotyrInputEntityData(
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
     * Finds the required asset type and if present, adds it to the untyped entity data
     *
     * @param untypedEntityData Untyped entity data to enrich
     * @param availableTypes Yet available asset types
     * @param <T> Type of the asset type
     * @return Option to enhanced data
     */
    private <T extends AssetTypeInput> Optional<TypedConnectorInputEntityData<T>> findAndAddType(
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
    private <T extends AssetTypeInput> TypedConnectorInputEntityData<T> addTypeToEntityData(
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

}
