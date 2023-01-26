/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.participant.*;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import edu.ie3.datamodel.models.UniqueEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  DataSourceFactory sourceFactory;

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

  public SystemParticipantSource (
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

    Stream<Map<String, String>> a = dataSource.getSourceData(PvInput.class);

    Set<FixedFeedInInput> fixedFeedInInputs = dataSource.buildNodeAssetEntities(FixedFeedInInput.class, fixedFeedInInputFactory, nodes, operators, nonBuildEntities);
    Set<PvInput> pvInputs = dataSource.buildNodeAssetEntities(PvInput.class, pvInputFactory, nodes, operators, nonBuildEntities);
    Set<LoadInput> loads = dataSource.buildNodeAssetEntities(LoadInput.class, loadInputFactory, nodes, operators, nonBuildEntities);
    Set<BmInput> bmInputs = dataSource.buildSystemParticipantEntities(BmInput.class, bmInputFactory, nodes, operators, bmTypes, nonBuildEntities);
    Set<StorageInput> storages = dataSource.buildSystemParticipantEntities(StorageInput.class, storageInputFactory, nodes, operators, storageTypes, nonBuildEntities);
    Set<WecInput> wecInputs = dataSource.buildSystemParticipantEntities(WecInput.class, wecInputFactory, nodes, operators, wecTypes, nonBuildEntities);
    Set<EvInput> evs = dataSource.buildSystemParticipantEntities(EvInput.class, evInputFactory, nodes, operators, evTypes, nonBuildEntities);
    Set<EvcsInput> evcs = dataSource.buildNodeAssetEntities(EvcsInput.class, evcsInputFactory, nodes, operators, nonBuildEntities);
    Set<ChpInput> chpInputs = dataSource.buildChpInputEntities(chpInputFactory, nodes, operators, chpTypes, thermalBuses, thermalStorages, nonBuildEntities);
    Set<HpInput> hpInputs =
        hpInputStream(nodes, operators, hpTypes, thermalBuses)
            .filter(isPresentCollectIfNot(HpInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());
    Set<EmInput> emInputs = dataSource.buildNodeAssetEntities(EmInput.class, emInputFactory, nodes, , nonBuildEntities);

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

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-


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











}
