/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.SystemParticipantsException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.participant.*;
import edu.ie3.datamodel.io.naming.FieldNamingStrategy;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.container.ExtendedSystemParticipants;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import edu.ie3.datamodel.utils.Try;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * Provides the capability to build {@link SystemParticipantInput} entities and the {@link
 * SystemParticipants} and {@link ExtendedSystemParticipants} containers.
 */
public class SystemParticipantSource extends AssetEntitySource {

  private static final String THERMAL_STORAGE = "thermalstorage";
  private static final String THERMAL_BUS = "thermalbus";

  // General fields
  private final TypeSource typeSource;
  private final RawGridSource rawGridSource;
  private final ThermalSource thermalSource;
  private final EnergyManagementSource energyManagementSource;

  // Factories
  private final AcInputFactory acInputFactory;
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

  // Enriching function
  protected static final TriEnrichFunction<
          EntityData, OperatorInput, NodeInput, EmInput, SystemParticipantEntityData>
      participantEnricher =
          (data, operators, nodes, emUnits) ->
              assetEnricher
                  .andThen(enrich(NODE, nodes, NodeAssetInputEntityData::new))
                  .andThen(
                      enrichWithDefault(
                          FieldNamingStrategy.CONTROLLING_EM,
                          emUnits,
                          null,
                          SystemParticipantEntityData::new))
                  .apply(data, operators);

  public SystemParticipantSource(
      TypeSource typeSource,
      ThermalSource thermalSource,
      RawGridSource rawGridSource,
      EnergyManagementSource energyManagementSource,
      DataSource dataSource) {
    super(dataSource);

    this.typeSource = typeSource;
    this.rawGridSource = rawGridSource;
    this.thermalSource = thermalSource;
    this.energyManagementSource = energyManagementSource;

    this.acInputFactory = new AcInputFactory();
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
  }

  @Override
  public void validate() throws ValidationException {
    validate(
        dataSource,
        AcInput.class,
        BmInput.class,
        ChpInput.class,
        EvInput.class,
        FixedFeedInInput.class,
        HpInput.class,
        LoadInput.class,
        PvInput.class,
        StorageInput.class,
        WecInput.class,
        EvcsInput.class);
  }

  /**
   * Loads electric vehicle participants and returns them as a {@link SystemParticipants} container.
   *
   * <p>This container contains only {@link EvInput} entities. Other participant types are provided
   * by {@link #getExtendedSystemParticipants()}.
   *
   * @return a complete container of electric vehicle participants
   * @throws SourceException if the EV participants or their required dependencies cannot be loaded
   */
  public SystemParticipants getSystemParticipants() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, NodeInput> nodes = rawGridSource.getNodes(operators);

    return getSystemParticipants(operators, nodes);
  }

  /**
   * Builds a {@link SystemParticipants} container from the provided operators and nodes.
   *
   * <p>This container contains only {@link EvInput} entities. Other participant types are provided
   * by {@link #getExtendedSystemParticipants(Map, Map)}.
   *
   * @param operators known operators
   * @param nodes known nodes
   * @return a complete container of electric vehicle participants
   * @throws SourceException if the EV participants cannot be loaded consistently
   */
  public SystemParticipants getSystemParticipants(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes) throws SourceException {

    Map<UUID, EvTypeInput> evTypes = typeSource.getEvTypes();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits();

    Try<Set<EvInput>, SourceException> evs =
        Try.of(() -> getEvs(operators, nodes, emUnits, evTypes), SourceException.class);

    List<SourceException> exceptions = Try.getExceptions(evs);

    if (!exceptions.isEmpty()) {
      throw new SystemParticipantsException(
          "Exception(s) occurred in "
              + exceptions.size()
              + " input file(s) while initializing system participants.",
          exceptions);
    } else {
      return new SystemParticipants(evs.getOrThrow());
    }
  }

  /**
   * Loads non-EV system participants and returns them as an {@link ExtendedSystemParticipants}
   * container.
   *
   * <p>This container contains the supported participant types other than {@link EvInput}. Electric
   * vehicle participants are provided by {@link #getSystemParticipants()}.
   *
   * @return a complete container of extended system participants
   * @throws SourceException if one or more participant types or their required dependencies cannot
   *     be loaded
   */
  public ExtendedSystemParticipants getExtendedSystemParticipants() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, NodeInput> nodes = rawGridSource.getNodes(operators);

    return getExtendedSystemParticipants(operators, nodes);
  }

  /**
   * Builds an {@link ExtendedSystemParticipants} container from the provided operators and nodes.
   *
   * <p>This container contains the supported participant types other than {@link EvInput}. Electric
   * vehicle participants are provided by {@link #getSystemParticipants(Map, Map)}.
   *
   * @param operators known operators
   * @param nodes known nodes
   * @return a complete container of extended system participants
   * @throws SourceException if one or more participant types cannot be loaded consistently
   */
  public ExtendedSystemParticipants getExtendedSystemParticipants(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes) throws SourceException {

    // Load the required participant types
    Map<UUID, BmTypeInput> bmTypes = typeSource.getBmTypes();
    Map<UUID, ChpTypeInput> chpTypes = typeSource.getChpTypes();
    Map<UUID, HpTypeInput> hpTypes = typeSource.getHpTypes();
    Map<UUID, AcTypeInput> acTypes = typeSource.getAcTypes();
    Map<UUID, StorageTypeInput> storageTypes = typeSource.getStorageTypes();
    Map<UUID, WecTypeInput> wecTypes = typeSource.getWecTypes();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits();

    // Load the required thermal dependencies
    Map<UUID, ThermalBusInput> thermalBuses = thermalSource.getThermalBuses(operators);
    Map<UUID, ThermalStorageInput> thermalStorages =
        thermalSource.getThermalStorages(operators, thermalBuses);

    Try<Set<FixedFeedInInput>, SourceException> fixedFeedInInputs =
        Try.of(() -> getFixedFeedIns(operators, nodes, emUnits), SourceException.class);
    Try<Set<PvInput>, SourceException> pvInputs =
        Try.of(() -> getPvPlants(operators, nodes, emUnits), SourceException.class);
    Try<Set<LoadInput>, SourceException> loads =
        Try.of(() -> getLoads(operators, nodes, emUnits), SourceException.class);
    Try<Set<BmInput>, SourceException> bmInputs =
        Try.of(() -> getBmPlants(operators, nodes, emUnits, bmTypes), SourceException.class);
    Try<Set<StorageInput>, SourceException> storages =
        Try.of(() -> getStorages(operators, nodes, emUnits, storageTypes), SourceException.class);
    Try<Set<WecInput>, SourceException> wecInputs =
        Try.of(() -> getWecPlants(operators, nodes, emUnits, wecTypes), SourceException.class);
    Try<Set<EvcsInput>, SourceException> evcs =
        Try.of(() -> getEvcs(operators, nodes, emUnits), SourceException.class);
    Try<Set<ChpInput>, SourceException> chpInputs =
        Try.of(
            () -> getChpPlants(operators, nodes, emUnits, chpTypes, thermalBuses, thermalStorages),
            SourceException.class);
    Try<Set<HpInput>, SourceException> hpInputs =
        Try.of(
            () -> getHeatPumps(operators, nodes, emUnits, hpTypes, thermalBuses),
            SourceException.class);
    Try<Set<AcInput>, SourceException> acInputs =
        Try.of(
            () -> getAirConditions(operators, nodes, emUnits, acTypes, thermalBuses),
            SourceException.class);

    List<SourceException> exceptions =
        Try.getExceptions(
            fixedFeedInInputs,
            pvInputs,
            loads,
            bmInputs,
            storages,
            wecInputs,
            evcs,
            chpInputs,
            hpInputs,
            acInputs);

    if (!exceptions.isEmpty()) {
      throw new SystemParticipantsException(
          "Exception(s) occurred in "
              + exceptions.size()
              + " input file(s) while initializing extended system participants.",
          exceptions);
    } else {
      return new ExtendedSystemParticipants(
          bmInputs.getOrThrow(),
          chpInputs.getOrThrow(),
          evcs.getOrThrow(),
          fixedFeedInInputs.getOrThrow(),
          hpInputs.getOrThrow(),
          acInputs.getOrThrow(),
          loads.getOrThrow(),
          pvInputs.getOrThrow(),
          storages.getOrThrow(),
          wecInputs.getOrThrow());
    }
  }

  /**
   * Returns a unique set of {@link FixedFeedInInput} instances.
   *
   * <p>The set is unique both by object identity and by UUID. UUID uniqueness has to be checked
   * manually because {@link FixedFeedInInput#equals(Object)} is not restricted to the entity UUID.
   *
   * @return a set of object- and UUID-unique fixed feed-in entities
   */
  public Set<FixedFeedInInput> getFixedFeedIns() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    return getFixedFeedIns(operators, rawGridSource.getNodes(operators), emUnits);
  }

  /**
   * Returns fixed feed-in entities using the provided operators, nodes and energy management units.
   *
   * @param operators known operators
   * @param nodes known nodes
   * @param emUnits known energy management units
   * @return a set of object- and UUID-unique fixed feed-in entities
   */
  public Set<FixedFeedInInput> getFixedFeedIns(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes, Map<UUID, EmInput> emUnits)
      throws SourceException {
    return getEntities(
            FixedFeedInInput.class,
            dataSource,
            fixedFeedInInputFactory,
            data -> participantEnricher.apply(data, operators, nodes, emUnits))
        .collect(toSet());
  }

  /**
   * Returns a unique set of {@link PvInput} instances.
   *
   * @return a set of object- and UUID-unique PV entities
   */
  public Set<PvInput> getPvPlants() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    return getPvPlants(operators, rawGridSource.getNodes(operators), emUnits);
  }

  /**
   * Returns PV entities using the provided operators, nodes and energy management units.
   *
   * @param operators known operators
   * @param nodes known nodes
   * @param emUnits known energy management units
   * @return a set of object- and UUID-unique PV entities
   */
  public Set<PvInput> getPvPlants(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes, Map<UUID, EmInput> emUnits)
      throws SourceException {
    return getEntities(
            PvInput.class,
            dataSource,
            pvInputFactory,
            data -> participantEnricher.apply(data, operators, nodes, emUnits))
        .collect(toSet());
  }

  /**
   * Returns a unique set of {@link LoadInput} instances.
   *
   * @return a set of object- and UUID-unique load entities
   */
  public Set<LoadInput> getLoads() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    return getLoads(operators, rawGridSource.getNodes(operators), emUnits);
  }

  /**
   * Returns load entities using the provided operators, nodes and energy management units.
   *
   * @param operators known operators
   * @param nodes known nodes
   * @param emUnits known energy management units
   * @return a set of object- and UUID-unique load entities
   */
  public Set<LoadInput> getLoads(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes, Map<UUID, EmInput> emUnits)
      throws SourceException {
    return getEntities(
            LoadInput.class,
            dataSource,
            loadInputFactory,
            data -> participantEnricher.apply(data, operators, nodes, emUnits))
        .collect(toSet());
  }

  /**
   * Returns a unique set of {@link EvcsInput} instances.
   *
   * @return a set of object- and UUID-unique EV charging station entities
   */
  public Set<EvcsInput> getEvcs() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    return getEvcs(operators, rawGridSource.getNodes(operators), emUnits);
  }

  /**
   * Returns EV charging station entities using the provided operators, nodes and energy management
   * units.
   *
   * @param operators known operators
   * @param nodes known nodes
   * @param emUnits known energy management units
   * @return a set of object- and UUID-unique EV charging station entities
   */
  public Set<EvcsInput> getEvcs(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes, Map<UUID, EmInput> emUnits)
      throws SourceException {
    return getEntities(
            EvcsInput.class,
            dataSource,
            evcsInputFactory,
            data -> participantEnricher.apply(data, operators, nodes, emUnits))
        .collect(toSet());
  }

  /**
   * Returns a unique set of {@link BmInput} instances.
   *
   * @return a set of object- and UUID-unique biomass plant entities
   */
  public Set<BmInput> getBmPlants() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    return getBmPlants(
        operators, rawGridSource.getNodes(operators), emUnits, typeSource.getBmTypes());
  }

  /**
   * Returns biomass plant entities using the provided operators, nodes, energy management units and
   * types.
   *
   * @param operators known operators
   * @param nodes known nodes
   * @param emUnits known energy management units
   * @param types known biomass plant types
   * @return a set of object- and UUID-unique biomass plant entities
   */
  public Set<BmInput> getBmPlants(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, BmTypeInput> types)
      throws SourceException {
    return getEntities(
            BmInput.class,
            dataSource,
            bmInputFactory,
            data ->
                participantEnricher
                    .andThen(enrichTypes(types))
                    .apply(data, operators, nodes, emUnits))
        .collect(toSet());
  }

  /**
   * Returns a unique set of {@link StorageInput} instances.
   *
   * @return a set of object- and UUID-unique storage entities
   */
  public Set<StorageInput> getStorages() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    return getStorages(
        operators, rawGridSource.getNodes(operators), emUnits, typeSource.getStorageTypes());
  }

  /**
   * Returns storage entities using the provided operators, nodes, energy management units and
   * types.
   *
   * @param operators known operators
   * @param nodes known nodes
   * @param emUnits known energy management units
   * @param types known storage types
   * @return a set of object- and UUID-unique storage entities
   */
  public Set<StorageInput> getStorages(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, StorageTypeInput> types)
      throws SourceException {
    return getEntities(
            StorageInput.class,
            dataSource,
            storageInputFactory,
            data ->
                participantEnricher
                    .andThen(enrichTypes(types))
                    .apply(data, operators, nodes, emUnits))
        .collect(toSet());
  }

  /**
   * Returns a unique set of {@link WecInput} instances.
   *
   * @return a set of object- and UUID-unique WEC entities
   */
  public Set<WecInput> getWecPlants() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    return getWecPlants(
        operators, rawGridSource.getNodes(operators), emUnits, typeSource.getWecTypes());
  }

  /**
   * Returns WEC entities using the provided operators, nodes, energy management units and types.
   *
   * @param operators known operators
   * @param nodes known nodes
   * @param emUnits known energy management units
   * @param types known WEC types
   * @return a set of object- and UUID-unique WEC entities
   */
  public Set<WecInput> getWecPlants(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, WecTypeInput> types)
      throws SourceException {
    return getEntities(
            WecInput.class,
            dataSource,
            wecInputFactory,
            data ->
                participantEnricher
                    .andThen(enrichTypes(types))
                    .apply(data, operators, nodes, emUnits))
        .collect(toSet());
  }

  /**
   * Returns a unique set of {@link EvInput} instances.
   *
   * @return a set of object- and UUID-unique electric vehicle entities
   */
  public Set<EvInput> getEvs() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    return getEvs(operators, rawGridSource.getNodes(operators), emUnits, typeSource.getEvTypes());
  }

  /**
   * Returns electric vehicle entities using the provided operators, nodes, energy management units
   * and types.
   *
   * @param operators known operators
   * @param nodes known nodes
   * @param emUnits known energy management units
   * @param types known EV types
   * @return a set of object- and UUID-unique electric vehicle entities
   */
  public Set<EvInput> getEvs(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, EvTypeInput> types)
      throws SourceException {
    return getEntities(
            EvInput.class,
            dataSource,
            evInputFactory,
            data ->
                participantEnricher
                    .andThen(enrichTypes(types))
                    .apply(data, operators, nodes, emUnits))
        .collect(toSet());
  }

  public Set<ChpInput> getChpPlants() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    Map<UUID, ThermalBusInput> thermalBuses = thermalSource.getThermalBuses(operators);

    return getChpPlants(
        operators,
        rawGridSource.getNodes(operators),
        emUnits,
        typeSource.getChpTypes(),
        thermalBuses,
        thermalSource.getThermalStorages(operators, thermalBuses));
  }

  /**
   * Returns CHP entities using the provided operators, nodes, energy management units, types and
   * thermal dependencies.
   *
   * @param operators known operators
   * @param nodes known nodes
   * @param emUnits known energy management units
   * @param types known CHP types
   * @param thermalBuses known thermal buses
   * @param thermalStorages known thermal storages
   * @return a set of object- and UUID-unique CHP entities
   */
  public Set<ChpInput> getChpPlants(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, ChpTypeInput> types,
      Map<UUID, ThermalBusInput> thermalBuses,
      Map<UUID, ThermalStorageInput> thermalStorages)
      throws SourceException {

    WrappedFunction<EntityData, ChpInputEntityData> builder =
        data ->
            participantEnricher
                .andThen(enrichTypes(types))
                .andThen(
                    biEnrich(
                        THERMAL_BUS,
                        thermalBuses,
                        THERMAL_STORAGE,
                        thermalStorages,
                        ChpInputEntityData::new))
                .apply(data, operators, nodes, emUnits);

    return getEntities(ChpInput.class, dataSource, chpInputFactory, builder).collect(toSet());
  }

  public Set<HpInput> getHeatPumps() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);

    return getHeatPumps(
        operators,
        rawGridSource.getNodes(operators),
        emUnits,
        typeSource.getHpTypes(),
        thermalSource.getThermalBuses());
  }

  /**
   * Returns heat pump entities using the provided operators, nodes, energy management units, types
   * and thermal buses.
   *
   * @param operators known operators
   * @param nodes known nodes
   * @param emUnits known energy management units
   * @param types known heat pump types
   * @param thermalBuses known thermal buses
   * @return a set of object- and UUID-unique heat pump entities
   */
  public Set<HpInput> getHeatPumps(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, HpTypeInput> types,
      Map<UUID, ThermalBusInput> thermalBuses)
      throws SourceException {

    WrappedFunction<EntityData, HpInputEntityData> builder =
        data ->
            participantEnricher
                .andThen(enrichTypes(types))
                .andThen(enrich(THERMAL_BUS, thermalBuses, HpInputEntityData::new))
                .apply(data, operators, nodes, emUnits);

    return getEntities(HpInput.class, dataSource, hpInputFactory, builder).collect(toSet());
  }

  /**
   * Returns air conditioning entities using the provided operators, nodes, energy management units,
   * types and thermal buses.
   *
   * @param operators known operators
   * @param nodes known nodes
   * @param emUnits known energy management units
   * @param types known air conditioning types
   * @param thermalBuses known thermal buses
   * @return a set of object- and UUID-unique air conditioning entities
   */
  public Set<AcInput> getAirConditions(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, AcTypeInput> types,
      Map<UUID, ThermalBusInput> thermalBuses)
      throws SourceException {

    WrappedFunction<EntityData, AcInputEntityData> builder =
        data ->
            participantEnricher
                .andThen(enrichTypes(types))
                .andThen(enrich(THERMAL_BUS, thermalBuses, AcInputEntityData::new))
                .apply(data, operators, nodes, emUnits);

    return getEntities(AcInput.class, dataSource, acInputFactory, builder).collect(toSet());
  }

  /**
   * Builds a function for enriching {@link SystemParticipantEntityData} with types.
   *
   * @param types all known types
   * @return a function that creates typed entity data
   * @param <T> type of the participant type entities
   * @param <D> type of the participant entity data
   */
  private static <T extends SystemParticipantTypeInput, D extends SystemParticipantEntityData>
      WrappedFunction<D, SystemParticipantTypedEntityData<T>> enrichTypes(Map<UUID, T> types) {
    BiFunction<D, T, SystemParticipantTypedEntityData<T>> typeEnricher =
        SystemParticipantTypedEntityData::new;
    return entityData -> enrich(TYPE, types, typeEnricher).apply(entityData);
  }
}
