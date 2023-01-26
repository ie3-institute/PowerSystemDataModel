package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.connectors.DatabaseConnector;
import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputEntityData;
import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputFactory;
import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputEntityData;
import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputFactory;
import edu.ie3.datamodel.io.factory.input.participant.ChpInputEntityData;
import edu.ie3.datamodel.io.factory.input.participant.HpInputEntityData;
import edu.ie3.datamodel.io.factory.input.participant.SystemParticipantTypedEntityData;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
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
    public abstract <T extends UniqueEntity> Stream<Map<String, String>> getSourceData(Class<T> entityClass);

    //public abstract <T extends ResultEntity> Stream<Map<String, String>> getSourceData(Class<T> entityClass);

    // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

    public abstract <V extends Value> IndividualTimeSeries<V> buildIndividualTimeSeries (
            UUID timeSeriesUuid,
            String filePath,
            Function<Map<String, String>, Optional<TimeBasedValue<V>>> fieldToValueFunction
    );

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

    //----------------------------------------------------------------------



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
