/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.FailedValidationException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.SystemParticipantsException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.participant.*;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
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
import java.util.stream.Stream;

/**
 * Implementation that provides the capability to build entities of type {@link
 * SystemParticipantInput} as well as {@link SystemParticipants} container.
 */
public class SystemParticipantSource extends AssetEntitySource {

  private static final String THERMAL_STORAGE = "thermalstorage";
  private static final String THERMAL_BUS = "thermalbus";

  // general fields
  private final TypeSource typeSource;
  private final RawGridSource rawGridSource;
  private final ThermalSource thermalSource;
  private final EnergyManagementSource energyManagementSource;

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

  // enriching function
  protected static final TriEnrichFunction<
          EntityData, OperatorInput, NodeInput, EmInput, SystemParticipantEntityData>
      participantEnricher =
          (data, operators, nodes, emUnits) ->
              assetEnricher
                  .andThen(enrich(NODE, nodes, NodeAssetInputEntityData::new))
                  .andThen(
                      enrichWithDefault(
                          SystemParticipantInputEntityFactory.EM,
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
  }

  @Override
  public void validate() throws ValidationException {
    Try.scanStream(
            Stream.of(
                validate(BmInput.class, dataSource, bmInputFactory),
                validate(ChpInput.class, dataSource, chpInputFactory),
                validate(EvInput.class, dataSource, evInputFactory),
                validate(FixedFeedInInput.class, dataSource, fixedFeedInInputFactory),
                validate(HpInput.class, dataSource, hpInputFactory),
                validate(LoadInput.class, dataSource, loadInputFactory),
                validate(PvInput.class, dataSource, pvInputFactory),
                validate(StorageInput.class, dataSource, storageInputFactory),
                validate(WecInput.class, dataSource, wecInputFactory),
                validate(EvcsInput.class, dataSource, evcsInputFactory)),
            "Validation")
        .transformF(FailedValidationException::new)
        .getOrThrow();
  }

  /**
   * Should return either a consistent instance of {@link SystemParticipants} or throw a {@link
   * SourceException}. The decision to throw a {@link SourceException} instead of returning the
   * incomplete {@link SystemParticipants} instance is motivated by the fact, that a {@link
   * SystemParticipants} is a container instance that depends on several other entities. Without
   * being complete, it is useless for further processing.
   *
   * <p>Hence, whenever at least one entity {@link SystemParticipants} depends on cannot be
   * provided, {@link SourceException} should be thrown. The thrown exception should provide enough
   * information to debug the error and fix the persistent data that has been failed to processed.
   *
   * <p>Furthermore, it is expected, that the specific implementation of this method ensures not
   * only the completeness of the resulting {@link SystemParticipants} instance, but also its
   * validity e.g. in the sense that not duplicate UUIDs exist within all entities contained in the
   * returning instance.
   *
   * @return a valid, complete {@link SystemParticipants}
   * @throws SourceException on error
   */
  public SystemParticipants getSystemParticipants() throws SourceException {

    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, NodeInput> nodes = rawGridSource.getNodes(operators);

    return getSystemParticipants(operators, nodes);
  }

  /**
   * Should return either a consistent instance of {@link SystemParticipants} or throw a {@link
   * SourceException}. The decision to throw a {@link SourceException} instead of returning the
   * incomplete {@link SystemParticipants} instance is motivated by the fact, that a {@link
   * SystemParticipants} is a container instance that depends on several other entities. Without
   * being complete, it is useless for further processing.
   *
   * <p>Hence, whenever at least one entity {@link SystemParticipants} depends on cannot be
   * provided, {@link SourceException} should be thrown. The thrown exception should provide enough
   * information to debug the error and fix the persistent data that has been failed to processed.
   *
   * <p>Furthermore, it is expected, that the specific implementation of this method ensures not
   * only the completeness of the resulting {@link SystemParticipants} instance, but also its
   * validity e.g. in the sense that not duplicate UUIDs exist within all entities contained in the
   * returning instance.
   *
   * <p>In contrast to {@link #getSystemParticipants()}, this method provides the ability to pass in
   * already existing input objects that this method depends on. Doing so, already loaded operators
   * and nodes can be recycled to improve performance and prevent unnecessary loading operations.
   *
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @return a valid, complete {@link SystemParticipants}
   * @throws SourceException on error
   */
  public SystemParticipants getSystemParticipants(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes) throws SourceException {

    // read all needed entities
    /// start with types and operators
    Map<UUID, BmTypeInput> bmTypes = typeSource.getBmTypes();
    Map<UUID, ChpTypeInput> chpTypes = typeSource.getChpTypes();
    Map<UUID, EvTypeInput> evTypes = typeSource.getEvTypes();
    Map<UUID, HpTypeInput> hpTypes = typeSource.getHpTypes();
    Map<UUID, StorageTypeInput> storageTypes = typeSource.getStorageTypes();
    Map<UUID, WecTypeInput> wecTypes = typeSource.getWecTypes();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits();

    /// go on with the thermal assets
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
    Try<Set<EvInput>, SourceException> evs =
        Try.of(() -> getEvs(operators, nodes, emUnits, evTypes), SourceException.class);
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

    List<SourceException> exceptions =
        Try.getExceptions(
            List.of(
                fixedFeedInInputs,
                pvInputs,
                loads,
                bmInputs,
                storages,
                wecInputs,
                evs,
                evcs,
                chpInputs,
                hpInputs));

    if (!exceptions.isEmpty()) {
      throw new SystemParticipantsException(
          exceptions.size() + " error(s) occurred while initializing system participants. ",
          exceptions);
    } else {
      // if everything is fine, return a system participants container
      // getOrThrow should not throw an exception in this context, because all exception are
      // filtered and thrown before
      return new SystemParticipants(
          bmInputs.getOrThrow(),
          chpInputs.getOrThrow(),
          evcs.getOrThrow(),
          evs.getOrThrow(),
          fixedFeedInInputs.getOrThrow(),
          hpInputs.getOrThrow(),
          loads.getOrThrow(),
          pvInputs.getOrThrow(),
          storages.getOrThrow(),
          wecInputs.getOrThrow());
    }
  }

  /**
   * Returns a unique set of {@link FixedFeedInInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link FixedFeedInInput} which has to be checked
   * manually, as {@link FixedFeedInInput#equals(Object)} is NOT restricted on the uuid of {@link
   * FixedFeedInInput}.
   *
   * @return a set of object- and uuid-unique {@link FixedFeedInInput} entities
   */
  public Set<FixedFeedInInput> getFixedFeedIns() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    return getFixedFeedIns(operators, rawGridSource.getNodes(operators), emUnits);
  }

  /**
   * Returns a set of {@link FixedFeedInInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link FixedFeedInInput} which has to be checked manually, as {@link
   * FixedFeedInInput#equals(Object)} is NOT restricted on the uuid of {@link FixedFeedInInput}.
   *
   * <p>In contrast to {@link #getFixedFeedIns()} this method provides the ability to pass in an
   * already existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link
   * FixedFeedInInput} instances depend on. Doing so, already loaded nodes can be recycled to
   * improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @param emUnits a map of UUID to object- and uuid-unique {@link EmInput} entities
   * @return a set of object- and uuid-unique {@link FixedFeedInInput} entities
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
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link PvInput} which has to be checked manually, as
   * {@link PvInput#equals(Object)} is NOT restricted on the uuid of {@link PvInput}.
   *
   * @return a set of object- and uuid-unique {@link PvInput} entities
   */
  public Set<PvInput> getPvPlants() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    return getPvPlants(operators, rawGridSource.getNodes(operators), emUnits);
  }

  /**
   * Returns a set of {@link PvInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * PvInput} which has to be checked manually, as {@link PvInput#equals(Object)} is NOT restricted
   * on the uuid of {@link PvInput}.
   *
   * <p>In contrast to {@link #getPvPlants()} this method provides the ability to pass in an already
   * existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link PvInput}
   * instances depend on. Doing so, already loaded nodes can be recycled to improve performance and
   * prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @param emUnits a map of UUID to object- and uuid-unique {@link EmInput} entities
   * @return a set of object- and uuid-unique {@link PvInput} entities
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
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link LoadInput} which has to be checked manually,
   * as {@link LoadInput#equals(Object)} is NOT restricted on the uuid of {@link LoadInput}.
   *
   * @return a set of object- and uuid-unique {@link LoadInput} entities
   */
  public Set<LoadInput> getLoads() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    return getLoads(operators, rawGridSource.getNodes(operators), emUnits);
  }

  /**
   * Returns a set of {@link LoadInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * LoadInput} which has to be checked manually, as {@link LoadInput#equals(Object)} is NOT
   * restricted on the uuid of {@link LoadInput}.
   *
   * <p>In contrast to {@link #getLoads()} this method provides the ability to pass in an already
   * existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link LoadInput}
   * instances depend on. Doing so, already loaded nodes can be recycled to improve performance and
   * prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @param emUnits a map of UUID to object- and uuid-unique {@link EmInput} entities
   * @return a set of object- and uuid-unique {@link LoadInput} entities
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
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EvcsInput} which has to be checked manually,
   * as {@link EvcsInput#equals(Object)} is NOT restricted on the uuid of {@link EvcsInput}.
   *
   * @return a set of object- and uuid-unique {@link EvcsInput} entities
   */
  public Set<EvcsInput> getEvcs() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    return getEvcs(operators, rawGridSource.getNodes(operators), emUnits);
  }

  /**
   * Returns a set of {@link EvcsInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * EvcsInput} which has to be checked manually, as {@link EvcsInput#equals(Object)} is NOT
   * restricted on the uuid of {@link EvcsInput}.
   *
   * <p>In contrast to {@link #getEvcs()} this method provides the ability to pass in an already
   * existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link EvcsInput}
   * instances depend on. Doing so, already loaded nodes can be recycled to improve performance and
   * prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @param emUnits a map of UUID to object- and uuid-unique {@link EmInput} entities
   * @return a set of object- and uuid-unique {@link EvcsInput} entities
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
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link BmInput} which has to be checked manually, as
   * {@link BmInput#equals(Object)} is NOT restricted on the uuid of {@link BmInput}.
   *
   * @return a set of object- and uuid-unique {@link BmInput} entities
   */
  public Set<BmInput> getBmPlants() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    return getBmPlants(
        operators, rawGridSource.getNodes(operators), emUnits, typeSource.getBmTypes());
  }

  /**
   * Returns a set of {@link BmInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * BmInput} which has to be checked manually, as {@link BmInput#equals(Object)} is NOT restricted
   * on the uuid of {@link BmInput}.
   *
   * <p>In contrast to {@link #getBmPlants()} this method provides the ability to pass in an already
   * existing set of {@link NodeInput}, {@link BmTypeInput} and {@link OperatorInput} entities, the
   * {@link BmInput} instances depend on. Doing so, already loaded nodes can be recycled to improve
   * performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @param emUnits a map of UUID to object- and uuid-unique {@link EmInput} entities
   * @param types a map of UUID to object- and uuid-unique {@link BmTypeInput} entities
   * @return a set of object- and uuid-unique {@link BmInput} entities
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
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link StorageInput} which has to be checked
   * manually, as {@link StorageInput#equals(Object)} is NOT restricted on the uuid of {@link
   * StorageInput}.
   *
   * @return a set of object- and uuid-unique {@link StorageInput} entities
   */
  public Set<StorageInput> getStorages() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    return getStorages(
        operators, rawGridSource.getNodes(operators), emUnits, typeSource.getStorageTypes());
  }

  /**
   * Returns a set of {@link StorageInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link StorageInput} which has to be checked manually, as {@link StorageInput#equals(Object)}
   * is NOT restricted on the uuid of {@link StorageInput}.
   *
   * <p>In contrast to {@link #getStorages()} this method provides the ability to pass in an already
   * existing set of {@link NodeInput}, {@link StorageTypeInput} and {@link OperatorInput} entities,
   * the {@link StorageInput} instances depend on. Doing so, already loaded nodes can be recycled to
   * improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} that should be
   *     used for the returning instances
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @param emUnits a map of UUID to object- and uuid-unique {@link EmInput} entities
   * @param types a map of UUID to object- and uuid-unique {@link StorageTypeInput} entities
   * @return a set of object- and uuid-unique {@link StorageInput} entities
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
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link WecInput} which has to be checked manually,
   * as {@link WecInput#equals(Object)} is NOT restricted on the uuid of {@link WecInput}.
   *
   * @return a set of object- and uuid-unique {@link WecInput} entities
   */
  public Set<WecInput> getWecPlants() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    return getWecPlants(
        operators, rawGridSource.getNodes(operators), emUnits, typeSource.getWecTypes());
  }

  /**
   * Returns a set of {@link WecInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * WecInput} which has to be checked manually, as {@link WecInput#equals(Object)} is NOT
   * restricted on the uuid of {@link WecInput}.
   *
   * <p>In contrast to {@link #getWecPlants()} this method provides the ability to pass in an
   * already existing set of {@link NodeInput}, {@link WecTypeInput} and {@link OperatorInput}
   * entities, the {@link WecInput} instances depend on. Doing so, already loaded nodes can be
   * recycled to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @param emUnits a map of UUID to object- and uuid-unique {@link EmInput} entities
   * @param types a map of UUID to object- and uuid-unique {@link WecTypeInput} entities
   * @return a set of object- and uuid-unique {@link WecInput} entities
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
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EvInput} which has to be checked manually, as
   * {@link EvInput#equals(Object)} is NOT restricted on the uuid of {@link EvInput}.
   *
   * @return a set of object- and uuid-unique {@link EvInput} entities
   */
  public Set<EvInput> getEvs() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, EmInput> emUnits = energyManagementSource.getEmUnits(operators);
    return getEvs(operators, rawGridSource.getNodes(operators), emUnits, typeSource.getEvTypes());
  }

  /**
   * Returns a set of {@link EvInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * EvInput} which has to be checked manually, as {@link EvInput#equals(Object)} is NOT restricted
   * on the uuid of {@link EvInput}.
   *
   * <p>In contrast to {@link #getEvs()} this method provides the ability to pass in an already
   * existing set of {@link NodeInput}, {@link EvTypeInput} and {@link OperatorInput} entities, the
   * {@link EvInput} instances depend on. Doing so, already loaded nodes can be recycled to improve
   * performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @param emUnits a map of UUID to object- and uuid-unique {@link EmInput} entities
   * @param types a map of UUID to object- and uuid-unique {@link EvTypeInput} entities
   * @return a set of object- and uuid-unique {@link EvInput} entities
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
   * If one of the sets of {@link NodeInput}, {@link ThermalBusInput}, {@link ThermalStorageInput}
   * or {@link ChpTypeInput} entities is not exhaustive for all available {@link ChpInput} entities
   * (e.g. a {@link NodeInput} or {@link ChpTypeInput} entity is missing) or if an error during the
   * building process occurs a {@link SourceException} is thrown, else all entities that are able to
   * be built will be returned.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   *
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @param emUnits a map of UUID to object- and uuid-unique {@link EmInput} entities
   * @param types a map of UUID to object- and uuid-unique {@link ChpTypeInput} entities
   * @param thermalBuses a map of UUID to object- and uuid-unique {@link ThermalBusInput} entities
   * @param thermalStorages a map of UUID to object- and uuid-unique {@link ThermalStorageInput}
   *     entities
   * @return a set of object- and uuid-unique {@link ChpInput} entities
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
   * If one of the sets of {@link NodeInput}, {@link ThermalBusInput} or {@link HpTypeInput}
   * entities is not exhaustive for all available {@link HpInput} entities (e.g. a {@link NodeInput}
   * or {@link HpTypeInput} entity is missing) or if an error during the building process occurs a
   * {@link SourceException} is thrown, else all entities that are able to be built will be
   * returned.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   *
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @param emUnits a map of UUID to object- and uuid-unique {@link EmInput} entities
   * @param types a map of UUID to object- and uuid-unique {@link HpTypeInput} entities
   * @param thermalBuses a map of UUID to object- and uuid-unique {@link ThermalBusInput} entities
   * @return a set of object- and uuid-unique {@link HpInput} entities
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

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
   * Builds a function for enriching {@link SystemParticipantEntityData} with types.
   *
   * @param types all known types
   * @return a typed entity data
   * @param <T> type of types
   */
  private static <T extends SystemParticipantTypeInput, D extends SystemParticipantEntityData>
      WrappedFunction<D, SystemParticipantTypedEntityData<T>> enrichTypes(Map<UUID, T> types) {
    BiFunction<D, T, SystemParticipantTypedEntityData<T>> typeEnricher =
        SystemParticipantTypedEntityData::new;
    return entityData -> enrich(TYPE, types, typeEnricher).apply(entityData);
  }
}
