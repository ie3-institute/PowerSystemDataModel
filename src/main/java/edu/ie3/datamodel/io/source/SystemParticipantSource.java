/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.factory.input.participant.*;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Interface that provides the capability to build entities of type {@link SystemParticipantInput}
 * as well as {@link SystemParticipants} container from .csv files.
 *
 * @version 0.1
 * @since 08.04.20
 */
public class SystemParticipantSource implements DataSource {

  private static final String THERMAL_STORAGE = "thermalstorage";
  private static final String THERMAL_BUS = "thermalbus";

  // general fields
  TypeSource typeSource;
  RawGridSource rawGridSource;
  ThermalSource thermalSource;
  FunctionalDataSource dataSource;

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

  public SystemParticipantSource(
          TypeSource typeSource,
          ThermalSource thermalSource,
          RawGridSource rawGridSource,
          FunctionalDataSource dataSource) {

    this.typeSource = typeSource;
    this.rawGridSource = rawGridSource;
    this.thermalSource = thermalSource;
    this.dataSource = dataSource;

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


  /**
   * Should return either a consistent instance of {@link SystemParticipants} wrapped in {@link
   * Optional} or an empty {@link Optional}. The decision to use {@link Optional} instead of
   * returning the {@link SystemParticipants} instance directly is motivated by the fact, that a
   * {@link SystemParticipants} is a container instance that depends on several other entities.
   * Without being complete, it is useless for further processing.
   *
   * <p>Hence, whenever at least one entity {@link SystemParticipants} depends on cannot be
   * provided, {@link Optional#empty()} should be returned and extensive logging should provide
   * enough information to debug the error and fix the persistent data that has been failed to
   * processed.
   *
   * <p>Furthermore, it is expected, that the specific implementation of this method ensures not
   * only the completeness of the resulting {@link SystemParticipants} instance, but also its
   * validity e.g. in the sense that not duplicate UUIDs exist within all entities contained in the
   * returning instance.
   *
   * @return either a valid, complete {@link SystemParticipants} optional or {@link
   *     Optional#empty()}
   */
  public Optional<SystemParticipants> getSystemParticipants() {
    return null;
    /*

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
     */
  }

  /**
   * Returns a unique set of {@link FixedFeedInInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link FixedFeedInInput} which has to be checked
   * manually, as {@link FixedFeedInInput#equals(Object)} is NOT restricted on the uuid of {@link
   * FixedFeedInInput}.
   *
   * @return a set of object and uuid unique {@link FixedFeedInInput} entities
   */
  public Set<FixedFeedInInput> getFixedFeedIns() {
    return null;
    /*
    Set<OperatorInput> operators = typeSource.getOperators();
    return getFixedFeedIns(rawGridSource.getNodes(operators), operators);
     */
  }

  /**
   * Returns a set of {@link FixedFeedInInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link FixedFeedInInput} which has to be checked manually, as {@link
   * FixedFeedInInput#equals(Object)} is NOT restricted on the uuid of {@link FixedFeedInInput}.
   *
   * <p>In contrast to {@link #getFixedFeedIns()} this interface provides the ability to pass in an
   * already existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link
   * FixedFeedInInput} instances depend on. Doing so, already loaded nodes can be recycled to
   * improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link FixedFeedInInput} entities
   */
  public Set<FixedFeedInInput> getFixedFeedIns(Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return null;
    /*
    return nodeAssetEntityStream(FixedFeedInInput.class, fixedFeedInInputFactory, nodes, operators)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());

     */
  }

  /**
   * Returns a unique set of {@link PvInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link PvInput} which has to be checked manually, as
   * {@link PvInput#equals(Object)} is NOT restricted on the uuid of {@link PvInput}.
   *
   * @return a set of object and uuid unique {@link PvInput} entities
   */
  public Set<PvInput> getPvPlants() {
    return null;
    /*
    Set<OperatorInput> operators = typeSource.getOperators();
    return getPvPlants(rawGridSource.getNodes(operators), operators);
     */
  }

  /**
   * Returns a set of {@link PvInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * PvInput} which has to be checked manually, as {@link PvInput#equals(Object)} is NOT restricted
   * on the uuid of {@link PvInput}.
   *
   * <p>In contrast to {@link #getPvPlants()} this interface provides the ability to pass in an
   * already existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link
   * PvInput} instances depend on. Doing so, already loaded nodes can be recycled to improve
   * performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link PvInput} entities
   */
  public Set<PvInput> getPvPlants(Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return null;
    /*
    return nodeAssetEntityStream(PvInput.class, pvInputFactory, nodes, operators)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
     */
  }

  /**
   * Returns a unique set of {@link LoadInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link LoadInput} which has to be checked manually,
   * as {@link LoadInput#equals(Object)} is NOT restricted on the uuid of {@link LoadInput}.
   *
   * @return a set of object and uuid unique {@link LoadInput} entities
   */
  public Set<LoadInput> getLoads() {
    return null;
    /*
    Set<OperatorInput> operators = typeSource.getOperators();
    return getLoads(rawGridSource.getNodes(operators), operators);
     */
  }

  /**
   * Returns a set of {@link LoadInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * LoadInput} which has to be checked manually, as {@link LoadInput#equals(Object)} is NOT
   * restricted on the uuid of {@link LoadInput}.
   *
   * <p>In contrast to {@link #getLoads()} this interface provides the ability to pass in an already
   * existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link LoadInput}
   * instances depend on. Doing so, already loaded nodes can be recycled to improve performance and
   * prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link LoadInput} entities
   */
  public Set<LoadInput> getLoads(Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return null;
    /*
    return nodeAssetEntityStream(LoadInput.class, loadInputFactory, nodes, operators)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
     */
  }

  /**
   * Returns a unique set of {@link EvcsInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EvcsInput} which has to be checked manually,
   * as {@link EvcsInput#equals(Object)} is NOT restricted on the uuid of {@link EvcsInput}.
   *
   * @return a set of object and uuid unique {@link EvcsInput} entities
   */
  public Set<EvcsInput> getEvCS() {
    return null;
    /*
    Set<OperatorInput> operators = typeSource.getOperators();
    return getEvCS(rawGridSource.getNodes(operators), operators);
     */
  }

  /**
   * Returns a set of {@link EvcsInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * EvcsInput} which has to be checked manually, as {@link EvcsInput#equals(Object)} is NOT
   * restricted on the uuid of {@link EvcsInput}.
   *
   * <p>In contrast to {@link #getEvCS()} this interface provides the ability to pass in an already
   * existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link EvcsInput}
   * instances depend on. Doing so, already loaded nodes can be recycled to improve performance and
   * prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link EvcsInput} entities
   */
  public Set<EvcsInput> getEvCS(Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return null;
    /*
    return nodeAssetEntityStream(EvcsInput.class, evcsInputFactory, nodes, operators)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());

     */
  }

  /**
   * Returns a unique set of {@link BmInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link BmInput} which has to be checked manually, as
   * {@link BmInput#equals(Object)} is NOT restricted on the uuid of {@link BmInput}.
   *
   * @return a set of object and uuid unique {@link BmInput} entities
   */
  public Set<BmInput> getBmPlants() {
    return null;
    /*
    Set<OperatorInput> operators = typeSource.getOperators();
    return getBmPlants(rawGridSource.getNodes(operators), operators, typeSource.getBmTypes());

     */
  }

  /**
   * Returns a set of {@link BmInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * BmInput} which has to be checked manually, as {@link BmInput#equals(Object)} is NOT restricted
   * on the uuid of {@link BmInput}.
   *
   * <p>In contrast to {@link #getBmPlants()} this interface provides the ability to pass in an
   * already existing set of {@link NodeInput}, {@link BmTypeInput} and {@link OperatorInput}
   * entities, the {@link BmInput} instances depend on. Doing so, already loaded nodes can be
   * recycled to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param types a set of object and uuid unique {@link BmTypeInput} entities
   * @return a set of object and uuid unique {@link BmInput} entities
   */
  public Set<BmInput> getBmPlants(
      Set<NodeInput> nodes, Set<OperatorInput> operators, Set<BmTypeInput> types) {
    return null;
    /*
    return typedEntityStream(BmInput.class, bmInputFactory, nodes, operators, types)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());

     */
  }

  /**
   * Returns a unique set of {@link StorageInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link StorageInput} which has to be checked
   * manually, as {@link StorageInput#equals(Object)} is NOT restricted on the uuid of {@link
   * StorageInput}.
   *
   * @return a set of object and uuid unique {@link StorageInput} entities
   */
  public Set<StorageInput> getStorages() {
    return null;
    /*
    Set<OperatorInput> operators = typeSource.getOperators();
    return getStorages(rawGridSource.getNodes(operators), operators, typeSource.getStorageTypes());

     */
  }

  /**
   * Returns a set of {@link StorageInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link StorageInput} which has to be checked manually, as {@link StorageInput#equals(Object)}
   * is NOT restricted on the uuid of {@link StorageInput}.
   *
   * <p>In contrast to {@link #getStorages()} this interface provides the ability to pass in an
   * already existing set of {@link NodeInput}, {@link StorageTypeInput} and {@link OperatorInput}
   * entities, the {@link StorageInput} instances depend on. Doing so, already loaded nodes can be
   * recycled to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param types a set of object and uuid unique {@link StorageTypeInput} entities
   * @return a set of object and uuid unique {@link StorageInput} entities
   */
  public Set<StorageInput> getStorages(
      Set<NodeInput> nodes, Set<OperatorInput> operators, Set<StorageTypeInput> types) {
    return null;
    /*
        return typedEntityStream(StorageInput.class, storageInputFactory, nodes, operators, types)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
     */
  }

  /**
   * Returns a unique set of {@link WecInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link WecInput} which has to be checked manually,
   * as {@link WecInput#equals(Object)} is NOT restricted on the uuid of {@link WecInput}.
   *
   * @return a set of object and uuid unique {@link WecInput} entities
   */
  public Set<WecInput> getWecPlants() {
    return null;
    /*
    Set<OperatorInput> operators = typeSource.getOperators();
    return getWecPlants(rawGridSource.getNodes(operators), operators, typeSource.getWecTypes());

     */
  }

  /**
   * Returns a set of {@link WecInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * WecInput} which has to be checked manually, as {@link WecInput#equals(Object)} is NOT
   * restricted on the uuid of {@link WecInput}.
   *
   * <p>In contrast to {@link #getWecPlants()} this interface provides the ability to pass in an
   * already existing set of {@link NodeInput}, {@link WecTypeInput} and {@link OperatorInput}
   * entities, the {@link WecInput} instances depend on. Doing so, already loaded nodes can be
   * recycled to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param types a set of object and uuid unique {@link WecTypeInput} entities
   * @return a set of object and uuid unique {@link WecInput} entities
   */
  public Set<WecInput> getWecPlants(
      Set<NodeInput> nodes, Set<OperatorInput> operators, Set<WecTypeInput> types) {
    return null;
    /*
    return typedEntityStream(WecInput.class, wecInputFactory, nodes, operators, types)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());

     */
  }

  /**
   * Returns a unique set of {@link EvInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EvInput} which has to be checked manually, as
   * {@link EvInput#equals(Object)} is NOT restricted on the uuid of {@link EvInput}.
   *
   * @return a set of object and uuid unique {@link EvInput} entities
   */
  public Set<EvInput> getEvs() {
    return null;
    /*
    Set<OperatorInput> operators = typeSource.getOperators();
    return getEvs(rawGridSource.getNodes(operators), operators, typeSource.getEvTypes());

     */
  }

  /**
   * Returns a set of {@link EvInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * EvInput} which has to be checked manually, as {@link EvInput#equals(Object)} is NOT restricted
   * on the uuid of {@link EvInput}.
   *
   * <p>In contrast to {@link #getEvs()} this interface provides the ability to pass in an already
   * existing set of {@link NodeInput}, {@link EvTypeInput} and {@link OperatorInput} entities, the
   * {@link EvInput} instances depend on. Doing so, already loaded nodes can be recycled to improve
   * performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param types a set of object and uuid unique {@link EvTypeInput} entities
   * @return a set of object and uuid unique {@link EvInput} entities
   */
  public Set<EvInput> getEvs(Set<NodeInput> nodes, Set<OperatorInput> operators, Set<EvTypeInput> types) {
    return null;
    /*
    return typedEntityStream(EvInput.class, evInputFactory, nodes, operators, types)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());

     */
  }

  /**
   * Returns a unique set of {@link ChpInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link ChpInput} which has to be checked manually,
   * as {@link ChpInput#equals(Object)} is NOT restricted on the uuid of {@link ChpInput}.
   *
   * @return a set of object and uuid unique {@link ChpInput} entities
   */
  public Set<ChpInput> getChpPlants() {
    return null;
    /*
    return getChpPlants(
        rawGridSource.getNodes(operators),
        operators,
        typeSource.getChpTypes(),
        thermalBuses,
        thermalSource.getThermalStorages(operators, thermalBuses));

     */
  }

  /**
   * Returns a set of {@link ChpInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * ChpInput} which has to be checked manually, as {@link ChpInput#equals(Object)} is NOT
   * restricted on the uuid of {@link ChpInput}.
   *
   * <p>In contrast to {@link #getChpPlants()} this interface provides the ability to pass in an
   * already existing set of {@link NodeInput}, {@link ChpTypeInput}, {@link ThermalBusInput},
   * {@link ThermalStorageInput} and {@link OperatorInput} entities, the {@link ChpInput} instances
   * depend on. Doing so, already loaded nodes can be recycled to improve performance and prevent
   * unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param types a set of object and uuid unique {@link ChpTypeInput} entities
   * @param thermalBuses a set of object and uuid unique {@link ThermalBusInput} entities
   * @param thermalStorages a set of object and uuid unique {@link ThermalStorageInput} entities
   * @return a set of object and uuid unique {@link ChpInput} entities
   */
  public Set<ChpInput> getChpPlants(
      Set<NodeInput> nodes,
      Set<OperatorInput> operators,
      Set<ChpTypeInput> types,
      Set<ThermalBusInput> thermalBuses,
      Set<ThermalStorageInput> thermalStorages) {
    return null;
/*
    return chpInputStream(nodes, operators, types, thermalBuses, thermalStorages)
            .flatMap(Optional::stream)
            .collect(Collectors.toSet());

 */
  }

  /**
   * Returns a unique set of {@link HpInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link HpInput} which has to be checked manually, as
   * {@link HpInput#equals(Object)} is NOT restricted on the uuid of {@link HpInput}.
   *
   * @return a set of object and uuid unique {@link HpInput} entities
   */
  public Set<HpInput> getHeatPumps() {
    return null;
    /*
    return getHeatPumps(
        rawGridSource.getNodes(operators),
        operators,
        typeSource.getHpTypes(),
        thermalSource.getThermalBuses());

     */

  }

  /**
   * Returns a set of {@link HpInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * HpInput} which has to be checked manually, as {@link HpInput#equals(Object)} is NOT restricted
   * on the uuid of {@link HpInput}.
   *
   * <p>In contrast to {@link #getHeatPumps()} this interface provides the ability to pass in an
   * already existing set of {@link NodeInput}, {@link HpTypeInput}, {@link ThermalBusInput}, {@link
   * ThermalStorageInput} and {@link OperatorInput} entities, the {@link HpInput} instances depend
   * on. Doing so, already loaded nodes can be recycled to improve performance and prevent
   * unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param types a set of object and uuid unique {@link HpTypeInput} entities
   * @param thermalBuses a set of object and uuid unique {@link ThermalBusInput} entities
   * @return a set of object and uuid unique {@link HpInput} entities
   */
  public Set<HpInput> getHeatPumps(
      Set<NodeInput> nodes,
      Set<OperatorInput> operators,
      Set<HpTypeInput> types,
      Set<ThermalBusInput> thermalBuses) {
    return null;
    /*
    return hpInputStream(nodes, operators, types, thermalBuses)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());

     */
  }

  /**
   * Returns a unique set of {@link EmInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EmInput} which has to be checked manually, as
   * {@link EmInput#equals(Object)} is NOT restricted on the uuid of {@link EmInput}.
   *
   * @return a set of object and uuid unique {@link EmInput} entities
   */
  public Set<EmInput> getEmSystems() {
    return null;

    //return getEmSystems(rawGridSource.getNodes(operators), operators);
  }

  /**
   * This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EmInput} which has to be checked manually, as
   * {@link EmInput#equals(Object)} is NOT restricted on the uuid of {@link EmInput}.
   *
   * <p>In contrast to {@link #getHeatPumps()} this interface provides the ability to pass in an
   * already existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link
   * EmInput} instances depend on. Doing so, already loaded nodes can be recycled to improve
   * performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link EmInput} entities
   */
  public Set<EmInput> getEmSystems(Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return null;
    /*
    return nodeAssetEntityStream(EmInput.class, emInputFactory, nodes, operators)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());

     */
  }
}
