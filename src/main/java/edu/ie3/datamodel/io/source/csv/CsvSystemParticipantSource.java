/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.participant.*;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.RawGridSource;
import edu.ie3.datamodel.io.source.SystemParticipantSource;
import edu.ie3.datamodel.io.source.ThermalSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Source that provides the capability to build entities of type {@link SystemParticipantInput} as
 * well as {@link SystemParticipants} container from .csv files.
 *
 * <p>This source is <b>not buffered</b> which means each call on a getter method always tries to
 * read all data is necessary to return the requested objects in a hierarchical cascading way.
 *
 * <p>If performance is an issue, it is recommended to read the data cascading starting with reading
 * nodes and then using the getters with arguments to avoid reading the same data multiple times.
 *
 * <p>The resulting sets are always unique on object <b>and</b> UUID base (with distinct UUIDs).
 *
 * @version 0.1
 * @since 03.04.20
 */
public class CsvSystemParticipantSource extends CsvDataSource implements SystemParticipantSource {

  private static final String THERMAL_STORAGE = "thermalstorage";
  private static final String THERMAL_BUS = "thermalbus";

  // general fields
  private final TypeSource typeSource;
  private final RawGridSource rawGridSource;
  private final ThermalSource thermalSource;

  // factories
  private final BmInputFactory bmInputFactory;
  private final ChpInputFactory chpInputFactory;
  private final EvInputFactory evInputFactory;
  private final FixedFeedInInputFactory fixedFeedInInputFactory;
  private final HpInputFactory hpInputFactory;
  private final LoadInputFactory loadInputFactory;
  private final PvInputFactory pvInputFactory;
  private final StorageInputFactory storageInputFactory;
  private final WecInputFactory wecInputFactory;
  private final EvcsInputFactory evcsInputFactory;
  private final EmInputFactory emInputFactory;

  public CsvSystemParticipantSource(
      String csvSep,
      String participantsFolderPath,
      FileNamingStrategy fileNamingStrategy,
      TypeSource typeSource,
      ThermalSource thermalSource,
      RawGridSource rawGridSource) {
    super(csvSep, participantsFolderPath, fileNamingStrategy);
    this.typeSource = typeSource;
    this.rawGridSource = rawGridSource;
    this.thermalSource = thermalSource;

    // init factories
    this.bmInputFactory = new BmInputFactory();
    this.chpInputFactory = new ChpInputFactory();
    this.evInputFactory = new EvInputFactory();
    this.fixedFeedInInputFactory = new FixedFeedInInputFactory();
    this.hpInputFactory = new HpInputFactory();
    this.loadInputFactory = new LoadInputFactory();
    this.pvInputFactory = new PvInputFactory();
    this.storageInputFactory = new StorageInputFactory();
    this.wecInputFactory = new WecInputFactory();
    this.evcsInputFactory = new EvcsInputFactory();
    this.emInputFactory = new EmInputFactory();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<SystemParticipants> getSystemParticipants() {

    // read all needed entities
    /// start with types and operators
    Set<OperatorInput> operators = typeSource.getOperators();
    Set<BmTypeInput> bmTypes = typeSource.getBmTypes();
    Set<ChpTypeInput> chpTypes = typeSource.getChpTypes();
    Set<EvTypeInput> evTypes = typeSource.getEvTypes();
    Set<HpTypeInput> hpTypes = typeSource.getHpTypes();
    Set<StorageTypeInput> storageTypes = typeSource.getStorageTypes();
    Set<WecTypeInput> wecTypes = typeSource.getWecTypes();

    /// go on with the thermal assets
    Set<ThermalBusInput> thermalBuses = thermalSource.getThermalBuses(operators);
    Set<ThermalStorageInput> thermalStorages =
        thermalSource.getThermalStorages(operators, thermalBuses);

    /// go on with the nodes
    Set<NodeInput> nodes = rawGridSource.getNodes(operators);

    // start with the entities needed for SystemParticipants container
    /// as we want to return a working grid, keep an eye on empty optionals which is equal to
    // elements that
    /// have been unable to be built e.g. due to missing elements they depend on
    ConcurrentHashMap<Class<? extends UniqueEntity>, LongAdder> nonBuildEntities =
        new ConcurrentHashMap<>();

    Set<FixedFeedInInput> fixedFeedInInputs =
        nodeAssetEntityStream(FixedFeedInInput.class, fixedFeedInInputFactory, nodes, operators)
            .filter(isPresentCollectIfNot(FixedFeedInInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());
    Set<PvInput> pvInputs =
        nodeAssetEntityStream(PvInput.class, pvInputFactory, nodes, operators)
            .filter(isPresentCollectIfNot(PvInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());
    Set<LoadInput> loads =
        nodeAssetEntityStream(LoadInput.class, loadInputFactory, nodes, operators)
            .filter(isPresentCollectIfNot(LoadInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());
    Set<BmInput> bmInputs =
        typedEntityStream(BmInput.class, bmInputFactory, nodes, operators, bmTypes)
            .filter(isPresentCollectIfNot(BmInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());
    Set<StorageInput> storages =
        typedEntityStream(StorageInput.class, storageInputFactory, nodes, operators, storageTypes)
            .filter(isPresentCollectIfNot(StorageInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());
    Set<WecInput> wecInputs =
        typedEntityStream(WecInput.class, wecInputFactory, nodes, operators, wecTypes)
            .filter(isPresentCollectIfNot(WecInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());
    Set<EvInput> evs =
        typedEntityStream(EvInput.class, evInputFactory, nodes, operators, evTypes)
            .filter(isPresentCollectIfNot(EvInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());
    Set<EvcsInput> evcs =
        nodeAssetEntityStream(EvcsInput.class, evcsInputFactory, nodes, operators)
            .filter(isPresentCollectIfNot(EvcsInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());
    Set<ChpInput> chpInputs =
        chpInputStream(nodes, operators, chpTypes, thermalBuses, thermalStorages)
            .filter(isPresentCollectIfNot(ChpInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());
    Set<HpInput> hpInputs =
        hpInputStream(nodes, operators, hpTypes, thermalBuses)
            .filter(isPresentCollectIfNot(HpInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());
    Set<EmInput> emInputs =
        nodeAssetEntityStream(EmInput.class, emInputFactory, nodes, operators)
            .filter(isPresentCollectIfNot(EmInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());

    // if we found invalid elements return an empty optional and log the problems
    if (!nonBuildEntities.isEmpty()) {
      nonBuildEntities.forEach(this::printInvalidElementInformation);
      return Optional.empty();
    }

    // if everything is fine, return a system participants container
    return Optional.of(
        new SystemParticipants(
            bmInputs,
            chpInputs,
            evcs,
            evs,
            fixedFeedInInputs,
            hpInputs,
            loads,
            pvInputs,
            storages,
            wecInputs,
            emInputs));
  }

  /** {@inheritDoc} */
  @Override
  public Set<FixedFeedInInput> getFixedFeedIns() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getFixedFeedIns(rawGridSource.getNodes(operators), operators);
  }
  /**
   * {@inheritDoc}
   *
   * <p>If the set of {@link NodeInput} entities is not exhaustive for all available {@link
   * FixedFeedInInput} entities (e.g. a {@link NodeInput} entity is missing) or if an error during
   * the building process occurs, the entity that misses something will be skipped (which can be
   * seen as a filtering functionality), but all entities that are able to be built will be returned
   * anyway and the elements that couldn't have been built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<FixedFeedInInput> getFixedFeedIns(Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return nodeAssetEntityStream(FixedFeedInInput.class, fixedFeedInInputFactory, nodes, operators)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

  /** {@inheritDoc} */
  @Override
  public Set<PvInput> getPvPlants() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getPvPlants(rawGridSource.getNodes(operators), operators);
  }

  /**
   * {@inheritDoc}
   *
   * <p>If the set of {@link NodeInput} entities is not exhaustive for all available {@link PvInput}
   * entities (e.g. a {@link NodeInput} entity is missing) or if an error during the building
   * process occurs, the entity that misses something will be skipped (which can be seen as a
   * filtering functionality), but all entities that are able to be built will be returned anyway
   * and the elements that couldn't have been built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<PvInput> getPvPlants(Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return nodeAssetEntityStream(PvInput.class, pvInputFactory, nodes, operators)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

  /** {@inheritDoc} */
  @Override
  public Set<LoadInput> getLoads() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getLoads(rawGridSource.getNodes(operators), operators);
  }

  /**
   * {@inheritDoc}
   *
   * <p>If the set of {@link NodeInput} entities is not exhaustive for all available {@link
   * LoadInput} entities (e.g. a {@link NodeInput} entity is missing) or if an error during the
   * building process occurs, the entity that misses something will be skipped (which can be seen as
   * a filtering functionality), but all entities that are able to be built will be returned anyway
   * and the elements that couldn't have been built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<LoadInput> getLoads(Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return nodeAssetEntityStream(LoadInput.class, loadInputFactory, nodes, operators)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }
  /** {@inheritDoc} */
  @Override
  public Set<EvcsInput> getEvCS() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getEvCS(rawGridSource.getNodes(operators), operators);
  }

  /**
   * {@inheritDoc}
   *
   * <p>If the set of {@link NodeInput} entities is not exhaustive for all available {@link
   * EvcsInput} entities (e.g. a {@link NodeInput} entity is missing) or if an error during the
   * building process occurs, the entity that misses something will be skipped (which can be seen as
   * a filtering functionality), but all entities that are able to be built will be returned anyway
   * and the elements that couldn't have been built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<EvcsInput> getEvCS(Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return nodeAssetEntityStream(EvcsInput.class, evcsInputFactory, nodes, operators)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

  /** {@inheritDoc} */
  @Override
  public Set<BmInput> getBmPlants() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getBmPlants(rawGridSource.getNodes(operators), operators, typeSource.getBmTypes());
  }

  /**
   * {@inheritDoc}
   *
   * <p>If one of the sets of {@link NodeInput} or {@link BmTypeInput} entities is not exhaustive
   * for all available {@link BmInput} entities (e.g. a {@link NodeInput} or {@link BmTypeInput}
   * entity is missing) or if an error during the building process occurs, the entity that misses
   * something will be skipped (which can be seen as a filtering functionality) but all entities
   * that are able to be built will be returned anyway and the elements that couldn't have been
   * built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<BmInput> getBmPlants(
      Set<NodeInput> nodes, Set<OperatorInput> operators, Set<BmTypeInput> types) {
    return typedEntityStream(BmInput.class, bmInputFactory, nodes, operators, types)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }
  /** {@inheritDoc} */
  @Override
  public Set<StorageInput> getStorages() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getStorages(rawGridSource.getNodes(operators), operators, typeSource.getStorageTypes());
  }

  /**
   * {@inheritDoc}
   *
   * <p>If one of the sets of {@link NodeInput} or {@link StorageTypeInput} entities is not
   * exhaustive for all available {@link StorageInput} entities (e.g. a {@link NodeInput} or {@link
   * StorageTypeInput} entity is missing) or if an error during the building process occurs, the
   * entity that misses something will be skipped (which can be seen as a filtering functionality)
   * but all entities that are able to be built will be returned anyway and the elements that
   * couldn't have been built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<StorageInput> getStorages(
      Set<NodeInput> nodes, Set<OperatorInput> operators, Set<StorageTypeInput> types) {
    return typedEntityStream(StorageInput.class, storageInputFactory, nodes, operators, types)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }
  /** {@inheritDoc} */
  @Override
  public Set<WecInput> getWecPlants() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getWecPlants(rawGridSource.getNodes(operators), operators, typeSource.getWecTypes());
  }

  /**
   * {@inheritDoc}
   *
   * <p>If one of the sets of {@link NodeInput} or {@link WecTypeInput} entities is not exhaustive
   * for all available {@link WecInput} entities (e.g. a {@link NodeInput} or {@link WecTypeInput}
   * entity is missing) or if an error during the building process occurs, the entity that misses
   * something will be skipped (which can be seen as a filtering functionality) but all entities
   * that are able to be built will be returned anyway and the elements that couldn't have been
   * built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<WecInput> getWecPlants(
      Set<NodeInput> nodes, Set<OperatorInput> operators, Set<WecTypeInput> types) {
    return typedEntityStream(WecInput.class, wecInputFactory, nodes, operators, types)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }
  /** {@inheritDoc} */
  @Override
  public Set<EvInput> getEvs() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getEvs(rawGridSource.getNodes(operators), operators, typeSource.getEvTypes());
  }

  /**
   * {@inheritDoc}
   *
   * <p>If one of the sets of {@link NodeInput} or {@link EvTypeInput} entities is not exhaustive
   * for all available {@link EvInput} entities (e.g. a {@link NodeInput} or {@link EvTypeInput}
   * entity is missing) or if an error during the building process occurs, the entity that misses
   * something will be skipped (which can be seen as a filtering functionality) but all entities
   * that are able to be built will be returned anyway and the elements that couldn't have been
   * built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<EvInput> getEvs(
      Set<NodeInput> nodes, Set<OperatorInput> operators, Set<EvTypeInput> types) {
    return typedEntityStream(EvInput.class, evInputFactory, nodes, operators, types)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

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
  /** {@inheritDoc} */
  @Override
  public Set<ChpInput> getChpPlants() {
    Set<OperatorInput> operators = typeSource.getOperators();
    Set<ThermalBusInput> thermalBuses = thermalSource.getThermalBuses(operators);
    return getChpPlants(
        rawGridSource.getNodes(operators),
        operators,
        typeSource.getChpTypes(),
        thermalBuses,
        thermalSource.getThermalStorages(operators, thermalBuses));
  }

  /**
   * {@inheritDoc}
   *
   * <p>If one of the sets of {@link NodeInput}, {@link ThermalBusInput}, {@link
   * ThermalStorageInput} or {@link ChpTypeInput} entities is not exhaustive for all available
   * {@link ChpInput} entities (e.g. a {@link NodeInput} or {@link ChpTypeInput} entity is missing)
   * or if an error during the building process occurs, the entity that misses something will be
   * skipped (which can be seen as a filtering functionality) but all entities that are able to be
   * built will be returned anyway and the elements that couldn't have been built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<ChpInput> getChpPlants(
      Set<NodeInput> nodes,
      Set<OperatorInput> operators,
      Set<ChpTypeInput> types,
      Set<ThermalBusInput> thermalBuses,
      Set<ThermalStorageInput> thermalStorages) {

    return chpInputStream(nodes, operators, types, thermalBuses, thermalStorages)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

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
  /** {@inheritDoc} */
  @Override
  public Set<HpInput> getHeatPumps() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getHeatPumps(
        rawGridSource.getNodes(operators),
        operators,
        typeSource.getHpTypes(),
        thermalSource.getThermalBuses());
  }

  /**
   * {@inheritDoc}
   *
   * <p>If one of the sets of {@link NodeInput}, {@link ThermalBusInput} or {@link HpTypeInput}
   * entities is not exhaustive for all available {@link HpInput} entities (e.g. a {@link NodeInput}
   * or {@link HpTypeInput} entity is missing) or if an error during the building process occurs,
   * the entity that misses something will be skipped (which can be seen as a filtering
   * functionality) but all entities that are able to be built will be returned anyway and the
   * elements that couldn't have been built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<HpInput> getHeatPumps(
      Set<NodeInput> nodes,
      Set<OperatorInput> operators,
      Set<HpTypeInput> types,
      Set<ThermalBusInput> thermalBuses) {
    return hpInputStream(nodes, operators, types, thermalBuses)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

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

  private Optional<ChpInputEntityData> buildChpEntityData(
      SystemParticipantTypedEntityData<ChpTypeInput> typedEntityData,
      Collection<ThermalStorageInput> thermalStorages,
      Collection<ThermalBusInput> thermalBuses) {

    // get the raw data
    Map<String, String> fieldsToAttributes = typedEntityData.getFieldsToValues();

    // get the thermal storage input for this chp unit
    Optional<ThermalStorageInput> thermalStorage =
        Optional.ofNullable(fieldsToAttributes.get(THERMAL_STORAGE))
            .flatMap(
                thermalStorageUuid -> findFirstEntityByUuid(thermalStorageUuid, thermalStorages));

    // get the thermal bus input for this chp unit
    Optional<ThermalBusInput> thermalBus =
        Optional.ofNullable(fieldsToAttributes.get("thermalBus"))
            .flatMap(thermalBusUuid -> findFirstEntityByUuid(thermalBusUuid, thermalBuses));

    // if the thermal storage or the thermal bus are not present we return an
    // empty element and log a warning
    if (!thermalStorage.isPresent() || !thermalBus.isPresent()) {
      StringBuilder sB = new StringBuilder();
      if (!thermalStorage.isPresent()) {
        sB.append("thermalStorage: ")
            .append(saveMapGet(fieldsToAttributes, THERMAL_STORAGE, FIELDS_TO_VALUES_MAP));
      }
      if (!thermalBus.isPresent()) {
        sB.append("\nthermalBus: ")
            .append(saveMapGet(fieldsToAttributes, THERMAL_BUS, FIELDS_TO_VALUES_MAP));
      }

      logSkippingWarning(
          typedEntityData.getTargetClass().getSimpleName(),
          saveMapGet(fieldsToAttributes, "uuid", FIELDS_TO_VALUES_MAP),
          saveMapGet(fieldsToAttributes, "id", FIELDS_TO_VALUES_MAP),
          sB.toString());

      return Optional.empty();
    }

    // remove fields that are passed as objects to constructor
    fieldsToAttributes
        .keySet()
        .removeAll(new HashSet<>(Arrays.asList("thermalBus", "thermalStorage")));

    return Optional.of(
        new ChpInputEntityData(
            fieldsToAttributes,
            typedEntityData.getOperatorInput(),
            typedEntityData.getNode(),
            typedEntityData.getTypeInput(),
            thermalBus.get(),
            thermalStorage.get()));
  }

  @Override
  public Set<EmInput> getEmSystems() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getEmSystems(rawGridSource.getNodes(operators), operators);
  }

  /**
   * {@inheritDoc}
   *
   * <p>If the set of {@link NodeInput} entities is not exhaustive for all available {@link
   * LoadInput} entities (e.g. a {@link NodeInput} entity is missing) or if an error during the
   * building process occurs, the entity that misses something will be skipped (which can be seen as
   * a filtering functionality), but all entities that are able to be built will be returned anyway
   * and the elements that couldn't have been built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<EmInput> getEmSystems(Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return nodeAssetEntityStream(EmInput.class, emInputFactory, nodes, operators)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }



}
