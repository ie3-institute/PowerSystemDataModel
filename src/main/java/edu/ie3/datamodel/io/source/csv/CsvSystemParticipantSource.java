/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.participant.*;
import edu.ie3.datamodel.io.source.RawGridSource;
import edu.ie3.datamodel.io.source.SystemParticipantSource;
import edu.ie3.datamodel.io.source.ThermalSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.input.EvcsInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.NotImplementedException;

/**
 * //ToDo: Class Description
 *
 * <p>TODO description needs hint that Set does NOT mean uuid uniqueness -> using the () getter
 * without providing files with unique entities might cause confusing results if duplicate uuids
 * exist on a file specific level (e.g. for types!)
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
  public SystemParticipants getSystemParticipants() {

    // todo instead of filtering empty optionals out directly when building assets from data handle
    //  the empty ones as error (compare with CsvRawGridSource)

    //        Set<BmInput> bmPlants, - done
    //        Set<ChpInput> chpPlants,  // todo needs thermal support
    //        Set<EvcsInput> evCS, - done
    //        Set<EvInput> evs, - done
    //        Set<FixedFeedInInput> fixedFeedIns, - done
    //        Set<HpInput> heatPumps,  // todo needs thermal support
    //        Set<LoadInput> loads, - done
    //        Set<PvInput> pvPlants, - done
    //        Set<StorageInput> storages, - done
    //        Set<WecInput> wecPlants - done
    //

    return null;
  }

  @Override
  public Set<FixedFeedInInput> getFixedFeedIns() {
    return filterEmptyOptionals(
            buildAssetInputEntityData(FixedFeedInInput.class, typeSource.getOperators())
                .map(
                    assetInputEntityData ->
                        buildUntypedEntityData(assetInputEntityData, rawGridSource.getNodes())
                            .map(dataOpt -> dataOpt.flatMap(fixedFeedInInputFactory::getEntity)))
                .flatMap(Function.identity()))
        .collect(Collectors.toSet());
  }

  @Override
  public Set<FixedFeedInInput> getFixedFeedIns(
      Collection<NodeInput> nodes, Collection<OperatorInput> operators) {

    return filterEmptyOptionals(
            buildAssetInputEntityData(FixedFeedInInput.class, operators)
                .map(
                    assetInputEntityData ->
                        buildUntypedEntityData(assetInputEntityData, nodes)
                            .map(dataOpt -> dataOpt.flatMap(fixedFeedInInputFactory::getEntity)))
                .flatMap(Function.identity()))
        .collect(Collectors.toSet());
  }

  @Override
  public Set<PvInput> getPvPlants() {
    return filterEmptyOptionals(
            buildAssetInputEntityData(PvInput.class, typeSource.getOperators())
                .map(
                    assetInputEntityData ->
                        buildUntypedEntityData(assetInputEntityData, rawGridSource.getNodes())
                            .map(dataOpt -> dataOpt.flatMap(pvInputFactory::getEntity)))
                .flatMap(Function.identity()))
        .collect(Collectors.toSet());
  }

  @Override
  public Set<PvInput> getPvPlants(
      Collection<NodeInput> nodes, Collection<OperatorInput> operators) {
    return filterEmptyOptionals(
            buildAssetInputEntityData(PvInput.class, operators)
                .map(
                    assetInputEntityData ->
                        buildUntypedEntityData(assetInputEntityData, nodes)
                            .map(dataOpt -> dataOpt.flatMap(pvInputFactory::getEntity)))
                .flatMap(Function.identity()))
        .collect(Collectors.toSet());
  }

  @Override
  public Set<LoadInput> getLoads() {
    return filterEmptyOptionals(
            buildAssetInputEntityData(LoadInput.class, typeSource.getOperators())
                .map(
                    assetInputEntityData ->
                        buildUntypedEntityData(assetInputEntityData, rawGridSource.getNodes())
                            .map(dataOpt -> dataOpt.flatMap(loadInputFactory::getEntity)))
                .flatMap(Function.identity()))
        .collect(Collectors.toSet());
  }

  @Override
  public Set<LoadInput> getLoads(Collection<NodeInput> nodes, Collection<OperatorInput> operators) {

    return filterEmptyOptionals(
            buildAssetInputEntityData(LoadInput.class, operators)
                .map(
                    assetInputEntityData ->
                        buildUntypedEntityData(assetInputEntityData, nodes)
                            .map(dataOpt -> dataOpt.flatMap(loadInputFactory::getEntity)))
                .flatMap(Function.identity()))
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

    return buildAssetInputEntityData(BmInput.class, typeSource.getOperators())
        .map(
            assetInputEntityData ->
                buildUntypedEntityData(assetInputEntityData, rawGridSource.getNodes())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(
                        untypedData ->
                            buildTypedEntityData(untypedData, typeSource.getBmTypes())
                                .map(dataOpt -> dataOpt.flatMap(bmInputFactory::getEntity)))
                    .flatMap(this::filterEmptyOptionals))
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }

  @Override
  public Set<BmInput> getBmPlants(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<BmTypeInput> types) {
    return buildAssetInputEntityData(BmInput.class, operators)
        .map(
            assetInputEntityData ->
                buildUntypedEntityData(assetInputEntityData, nodes)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(
                        untypedData ->
                            buildTypedEntityData(untypedData, types)
                                .map(dataOpt -> dataOpt.flatMap(bmInputFactory::getEntity)))
                    .flatMap(this::filterEmptyOptionals))
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }

  @Override
  public Set<StorageInput> getStorages() {

    return buildAssetInputEntityData(StorageInput.class, typeSource.getOperators())
        .map(
            assetInputEntityData ->
                buildUntypedEntityData(assetInputEntityData, rawGridSource.getNodes())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(
                        untypedData ->
                            buildTypedEntityData(untypedData, typeSource.getStorageTypes())
                                .map(dataOpt -> dataOpt.flatMap(storageInputFactory::getEntity)))
                    .flatMap(this::filterEmptyOptionals))
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }

  @Override
  public Set<StorageInput> getStorages(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<StorageTypeInput> types) {
    return buildAssetInputEntityData(StorageInput.class, operators)
        .map(
            assetInputEntityData ->
                buildUntypedEntityData(assetInputEntityData, nodes)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(
                        untypedData ->
                            buildTypedEntityData(untypedData, types)
                                .map(dataOpt -> dataOpt.flatMap(storageInputFactory::getEntity)))
                    .flatMap(this::filterEmptyOptionals))
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }

  @Override
  public Set<WecInput> getWecPlants() {

    return buildAssetInputEntityData(WecInput.class, typeSource.getOperators())
        .map(
            assetInputEntityData ->
                buildUntypedEntityData(assetInputEntityData, rawGridSource.getNodes())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(
                        untypedData ->
                            buildTypedEntityData(untypedData, typeSource.getWecTypes())
                                .map(dataOpt -> dataOpt.flatMap(wecInputFactory::getEntity)))
                    .flatMap(this::filterEmptyOptionals))
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }

  @Override
  public Set<WecInput> getWecPlants(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<WecTypeInput> types) {
    return buildAssetInputEntityData(WecInput.class, operators)
        .map(
            assetInputEntityData ->
                buildUntypedEntityData(assetInputEntityData, nodes)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(
                        untypedData ->
                            buildTypedEntityData(untypedData, types)
                                .map(dataOpt -> dataOpt.flatMap(wecInputFactory::getEntity)))
                    .flatMap(this::filterEmptyOptionals))
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }

  @Override
  public Set<EvInput> getEvs() {

    return buildAssetInputEntityData(EvInput.class, typeSource.getOperators())
        .map(
            assetInputEntityData ->
                buildUntypedEntityData(assetInputEntityData, rawGridSource.getNodes())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(
                        untypedData ->
                            buildTypedEntityData(untypedData, typeSource.getEvTypes())
                                .map(dataOpt -> dataOpt.flatMap(evInputFactory::getEntity)))
                    .flatMap(this::filterEmptyOptionals))
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }

  @Override
  public Set<EvInput> getEvs(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<EvTypeInput> types) {

    return buildAssetInputEntityData(EvInput.class, operators)
        .map(
            assetInputEntityData ->
                buildUntypedEntityData(assetInputEntityData, nodes)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(
                        untypedData ->
                            buildTypedEntityData(untypedData, types)
                                .map(dataOpt -> dataOpt.flatMap(evInputFactory::getEntity)))
                    .flatMap(this::filterEmptyOptionals))
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }

  @Override
  public Set<ChpInput> getChpPlants() {

    return buildAssetInputEntityData(ChpInput.class, typeSource.getOperators())
        .map(
            assetInputEntityData ->
                buildUntypedEntityData(assetInputEntityData, rawGridSource.getNodes())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(
                        untypedData ->
                            buildTypedEntityData(untypedData, typeSource.getChpTypes())
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .flatMap(
                                    typedData ->
                                        buildChpInputData(
                                            typedData,
                                            thermalSource.getThermalStorages(),
                                            thermalSource.getThermalBuses()))
                                .map(dataOpt -> dataOpt.flatMap(chpInputFactory::getEntity)))
                    .flatMap(this::filterEmptyOptionals))
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }

  @Override
  public Set<ChpInput> getChpPlants(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<ChpTypeInput> types,
      Collection<ThermalStorageInput> thermalStorages,
      Collection<ThermalBusInput> thermalBuses) {

    return buildAssetInputEntityData(ChpInput.class, operators)
        .map(
            assetInputEntityData ->
                buildUntypedEntityData(assetInputEntityData, nodes)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(
                        untypedData ->
                            buildTypedEntityData(untypedData, types)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .flatMap(
                                    typedData ->
                                        buildChpInputData(typedData, thermalStorages, thermalBuses))
                                .map(dataOpt -> dataOpt.flatMap(chpInputFactory::getEntity)))
                    .flatMap(this::filterEmptyOptionals))
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }

  @Override
  public Set<HpInput> getHeatPumps() {

    return buildAssetInputEntityData(HpInput.class, typeSource.getOperators())
        .map(
            assetInputEntityData ->
                buildUntypedEntityData(assetInputEntityData, rawGridSource.getNodes())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(
                        untypedData ->
                            buildTypedEntityData(untypedData, typeSource.getHpTypes())
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .flatMap(
                                    typedData ->
                                        buildHpEntityData(
                                            typedData, thermalSource.getThermalBuses()))
                                .map(dataOpt -> dataOpt.flatMap(hpInputFactory::getEntity)))
                    .flatMap(this::filterEmptyOptionals))
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }

  @Override
  public Set<HpInput> getHeatPumps(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<HpTypeInput> types,
      Collection<ThermalBusInput> thermalBuses) {

    return buildAssetInputEntityData(HpInput.class, operators)
        .map(
            assetInputEntityData ->
                buildUntypedEntityData(assetInputEntityData, nodes)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(
                        untypedData ->
                            buildTypedEntityData(untypedData, types)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .flatMap(typedData -> buildHpEntityData(typedData, thermalBuses))
                                .map(dataOpt -> dataOpt.flatMap(hpInputFactory::getEntity)))
                    .flatMap(this::filterEmptyOptionals))
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }

  private Stream<Optional<SystemParticipantEntityData>> buildUntypedEntityData(
      AssetInputEntityData assetInputEntityData, Collection<NodeInput> nodes) {

    // get the raw data
    Map<String, String> fieldsToAttributes = assetInputEntityData.getFieldsToValues();

    // get the node of the entity
    String nodeUuid = fieldsToAttributes.get(NODE);
    Optional<NodeInput> node = findNodeByUuid(nodeUuid, nodes);

    // if the node is not present we return an empty element and
    // log a warning
    if (!node.isPresent()) {
      logSkippingWarning(
          assetInputEntityData.getEntityClass().getSimpleName(),
          fieldsToAttributes.get("uuid"),
          fieldsToAttributes.get("id"),
          NODE + ": " + nodeUuid);
      return Stream.of(Optional.empty());
    }

    // remove fields that are passed as objects to constructor
    fieldsToAttributes.keySet().removeAll(new HashSet<>(Arrays.asList(NODE)));

    return Stream.of(
        Optional.of(
            new SystemParticipantEntityData(
                fieldsToAttributes,
                assetInputEntityData.getEntityClass(),
                assetInputEntityData.getOperatorInput(),
                node.get())));
  }

  private <T extends SystemParticipantTypeInput>
      Stream<Optional<SystemParticipantTypedEntityData<T>>> buildTypedEntityData(
          SystemParticipantEntityData noTypeEntityData, Collection<T> types) {

    // get the raw data
    Map<String, String> fieldsToAttributes = noTypeEntityData.getFieldsToValues();

    // get the type entity of this entity
    String typeUuid = fieldsToAttributes.get(TYPE);
    Optional<T> assetType = findTypeByUuid(typeUuid, types);

    // if the type is not present we return an empty element and
    // log a warning
    if (!assetType.isPresent()) {
      logSkippingWarning(
          noTypeEntityData.getEntityClass().getSimpleName(),
          fieldsToAttributes.get("uuid"),
          fieldsToAttributes.get("id"),
          TYPE + ": " + typeUuid);
      return Stream.of(Optional.empty());
    }

    // remove fields that are passed as objects to constructor
    fieldsToAttributes.keySet().removeAll(new HashSet<>(Collections.singletonList(TYPE)));

    /// for operator ignore warning for excessive lambda usage in .orElseGet()
    /// because of performance (see https://www.baeldung.com/java-optional-or-else-vs-or-else-get=
    // for details)
    return Stream.of(
        Optional.of(
            new SystemParticipantTypedEntityData<>(
                fieldsToAttributes,
                noTypeEntityData.getEntityClass(),
                noTypeEntityData.getOperatorInput(),
                noTypeEntityData.getNode(),
                assetType.get())));
  }

  private Stream<Optional<HpInputEntityData>> buildHpEntityData(
      SystemParticipantTypedEntityData<HpTypeInput> typedEntityData,
      Collection<ThermalBusInput> thermalBuses) {

    // get the raw data
    Map<String, String> fieldsToAttributes = typedEntityData.getFieldsToValues();

    // get the thermal bus input for this chp unit
    String thermalBusUuid = fieldsToAttributes.get("thermalbus");
    Optional<ThermalBusInput> thermalBus =
        thermalBuses.stream()
            .filter(storage -> storage.getUuid().toString().equalsIgnoreCase(thermalBusUuid))
            .findFirst();

    // if the thermal bus is not present we return an empty element and
    // log a warning
    if (!thermalBus.isPresent()) {
      logSkippingWarning(
          typedEntityData.getEntityClass().getSimpleName(),
          fieldsToAttributes.get("uuid"),
          fieldsToAttributes.get("id"),
          "thermalBus: " + thermalBusUuid);
      return Stream.of(Optional.empty());
    }

    // remove fields that are passed as objects to constructor
    fieldsToAttributes.keySet().removeAll(new HashSet<>(Collections.singletonList("thermalbus")));

    /// for operator ignore warning for excessive lambda usage in .orElseGet()
    /// because of performance (see https://www.baeldung.com/java-optional-or-else-vs-or-else-get=
    // for details)
    return Stream.of(
        Optional.of(
            new HpInputEntityData(
                fieldsToAttributes,
                typedEntityData.getOperatorInput(),
                typedEntityData.getNode(),
                typedEntityData.getTypeInput(),
                thermalBus.get())));
  }

  private Stream<Optional<ChpInputEntityData>> buildChpInputData(
      SystemParticipantTypedEntityData<ChpTypeInput> typedEntityData,
      Collection<ThermalStorageInput> thermalStorages,
      Collection<ThermalBusInput> thermalBuses) {

    // get the raw data
    Map<String, String> fieldsToAttributes = typedEntityData.getFieldsToValues();

    // get the thermal storage input for this chp unit
    String thermalStorageUuid = fieldsToAttributes.get("thermalstorage");
    Optional<ThermalStorageInput> thermalStorage =
        thermalStorages.stream()
            .filter(storage -> storage.getUuid().toString().equalsIgnoreCase(thermalStorageUuid))
            .findFirst();

    // get the thermal bus input for this chp unit
    String thermalBusUuid = fieldsToAttributes.get("thermalbus");
    Optional<ThermalBusInput> thermalBus =
        thermalBuses.stream()
            .filter(storage -> storage.getUuid().toString().equalsIgnoreCase(thermalBusUuid))
            .findFirst();

    // if the thermal storage is not present we return an empty element and
    // log a warning
    if (!thermalStorage.isPresent() || !thermalBus.isPresent()) {
      String debugString =
          Stream.of(
                  new AbstractMap.SimpleEntry<>(
                      thermalStorage, "thermalStorage: " + thermalStorageUuid),
                  new AbstractMap.SimpleEntry<>(thermalBus, "thermalBus: " + thermalBusUuid))
              .filter(entry -> !entry.getKey().isPresent())
              .map(AbstractMap.SimpleEntry::getValue)
              .collect(Collectors.joining("\n"));

      logSkippingWarning(
          typedEntityData.getEntityClass().getSimpleName(),
          fieldsToAttributes.get("uuid"),
          fieldsToAttributes.get("id"),
          debugString);
      return Stream.of(Optional.empty());
    }

    // remove fields that are passed as objects to constructor
    fieldsToAttributes
        .keySet()
        .removeAll(new HashSet<>(Arrays.asList("thermalbus", "thermalStorage")));

    /// for operator ignore warning for excessive lambda usage in .orElseGet()
    /// because of performance (see https://www.baeldung.com/java-optional-or-else-vs-or-else-get=
    // for details)
    return Stream.of(
        Optional.of(
            new ChpInputEntityData(
                fieldsToAttributes,
                typedEntityData.getOperatorInput(),
                typedEntityData.getNode(),
                typedEntityData.getTypeInput(),
                thermalBus.get(),
                thermalStorage.get())));
  }
}
