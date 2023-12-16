/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.SystemParticipantsException;
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.participant.*;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.Failure;
import edu.ie3.datamodel.utils.Try.Success;
import java.util.*;
import java.util.stream.Stream;

/**
 * Implementation that provides the capability to build entities of type {@link
 * SystemParticipantInput} as well as {@link SystemParticipants} container.
 */
public class SystemParticipantSource extends EntitySource {

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
   * @return either a valid, complete {@link SystemParticipants} or throws a {@link SourceException}
   */
  public SystemParticipants getSystemParticipants() throws SourceException {

    // read all needed entities
    /// start with types and operators
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
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

    /// go on with the nodes
    Map<UUID, NodeInput> nodes = rawGridSource.getNodes(operators);
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
   * @return a set of object and uuid unique {@link FixedFeedInInput} entities
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
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link FixedFeedInInput} entities
   */
  public Set<FixedFeedInInput> getFixedFeedIns(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes, Map<UUID, EmInput> emUnits)
      throws SourceException {
    return unpackSet(
        buildSystemParticipantEntityData(FixedFeedInInput.class, operators, nodes, emUnits)
            .map(fixedFeedInInputFactory::get),
        FixedFeedInInput.class);
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
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link PvInput} entities
   */
  public Set<PvInput> getPvPlants(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes, Map<UUID, EmInput> emUnits)
      throws SourceException {
    return unpackSet(
        buildSystemParticipantEntityData(PvInput.class, operators, nodes, emUnits)
            .map(pvInputFactory::get),
        PvInput.class);
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
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link LoadInput} entities
   */
  public Set<LoadInput> getLoads(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes, Map<UUID, EmInput> emUnits)
      throws SourceException {
    return unpackSet(
        buildSystemParticipantEntityData(LoadInput.class, operators, nodes, emUnits)
            .map(loadInputFactory::get),
        LoadInput.class);
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
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link EvcsInput} entities
   */
  public Set<EvcsInput> getEvcs(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes, Map<UUID, EmInput> emUnits)
      throws SourceException {
    return unpackSet(
        buildSystemParticipantEntityData(EvcsInput.class, operators, nodes, emUnits)
            .map(evcsInputFactory::get),
        EvcsInput.class);
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
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param types a set of object and uuid unique {@link BmTypeInput} entities
   * @return a set of object and uuid unique {@link BmInput} entities
   */
  public Set<BmInput> getBmPlants(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, BmTypeInput> types)
      throws SourceException {
    return unpackSet(
        buildTypedSystemParticipantEntityData(BmInput.class, operators, nodes, emUnits, types)
            .map(bmInputFactory::get),
        BmInput.class);
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
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param types a set of object and uuid unique {@link StorageTypeInput} entities
   * @return a set of object and uuid unique {@link StorageInput} entities
   */
  public Set<StorageInput> getStorages(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, StorageTypeInput> types)
      throws SourceException {
    return unpackSet(
        buildTypedSystemParticipantEntityData(StorageInput.class, operators, nodes, emUnits, types)
            .map(storageInputFactory::get),
        StorageInput.class);
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
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param types a set of object and uuid unique {@link WecTypeInput} entities
   * @return a set of object and uuid unique {@link WecInput} entities
   */
  public Set<WecInput> getWecPlants(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, WecTypeInput> types)
      throws SourceException {
    return unpackSet(
        buildTypedSystemParticipantEntityData(WecInput.class, operators, nodes, emUnits, types)
            .map(wecInputFactory::get),
        WecInput.class);
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
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param types a set of object and uuid unique {@link EvTypeInput} entities
   * @return a set of object and uuid unique {@link EvInput} entities
   */
  public Set<EvInput> getEvs(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, EvTypeInput> types)
      throws SourceException {
    return unpackSet(
        buildTypedSystemParticipantEntityData(EvInput.class, operators, nodes, emUnits, types)
            .map(evInputFactory::get),
        EvInput.class);
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
   */
  public Set<ChpInput> getChpPlants(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, ChpTypeInput> types,
      Map<UUID, ThermalBusInput> thermalBuses,
      Map<UUID, ThermalStorageInput> thermalStorages)
      throws SourceException {
    return unpackSet(
        buildChpEntityData(
                buildTypedSystemParticipantEntityData(
                    ChpInput.class, operators, nodes, emUnits, types),
                thermalStorages,
                thermalBuses)
            .map(chpInputFactory::get),
        ChpInput.class);
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
   */
  public Set<HpInput> getHeatPumps(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, HpTypeInput> types,
      Map<UUID, ThermalBusInput> thermalBuses)
      throws SourceException {
    return unpackSet(
        buildHpEntityData(
                buildTypedSystemParticipantEntityData(
                    HpInput.class, operators, nodes, emUnits, types),
                thermalBuses)
            .map(hpInputFactory::get),
        HpInput.class);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  private static Stream<Try<ChpInputEntityData, SourceException>> buildChpEntityData(
      Stream<Try<SystemParticipantTypedEntityData<ChpTypeInput>, SourceException>>
          typedEntityDataStream,
      Map<UUID, ThermalStorageInput> thermalStorages,
      Map<UUID, ThermalBusInput> thermalBuses) {

    return typedEntityDataStream
        .parallel()
        .map(
            typedEntityDataOpt ->
                typedEntityDataOpt.flatMap(
                    typedEntityData ->
                        createChpEntityData(typedEntityData, thermalStorages, thermalBuses)));
  }

  private static Try<ChpInputEntityData, SourceException> createChpEntityData(
      SystemParticipantTypedEntityData<ChpTypeInput> typedEntityData,
      Map<UUID, ThermalStorageInput> thermalStorages,
      Map<UUID, ThermalBusInput> thermalBuses) {

    // get the raw data
    Map<String, String> fieldsToAttributes = typedEntityData.getFieldsToValues();

    // get the thermal storage input for this chp unit
    Optional<ThermalStorageInput> thermalStorage =
        Optional.ofNullable(fieldsToAttributes.get(THERMAL_STORAGE))
            .flatMap(
                thermalStorageUuid ->
                    Optional.ofNullable(thermalStorages.get(UUID.fromString(thermalStorageUuid))));

    // get the thermal bus input for this chp unit
    Optional<ThermalBusInput> thermalBus =
        Optional.ofNullable(fieldsToAttributes.get("thermalBus"))
            .flatMap(
                thermalBusUuid ->
                    Optional.ofNullable(thermalBuses.get(UUID.fromString(thermalBusUuid))));

    // if the thermal storage or the thermal bus are not present we return an
    // empty element and log a warning
    if (thermalStorage.isEmpty() || thermalBus.isEmpty()) {
      StringBuilder sB = new StringBuilder();
      if (thermalStorage.isEmpty()) {
        sB.append("thermalStorage: ")
            .append(safeMapGet(fieldsToAttributes, THERMAL_STORAGE, FIELDS_TO_VALUES_MAP));
      }
      if (thermalBus.isEmpty()) {
        sB.append("\nthermalBus: ")
            .append(safeMapGet(fieldsToAttributes, THERMAL_BUS, FIELDS_TO_VALUES_MAP));
      }

      String skippingMessage =
          buildSkippingMessage(
              typedEntityData.getTargetClass().getSimpleName(),
              safeMapGet(fieldsToAttributes, "uuid", FIELDS_TO_VALUES_MAP),
              safeMapGet(fieldsToAttributes, "id", FIELDS_TO_VALUES_MAP),
              sB.toString());
      return new Failure<>(new SourceException("Failure due to: " + skippingMessage));
    }

    // remove fields that are passed as objects to constructor
    fieldsToAttributes
        .keySet()
        .removeAll(new HashSet<>(Arrays.asList("thermalBus", "thermalStorage")));

    return new Success<>(
        new ChpInputEntityData(
            fieldsToAttributes,
            typedEntityData.getOperatorInput(),
            typedEntityData.getNode(),
            typedEntityData.getEm().orElse(null),
            typedEntityData.getTypeInput(),
            thermalBus.get(),
            thermalStorage.get()));
  }

  /**
   * Enriches a given stream of {@link SystemParticipantTypedEntityData} tries with a type of {@link
   * ThermalBusInput} based on the provided collection of buses and the fields to values mapping
   * inside the already provided {@link SystemParticipantTypedEntityData} instance.
   *
   * @param typedEntityDataStream the data stream of {@link SystemParticipantTypedEntityData} tries
   * @param thermalBuses the thermal buses that should be used for enrichment and to build {@link
   *     HpInputEntityData}
   * @return stream of tries of {@link HpInputEntityData} instances
   */
  private static Stream<Try<HpInputEntityData, SourceException>> buildHpEntityData(
      Stream<Try<SystemParticipantTypedEntityData<HpTypeInput>, SourceException>>
          typedEntityDataStream,
      Map<UUID, ThermalBusInput> thermalBuses) {

    return typedEntityDataStream
        .parallel()
        .map(
            typedEntityDataOpt ->
                typedEntityDataOpt.flatMap(
                    typedEntityData -> createHpEntityData(typedEntityData, thermalBuses)));
  }

  private static Try<HpInputEntityData, SourceException> createHpEntityData(
      SystemParticipantTypedEntityData<HpTypeInput> typedEntityData,
      Map<UUID, ThermalBusInput> thermalBuses) {
    // get the raw data
    Map<String, String> fieldsToAttributes = typedEntityData.getFieldsToValues();

    // get the thermal bus input for this chp unit and try to build the entity data
    Optional<HpInputEntityData> hpInputEntityDataOpt =
        Optional.ofNullable(fieldsToAttributes.get(THERMAL_BUS))
            .flatMap(
                thermalBusUuid ->
                    Optional.ofNullable(thermalBuses.get(UUID.fromString(thermalBusUuid)))
                        .map(
                            thermalBus -> {

                              // remove fields that are passed as objects to constructor
                              fieldsToAttributes.keySet().remove(THERMAL_BUS);

                              return new HpInputEntityData(
                                  fieldsToAttributes,
                                  typedEntityData.getOperatorInput(),
                                  typedEntityData.getNode(),
                                  typedEntityData.getEm().orElse(null),
                                  typedEntityData.getTypeInput(),
                                  thermalBus);
                            }));

    // if the requested entity is not present we return an empty element and
    // log a warning
    if (hpInputEntityDataOpt.isEmpty()) {
      String failureMessage =
          buildSkippingMessage(
              typedEntityData.getTargetClass().getSimpleName(),
              safeMapGet(fieldsToAttributes, "uuid", FIELDS_TO_VALUES_MAP),
              safeMapGet(fieldsToAttributes, "id", FIELDS_TO_VALUES_MAP),
              "thermalBus: " + safeMapGet(fieldsToAttributes, THERMAL_BUS, FIELDS_TO_VALUES_MAP));
      return new Failure<>(new SourceException("Failure due to: " + failureMessage));
    }

    return new Success<>(hpInputEntityDataOpt.get());
  }

  /**
   * Constructs a stream of {@link SystemParticipantTypedEntityData} wrapped in {@link Try}'s.
   *
   * @param entityClass the class of the entities that should be built
   * @param nodes the nodes that should be considered for these entities
   * @param operators the operators that should be considered for these entities
   * @param types the types that should be considered for these entities
   * @param <T> the type of the type model of the resulting entity
   * @return a stream of tries holding an instance of a {@link SystemParticipantTypedEntityData}
   */
  private <T extends SystemParticipantTypeInput>
      Stream<Try<SystemParticipantTypedEntityData<T>, SourceException>>
          buildTypedSystemParticipantEntityData(
              Class<? extends SystemParticipantInput> entityClass,
              Map<UUID, OperatorInput> operators,
              Map<UUID, NodeInput> nodes,
              Map<UUID, EmInput> emUnits,
              Map<UUID, T> types) {
    return buildTypedSystemParticipantEntityData(
        buildSystemParticipantEntityData(entityClass, operators, nodes, emUnits), types);
  }

  /**
   * Enriches a given stream of {@link SystemParticipantEntityData} {@link Try} objects with a type
   * of {@link SystemParticipantTypeInput} based on the provided collection of types and the fields
   * to values mapping that inside the already provided {@link SystemParticipantEntityData}
   * instance.
   *
   * @param systemParticipantEntityDataStream the data stream of {@link SystemParticipantEntityData}
   *     {@link Try} objects
   * @param types the types that should be used for enrichment and to build {@link
   *     SystemParticipantTypedEntityData} from
   * @param <T> the type of the provided entity types as well as the type parameter of the resulting
   *     {@link SystemParticipantTypedEntityData}
   * @return a stream of tries of {@link SystemParticipantTypedEntityData} instances
   */
  private static <T extends SystemParticipantTypeInput>
      Stream<Try<SystemParticipantTypedEntityData<T>, SourceException>>
          buildTypedSystemParticipantEntityData(
              Stream<Try<SystemParticipantEntityData, SourceException>>
                  systemParticipantEntityDataStream,
              Map<UUID, T> types) {
    return systemParticipantEntityDataStream
        .parallel()
        .map(
            participantEntityDataTry ->
                participantEntityDataTry.flatMap(
                    participantEntityData ->
                        createTypedSystemParticipantEntityData(participantEntityData, types)));
  }

  private static <T extends SystemParticipantTypeInput>
      Try<SystemParticipantTypedEntityData<T>, SourceException>
          createTypedSystemParticipantEntityData(
              SystemParticipantEntityData systemParticipantEntityData, Map<UUID, T> types) {
    return getAssetType(
            types,
            systemParticipantEntityData.getFieldsToValues(),
            systemParticipantEntityData.getClass().getSimpleName())
        .map(
            // if the operation was successful, transform and return to the data
            assetType -> {
              Map<String, String> fieldsToAttributes =
                  systemParticipantEntityData.getFieldsToValues();

              // remove fields that are passed as objects to constructor
              fieldsToAttributes.keySet().remove(TYPE);

              return new SystemParticipantTypedEntityData<>(systemParticipantEntityData, assetType);
            });
  }

  private <S extends SystemParticipantInput>
      Stream<Try<SystemParticipantEntityData, SourceException>> buildSystemParticipantEntityData(
          Class<S> entityClass,
          Map<UUID, OperatorInput> operators,
          Map<UUID, NodeInput> nodes,
          Map<UUID, EmInput> emUnits) {
    return buildSystemParticipantEntityData(
        nodeAssetInputEntityDataStream(assetInputEntityDataStream(entityClass, operators), nodes),
        emUnits);
  }

  /**
   * Enriches a given stream of {@link NodeAssetInputEntityData} {@link Try} objects with a type of
   * {@link EmInput} based on the provided collection of EMs and the fields to values mapping that
   * inside the already provided {@link NodeAssetInputEntityData} instance.
   *
   * @param nodeAssetEntityDataStream the data stream of {@link NodeAssetInputEntityData} {@link
   *     Try} objects
   * @param emUnits the energy management units that should be used for enrichment and to build
   *     {@link SystemParticipantEntityData} from
   * @return a stream of tries of {@link SystemParticipantEntityData} instances
   */
  private static Stream<Try<SystemParticipantEntityData, SourceException>>
      buildSystemParticipantEntityData(
          Stream<Try<NodeAssetInputEntityData, SourceException>> nodeAssetEntityDataStream,
          Map<UUID, EmInput> emUnits) {
    return nodeAssetEntityDataStream
        .parallel()
        .map(
            nodeAssetInputEntityDataOpt ->
                nodeAssetInputEntityDataOpt.flatMap(
                    nodeAssetInputEntityData ->
                        createSystemParticipantEntityData(nodeAssetInputEntityData, emUnits)));
  }

  private static Try<SystemParticipantEntityData, SourceException>
      createSystemParticipantEntityData(
          NodeAssetInputEntityData nodeAssetInputEntityData, Map<UUID, EmInput> emUnits) {

    Map<String, String> fieldsToAttributes = nodeAssetInputEntityData.getFieldsToValues();

    Try<Optional<EmInput>, SourceException> tryEm =
        Optional.ofNullable(
                nodeAssetInputEntityData.getUUID(SystemParticipantInputEntityFactory.EM))
            .map(
                // System participant has given a proper UUID for EM. In case of success, we wrap in
                // Optional
                emUuid -> getEntity(emUuid, emUnits).map(Optional::of))
            // No UUID was given (column does not exist, or field is empty),
            // this is totally fine - we return an "empty success"
            .orElse(new Try.Success<>(Optional.empty()));

    return tryEm.map(
        // if the operation was successful, transform and return to the data
        optionalEm -> {
          // remove fields that are passed as objects to constructor
          fieldsToAttributes.keySet().remove(SystemParticipantInputEntityFactory.EM);

          return new SystemParticipantEntityData(nodeAssetInputEntityData, optionalEm.orElse(null));
        });
  }

  private static <T> Try<T, SourceException> getEntity(UUID uuid, Map<UUID, T> entityMap) {
    return Optional.ofNullable(entityMap.get(uuid))
        // We either find a matching entity for given UUID, thus return a success
        .map(entity -> (Try<T, SourceException>) new Try.Success<T, SourceException>(entity))
        // ... or find no matching entity, returning a failure.
        .orElse(
            new Try.Failure<>(
                new SourceException("Entity with uuid " + uuid + " was not provided.")));
  }
}
