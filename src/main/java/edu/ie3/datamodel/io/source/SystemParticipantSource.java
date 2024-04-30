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

  @Override
  public void validate() throws ValidationException {
    Try.scanStream(
            Stream.of(
                validate(BmInput.class, bmInputFactory),
                validate(ChpInput.class, chpInputFactory),
                validate(EvInput.class, evInputFactory),
                validate(FixedFeedInInput.class, fixedFeedInInputFactory),
                validate(HpInput.class, hpInputFactory),
                validate(LoadInput.class, loadInputFactory),
                validate(PvInput.class, pvInputFactory),
                validate(StorageInput.class, storageInputFactory),
                validate(WecInput.class, wecInputFactory),
                validate(EvcsInput.class, evcsInputFactory)),
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
    return unpackSet(
        chpEntityStream(
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
    return unpackSet(
        hpEntityStream(
                buildTypedSystemParticipantEntityData(
                    HpInput.class, operators, nodes, emUnits, types),
                thermalBuses)
            .map(hpInputFactory::get),
        HpInput.class);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  private static Stream<Try<ChpInputEntityData, SourceException>> chpEntityStream(
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
                        enrichEntityData(
                            typedEntityData,
                            THERMAL_BUS,
                            thermalBuses,
                            THERMAL_STORAGE,
                            thermalStorages,
                            ChpInputEntityData::new)));
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
  private static Stream<Try<HpInputEntityData, SourceException>> hpEntityStream(
      Stream<Try<SystemParticipantTypedEntityData<HpTypeInput>, SourceException>>
          typedEntityDataStream,
      Map<UUID, ThermalBusInput> thermalBuses) {

    return typedEntityDataStream
        .parallel()
        .map(
            typedEntityDataOpt ->
                typedEntityDataOpt.flatMap(
                    typedEntityData ->
                        enrichEntityData(
                            typedEntityData, THERMAL_BUS, thermalBuses, HpInputEntityData::new)));
  }

  /**
   * Constructs a stream of {@link SystemParticipantTypedEntityData} wrapped in {@link Try}'s.
   *
   * @param entityClass the class of the entities that should be built
   * @param operators the operators that should be considered for these entities
   * @param nodes the nodes that should be considered for these entities
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
    return typedSystemParticipantEntityStream(
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
          typedSystemParticipantEntityStream(
              Stream<Try<SystemParticipantEntityData, SourceException>>
                  systemParticipantEntityDataStream,
              Map<UUID, T> types) {
    return systemParticipantEntityDataStream
        .parallel()
        .map(
            participantEntityDataTry ->
                participantEntityDataTry.flatMap(
                    participantEntityData ->
                        enrichEntityData(
                            participantEntityData,
                            TYPE,
                            types,
                            SystemParticipantTypedEntityData<T>::new)));
  }

  private Stream<Try<SystemParticipantEntityData, SourceException>>
      buildSystemParticipantEntityData(
          Class<? extends SystemParticipantInput> entityClass,
          Map<UUID, OperatorInput> operators,
          Map<UUID, NodeInput> nodes,
          Map<UUID, EmInput> emUnits) {
    return systemParticipantEntityStream(
        buildNodeAssetEntityData(entityClass, operators, nodes), emUnits);
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
      systemParticipantEntityStream(
          Stream<Try<NodeAssetInputEntityData, SourceException>> nodeAssetEntityDataStream,
          Map<UUID, EmInput> emUnits) {
    return nodeAssetEntityDataStream
        .parallel()
        .map(
            nodeAssetInputEntityDataTry ->
                nodeAssetInputEntityDataTry.flatMap(
                    nodeAssetInputEntityData ->
                        optionallyEnrichEntityData(
                            nodeAssetInputEntityData,
                            SystemParticipantInputEntityFactory.EM,
                            emUnits,
                            null,
                            SystemParticipantEntityData::new)));
  }
}
