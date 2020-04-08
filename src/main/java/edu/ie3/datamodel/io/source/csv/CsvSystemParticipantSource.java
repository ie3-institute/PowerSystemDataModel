/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.factory.input.UntypedSingleNodeEntityData;
import edu.ie3.datamodel.io.factory.input.participant.*;
import edu.ie3.datamodel.io.source.RawGridSource;
import edu.ie3.datamodel.io.source.SystemParticipantSource;
import edu.ie3.datamodel.io.source.ThermalSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import java.util.*;
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
    Collection<OperatorInput> operators = typeSource.getOperators();
    return getFixedFeedIns(rawGridSource.getNodes(operators), operators);
  }

  @Override
  public Set<FixedFeedInInput> getFixedFeedIns(
      Collection<NodeInput> nodes, Collection<OperatorInput> operators) {
    return filterEmptyOptionals(
            buildUntypedEntityData(
                    buildAssetInputEntityData(FixedFeedInInput.class, operators), nodes)
                .map(dataOpt -> dataOpt.flatMap(fixedFeedInInputFactory::getEntity)))
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
            buildUntypedEntityData(buildAssetInputEntityData(PvInput.class, operators), nodes)
                .map(dataOpt -> dataOpt.flatMap(pvInputFactory::getEntity)))
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
            buildUntypedEntityData(buildAssetInputEntityData(LoadInput.class, operators), nodes)
                .map(dataOpt -> dataOpt.flatMap(loadInputFactory::getEntity)))
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
            buildTypedEntityData(
                    buildUntypedEntityData(
                            buildAssetInputEntityData(BmInput.class, operators), nodes)
                        .filter(Optional::isPresent)
                        .map(Optional::get),
                    types)
                .map(dataOpt -> dataOpt.flatMap(bmInputFactory::getEntity)))
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
            buildTypedEntityData(
                    buildUntypedEntityData(
                            buildAssetInputEntityData(StorageInput.class, operators), nodes)
                        .filter(Optional::isPresent)
                        .map(Optional::get),
                    types)
                .map(dataOpt -> dataOpt.flatMap(storageInputFactory::getEntity)))
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
            buildTypedEntityData(
                    buildUntypedEntityData(
                            buildAssetInputEntityData(WecInput.class, operators), nodes)
                        .filter(Optional::isPresent)
                        .map(Optional::get),
                    types)
                .map(dataOpt -> dataOpt.flatMap(wecInputFactory::getEntity)))
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
            buildTypedEntityData(
                    buildUntypedEntityData(
                            buildAssetInputEntityData(EvInput.class, operators), nodes)
                        .filter(Optional::isPresent)
                        .map(Optional::get),
                    types)
                .map(dataOpt -> dataOpt.flatMap(evInputFactory::getEntity)))
        .collect(Collectors.toSet());
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
            buildChpEntityData(
                    buildTypedEntityData(
                            buildUntypedEntityData(
                                    buildAssetInputEntityData(ChpInput.class, operators), nodes)
                                .filter(Optional::isPresent)
                                .map(Optional::get),
                            types)
                        .filter(Optional::isPresent)
                        .map(Optional::get),
                    thermalStorages,
                    thermalBuses)
                .map(dataOpt -> dataOpt.flatMap(chpInputFactory::getEntity)))
        .collect(Collectors.toSet());
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

    return filterEmptyOptionals(
            buildHpEntityData(
                    buildTypedEntityData(
                            buildUntypedEntityData(
                                    buildAssetInputEntityData(HpInput.class, operators), nodes)
                                .filter(Optional::isPresent)
                                .map(Optional::get),
                            types)
                        .filter(Optional::isPresent)
                        .map(Optional::get),
                    thermalBuses)
                .map(dataOpt -> dataOpt.flatMap(hpInputFactory::getEntity)))
        .collect(Collectors.toSet());
  }

  private <T extends SystemParticipantTypeInput>
      Stream<Optional<SystemParticipantTypedEntityData<T>>> buildTypedEntityData(
          Stream<UntypedSingleNodeEntityData> noTypeEntityDataStream, Collection<T> types) {

    return noTypeEntityDataStream
        .parallel()
        .map(
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
            });
  }

  private Stream<Optional<HpInputEntityData>> buildHpEntityData(
      Stream<SystemParticipantTypedEntityData<HpTypeInput>> typedEntityDataStream,
      Collection<ThermalBusInput> thermalBuses) {

    return typedEntityDataStream
        .parallel()
        .map(
            typedEntityData -> {
              // get the raw data
              Map<String, String> fieldsToAttributes = typedEntityData.getFieldsToValues();

              // get the thermal bus input for this chp unit
              String thermalBusUuid = fieldsToAttributes.get("thermalbus");
              Optional<ThermalBusInput> thermalBus =
                  thermalBuses.stream()
                      .filter(
                          storage -> storage.getUuid().toString().equalsIgnoreCase(thermalBusUuid))
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
            });
  }

  private Stream<Optional<ChpInputEntityData>> buildChpEntityData(
      Stream<SystemParticipantTypedEntityData<ChpTypeInput>> typedEntityDataStream,
      Collection<ThermalStorageInput> thermalStorages,
      Collection<ThermalBusInput> thermalBuses) {

    return typedEntityDataStream
        .parallel()
        .map(
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

              // if the thermal storage or the thermal bus are not present we return an empty
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
                  .removeAll(new HashSet<>(Arrays.asList(thermalBusField, "thermalStorage")));

              return Optional.of(
                  new ChpInputEntityData(
                      fieldsToAttributes,
                      typedEntityData.getOperatorInput(),
                      typedEntityData.getNode(),
                      typedEntityData.getTypeInput(),
                      thermalBus.get(),
                      thermalStorage.get()));
            });
  }
}
