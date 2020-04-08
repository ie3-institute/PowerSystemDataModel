/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.input.UntypedSingleNodeEntityData;
import edu.ie3.datamodel.io.factory.input.participant.*;
import edu.ie3.datamodel.io.source.RawGridSource;
import edu.ie3.datamodel.io.source.SystemParticipantSource;
import edu.ie3.datamodel.io.source.ThermalSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.*;
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
import org.apache.commons.lang3.NotImplementedException;

/**
 * //ToDo: Class Description
 *
 * <p>TODO description needs hint that Set does NOT mean uuid uniqueness -> using the () getter //
 * todo performance improvements in all sources to make as as less possible recursive stream calls
 * on files without providing files with unique entities might cause confusing results if duplicate
 * uuids exist on a file specific level (e.g. for types!)
 *
 * @version 0.1
 * @since 06.04.20
 */
public class CsvSystemParticipantSource extends CsvDataSource implements SystemParticipantSource {

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
  }

  @Override
  // todo check for all duplciates!
  public Optional<SystemParticipants> getSystemParticipants() {

    // read all needed entities
    /// start with types and operators
    Collection<OperatorInput> operators = typeSource.getOperators();
    Collection<BmTypeInput> bmTypes = typeSource.getBmTypes();
    Collection<ChpTypeInput> chpTypes = typeSource.getChpTypes();
    Collection<EvTypeInput> evTypes = typeSource.getEvTypes();
    Collection<HpTypeInput> hpTypes = typeSource.getHpTypes();
    Collection<StorageTypeInput> storageTypes = typeSource.getStorageTypes();
    Collection<WecTypeInput> wecTypes = typeSource.getWecTypes();

    /// go on with the thermal assets
    Collection<ThermalBusInput> thermalBuses = thermalSource.getThermalBuses(operators);
    Collection<ThermalStorageInput> thermalStorages =
        thermalSource.getThermalStorages(operators, thermalBuses);
    /// go on with the nodes incl. filter of unique entities + warning if duplicate uuids got
    // filtered out
    Collection<NodeInput> nodes =
        checkForUuidDuplicates(NodeInput.class, rawGridSource.getNodes(operators));

    // start with the entities needed for SystemParticipants container
    /// as we want to return a working grid, keep an eye on empty optionals
    ConcurrentHashMap<Class<? extends UniqueEntity>, LongAdder> invalidElementsCounter =
        new ConcurrentHashMap<>();

    Set<FixedFeedInInput> fixedFeedInInputs =
        checkForUuidDuplicates(
            FixedFeedInInput.class,
            untypedEntityStream(FixedFeedInInput.class, fixedFeedInInputFactory, nodes, operators)
                .filter(isPresentCollectIfNot(FixedFeedInInput.class, invalidElementsCounter))
                .map(Optional::get)
                .collect(Collectors.toSet()));
    Set<PvInput> pvInputs =
        checkForUuidDuplicates(
            PvInput.class,
            untypedEntityStream(PvInput.class, pvInputFactory, nodes, operators)
                .filter(isPresentCollectIfNot(PvInput.class, invalidElementsCounter))
                .map(Optional::get)
                .collect(Collectors.toSet()));
    Set<LoadInput> loads =
        checkForUuidDuplicates(
            LoadInput.class,
            untypedEntityStream(LoadInput.class, loadInputFactory, nodes, operators)
                .filter(isPresentCollectIfNot(LoadInput.class, invalidElementsCounter))
                .map(Optional::get)
                .collect(Collectors.toSet()));
    Set<BmInput> bmInputs =
        checkForUuidDuplicates(
            BmInput.class,
            typedEntityStream(BmInput.class, bmInputFactory, nodes, operators, bmTypes)
                .filter(isPresentCollectIfNot(BmInput.class, invalidElementsCounter))
                .map(Optional::get)
                .collect(Collectors.toSet()));
    Set<StorageInput> storages =
        checkForUuidDuplicates(
            StorageInput.class,
            typedEntityStream(
                    StorageInput.class, storageInputFactory, nodes, operators, storageTypes)
                .filter(isPresentCollectIfNot(StorageInput.class, invalidElementsCounter))
                .map(Optional::get)
                .collect(Collectors.toSet()));
    Set<WecInput> wecInputs =
        checkForUuidDuplicates(
            WecInput.class,
            typedEntityStream(WecInput.class, wecInputFactory, nodes, operators, wecTypes)
                .filter(isPresentCollectIfNot(WecInput.class, invalidElementsCounter))
                .map(Optional::get)
                .collect(Collectors.toSet()));
    Set<EvInput> evs =
        checkForUuidDuplicates(
            EvInput.class,
            typedEntityStream(EvInput.class, evInputFactory, nodes, operators, evTypes)
                .filter(isPresentCollectIfNot(EvInput.class, invalidElementsCounter))
                .map(Optional::get)
                .collect(Collectors.toSet()));
    Set<ChpInput> chpInputs =
        checkForUuidDuplicates(
            ChpInput.class,
            chpInputStream(nodes, operators, chpTypes, thermalBuses, thermalStorages)
                .filter(isPresentCollectIfNot(ChpInput.class, invalidElementsCounter))
                .map(Optional::get)
                .collect(Collectors.toSet()));
    Set<HpInput> hpInputs =
        checkForUuidDuplicates(
            HpInput.class,
            hpInputStream(nodes, operators, hpTypes, thermalBuses)
                .filter(isPresentCollectIfNot(HpInput.class, invalidElementsCounter))
                .map(Optional::get)
                .collect(Collectors.toSet()));

    // if we found invalid elements return an empty optional and log the problems
    if (!invalidElementsCounter.isEmpty()) {
      invalidElementsCounter.forEach(this::printInvalidElementInformation);
      return Optional.empty();
    }

    // if everything is fine, return a system participants container
    return Optional.of(
        new SystemParticipants(
            bmInputs,
            chpInputs,
            Collections.emptySet(),
            evs,
            fixedFeedInInputs,
            hpInputs,
            loads,
            pvInputs,
            storages,
            wecInputs));
  }

  @Override
  public Set<FixedFeedInInput> getFixedFeedIns() {
    Collection<OperatorInput> operators = typeSource.getOperators();
    return getFixedFeedIns(rawGridSource.getNodes(operators), operators);
  }

  @Override
  public Set<FixedFeedInInput> getFixedFeedIns(
      Collection<NodeInput> nodes, Collection<OperatorInput> operators) {
    return filterEmptyOptionals(
            untypedEntityStream(FixedFeedInInput.class, fixedFeedInInputFactory, nodes, operators))
        .collect(Collectors.toSet());
  }

  @Override
  public Set<PvInput> getPvPlants() {
    Collection<OperatorInput> operators = typeSource.getOperators();
    return getPvPlants(rawGridSource.getNodes(operators), operators);
  }

  @Override
  public Set<PvInput> getPvPlants(
      Collection<NodeInput> nodes, Collection<OperatorInput> operators) {
    return filterEmptyOptionals(
            untypedEntityStream(PvInput.class, pvInputFactory, nodes, operators))
        .collect(Collectors.toSet());
  }

  @Override
  public Set<LoadInput> getLoads() {
    Collection<OperatorInput> operators = typeSource.getOperators();
    return getLoads(rawGridSource.getNodes(operators), operators);
  }

  @Override
  public Set<LoadInput> getLoads(Collection<NodeInput> nodes, Collection<OperatorInput> operators) {
    return filterEmptyOptionals(
            untypedEntityStream(LoadInput.class, loadInputFactory, nodes, operators))
        .collect(Collectors.toSet());
  }

  @Override
  public Set<EvcsInput> getEvCS() {
    throw new NotImplementedException("Ev Charging Stations are not implemented yet!");
  }

  @Override
  public Set<EvcsInput> getEvCS(Collection<NodeInput> nodes, Collection<OperatorInput> operators) {
    throw new NotImplementedException("Ev Charging Stations are not implemented yet!");
  }

  @Override
  public Set<BmInput> getBmPlants() {
    Collection<OperatorInput> operators = typeSource.getOperators();
    return getBmPlants(rawGridSource.getNodes(operators), operators, typeSource.getBmTypes());
  }

  @Override
  public Set<BmInput> getBmPlants(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<BmTypeInput> types) {
    return filterEmptyOptionals(
            typedEntityStream(BmInput.class, bmInputFactory, nodes, operators, types))
        .collect(Collectors.toSet());
  }

  @Override
  public Set<StorageInput> getStorages() {
    Collection<OperatorInput> operators = typeSource.getOperators();
    return getStorages(rawGridSource.getNodes(operators), operators, typeSource.getStorageTypes());
  }

  @Override
  public Set<StorageInput> getStorages(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<StorageTypeInput> types) {
    return filterEmptyOptionals(
            typedEntityStream(StorageInput.class, storageInputFactory, nodes, operators, types))
        .collect(Collectors.toSet());
  }

  @Override
  public Set<WecInput> getWecPlants() {

    Collection<OperatorInput> operators = typeSource.getOperators();

    return getWecPlants(rawGridSource.getNodes(operators), operators, typeSource.getWecTypes());
  }

  @Override
  public Set<WecInput> getWecPlants(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<WecTypeInput> types) {

    return filterEmptyOptionals(
            typedEntityStream(WecInput.class, wecInputFactory, nodes, operators, types))
        .collect(Collectors.toSet());
  }

  @Override
  public Set<EvInput> getEvs() {

    Collection<OperatorInput> operators = typeSource.getOperators();

    return getEvs(rawGridSource.getNodes(operators), operators, typeSource.getEvTypes());
  }

  @Override
  public Set<EvInput> getEvs(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<EvTypeInput> types) {

    return filterEmptyOptionals(
            typedEntityStream(EvInput.class, evInputFactory, nodes, operators, types))
        .collect(Collectors.toSet());
  }

  private <T extends SystemParticipantInput, A extends SystemParticipantTypeInput>
      Stream<Optional<T>> typedEntityStream(
          Class<T> entityClass,
          EntityFactory<T, SystemParticipantTypedEntityData<A>> factory,
          Collection<NodeInput> nodes,
          Collection<OperatorInput> operators,
          Collection<A> types) {
    return buildTypedEntityData(
            buildUntypedEntityData(buildAssetInputEntityData(entityClass, operators), nodes), types)
        .map(dataOpt -> dataOpt.flatMap(factory::getEntity));
  }

  @Override
  public Set<ChpInput> getChpPlants() {
    Collection<OperatorInput> operators = typeSource.getOperators();
    Collection<ThermalBusInput> thermalBuses = thermalSource.getThermalBuses(operators);
    return getChpPlants(
        rawGridSource.getNodes(operators),
        operators,
        typeSource.getChpTypes(),
        thermalBuses,
        thermalSource.getThermalStorages(operators, thermalBuses));
  }

  @Override
  public Set<ChpInput> getChpPlants(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<ChpTypeInput> types,
      Collection<ThermalBusInput> thermalBuses,
      Collection<ThermalStorageInput> thermalStorages) {

    return filterEmptyOptionals(
            chpInputStream(nodes, operators, types, thermalBuses, thermalStorages))
        .collect(Collectors.toSet());
  }

  private Stream<Optional<ChpInput>> chpInputStream(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<ChpTypeInput> types,
      Collection<ThermalBusInput> thermalBuses,
      Collection<ThermalStorageInput> thermalStorages) {
    return buildChpEntityData(
            buildTypedEntityData(
                buildUntypedEntityData(buildAssetInputEntityData(ChpInput.class, operators), nodes),
                types),
            thermalStorages,
            thermalBuses)
        .map(dataOpt -> dataOpt.flatMap(chpInputFactory::getEntity));
  }

  @Override
  public Set<HpInput> getHeatPumps() {
    Collection<OperatorInput> operators = typeSource.getOperators();
    return getHeatPumps(
        rawGridSource.getNodes(operators),
        operators,
        typeSource.getHpTypes(),
        thermalSource.getThermalBuses());
  }

  @Override
  public Set<HpInput> getHeatPumps(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<HpTypeInput> types,
      Collection<ThermalBusInput> thermalBuses) {
    return filterEmptyOptionals(hpInputStream(nodes, operators, types, thermalBuses))
        .collect(Collectors.toSet());
  }

  private Stream<Optional<HpInput>> hpInputStream(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<HpTypeInput> types,
      Collection<ThermalBusInput> thermalBuses) {
    return buildHpEntityData(
            buildTypedEntityData(
                buildUntypedEntityData(buildAssetInputEntityData(HpInput.class, operators), nodes),
                types),
            thermalBuses)
        .map(dataOpt -> dataOpt.flatMap(hpInputFactory::getEntity));
  }

  private <T extends SystemParticipantTypeInput>
      Stream<Optional<SystemParticipantTypedEntityData<T>>> buildTypedEntityData(
          Stream<Optional<UntypedSingleNodeEntityData>> noTypeEntityDataStream,
          Collection<T> types) {

    return noTypeEntityDataStream
        .parallel()
        .map(
            typedEntityDataOpt ->
                typedEntityDataOpt.flatMap(
                    noTypeEntityData -> {
                      // get the raw data
                      Map<String, String> fieldsToAttributes = noTypeEntityData.getFieldsToValues();

                      // get the type entity of this entity
                      String typeUuid = fieldsToAttributes.get(TYPE);
                      Optional<T> assetType = findFirstEntityByUuid(typeUuid, types);

                      // if the type is not present we return an empty element and
                      // log a warning
                      if (!assetType.isPresent()) {
                        logSkippingWarning(
                            noTypeEntityData.getEntityClass().getSimpleName(),
                            fieldsToAttributes.get("uuid"),
                            fieldsToAttributes.get("id"),
                            TYPE + ": " + typeUuid);
                        return Optional.empty();
                      }

                      // remove fields that are passed as objects to constructor
                      fieldsToAttributes.keySet().remove(TYPE);

                      return Optional.of(
                          new SystemParticipantTypedEntityData<>(
                              fieldsToAttributes,
                              noTypeEntityData.getEntityClass(),
                              noTypeEntityData.getOperatorInput(),
                              noTypeEntityData.getNode(),
                              assetType.get()));
                    }));
  }

  private Stream<Optional<HpInputEntityData>> buildHpEntityData(
      Stream<Optional<SystemParticipantTypedEntityData<HpTypeInput>>> typedEntityDataStream,
      Collection<ThermalBusInput> thermalBuses) {

    return typedEntityDataStream
        .parallel()
        .map(
            typedEntityDataOpt ->
                typedEntityDataOpt.flatMap(
                    typedEntityData -> {
                      // get the raw data
                      Map<String, String> fieldsToAttributes = typedEntityData.getFieldsToValues();

                      // get the thermal bus input for this chp unit
                      String thermalBusUuid = fieldsToAttributes.get("thermalbus");
                      Optional<ThermalBusInput> thermalBus =
                          thermalBuses.stream()
                              .filter(
                                  storage ->
                                      storage.getUuid().toString().equalsIgnoreCase(thermalBusUuid))
                              .findFirst();

                      // if the thermal bus is not present we return an empty element and
                      // log a warning
                      if (!thermalBus.isPresent()) {
                        logSkippingWarning(
                            typedEntityData.getEntityClass().getSimpleName(),
                            fieldsToAttributes.get("uuid"),
                            fieldsToAttributes.get("id"),
                            "thermalBus: " + thermalBusUuid);
                        return Optional.empty();
                      }

                      // remove fields that are passed as objects to constructor
                      fieldsToAttributes.keySet().remove("thermalbus");

                      return Optional.of(
                          new HpInputEntityData(
                              fieldsToAttributes,
                              typedEntityData.getOperatorInput(),
                              typedEntityData.getNode(),
                              typedEntityData.getTypeInput(),
                              thermalBus.get()));
                    }));
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
                    typedEntityData -> {
                      // get the raw data
                      Map<String, String> fieldsToAttributes = typedEntityData.getFieldsToValues();

                      // get the thermal storage input for this chp unit
                      String thermalStorageUuid = fieldsToAttributes.get("thermalstorage");
                      Optional<ThermalStorageInput> thermalStorage =
                          findFirstEntityByUuid(thermalStorageUuid, thermalStorages);

                      // get the thermal bus input for this chp unit
                      final String thermalBusField = "thermalBus";
                      String thermalBusUuid = fieldsToAttributes.get(thermalBusField);
                      Optional<ThermalBusInput> thermalBus =
                          findFirstEntityByUuid(thermalBusUuid, thermalBuses);

                      // if the thermal storage or the thermal bus are not present we return an
                      // empty
                      // element and log a warning
                      if (!thermalStorage.isPresent() || !thermalBus.isPresent()) {
                        String debugString =
                            Stream.of(
                                    new AbstractMap.SimpleEntry<>(
                                        thermalStorage, "thermalStorage: " + thermalStorageUuid),
                                    new AbstractMap.SimpleEntry<>(
                                        thermalBus, thermalBusField + ": " + thermalBusUuid))
                                .filter(entry -> !entry.getKey().isPresent())
                                .map(AbstractMap.SimpleEntry::getValue)
                                .collect(Collectors.joining("\n"));

                        logSkippingWarning(
                            typedEntityData.getEntityClass().getSimpleName(),
                            fieldsToAttributes.get("uuid"),
                            fieldsToAttributes.get("id"),
                            debugString);
                        return Optional.empty();
                      }

                      // remove fields that are passed as objects to constructor
                      fieldsToAttributes
                          .keySet()
                          .removeAll(
                              new HashSet<>(Arrays.asList(thermalBusField, "thermalStorage")));

                      return Optional.of(
                          new ChpInputEntityData(
                              fieldsToAttributes,
                              typedEntityData.getOperatorInput(),
                              typedEntityData.getNode(),
                              typedEntityData.getTypeInput(),
                              thermalBus.get(),
                              thermalStorage.get()));
                    }));
  }
}
