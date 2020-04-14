/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.factory.input.*;
import edu.ie3.datamodel.io.source.ThermalSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.thermal.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * //ToDo: Class Description todo note that Set does not check for unique uuids
 *
 * @version 0.1
 * @since 07.04.20
 */
public class CsvThermalSource extends CsvDataSource implements ThermalSource {

  // general fields
  private final TypeSource typeSource;

  // factories
  private final ThermalBusInputFactory thermalBusInputFactory;
  private final CylindricalStorageInputFactory cylindricalStorageInputFactory;
  private final ThermalHouseInputFactory thermalHouseInputFactory;

  public CsvThermalSource(
      String csvSep,
      String thermalUnitsFolderPath,
      FileNamingStrategy fileNamingStrategy,
      TypeSource typeSource) {
    super(csvSep, thermalUnitsFolderPath, fileNamingStrategy);
    this.typeSource = typeSource;

    // init factories
    this.thermalBusInputFactory = new ThermalBusInputFactory();
    this.cylindricalStorageInputFactory = new CylindricalStorageInputFactory();
    this.thermalHouseInputFactory = new ThermalHouseInputFactory();
  }

  @Override
  public Set<ThermalBusInput> getThermalBuses() {
    return filterEmptyOptionals(
            assetInputEntityDataStream(ThermalBusInput.class, typeSource.getOperators())
                .map(thermalBusInputFactory::getEntity))
        .collect(Collectors.toSet());
  }

  @Override
  public Set<ThermalBusInput> getThermalBuses(Collection<OperatorInput> operators) {
    return filterEmptyOptionals(
            assetInputEntityDataStream(ThermalBusInput.class, operators)
                .map(thermalBusInputFactory::getEntity))
        .collect(Collectors.toSet());
  }

  @Override
  public Set<ThermalStorageInput> getThermalStorages() {
    return new HashSet<>(getCylindricStorages());
  }

  @Override
  public Set<ThermalStorageInput> getThermalStorages(
      Collection<OperatorInput> operators, Collection<ThermalBusInput> thermalBuses) {
    return new HashSet<>(getCylindricStorages(operators, thermalBuses));
  }

  @Override
  public Set<ThermalHouseInput> getThermalHouses() {

    return (assetInputEntityDataStream(ThermalHouseInput.class, typeSource.getOperators())
        .map(
            assetInputEntityData ->
                buildThermalUnitInputEntityData(assetInputEntityData, getThermalBuses())
                    .map(dataOpt -> dataOpt.flatMap(thermalHouseInputFactory::getEntity)))
        .flatMap(this::filterEmptyOptionals)
        .collect(Collectors.toSet()));
  }

  @Override
  public Set<ThermalHouseInput> getThermalHouses(
      Collection<OperatorInput> operators, Collection<ThermalBusInput> thermalBuses) {

    return (assetInputEntityDataStream(ThermalHouseInput.class, operators)
        .map(
            assetInputEntityData ->
                buildThermalUnitInputEntityData(assetInputEntityData, thermalBuses)
                    .map(dataOpt -> dataOpt.flatMap(thermalHouseInputFactory::getEntity)))
        .flatMap(this::filterEmptyOptionals)
        .collect(Collectors.toSet()));
  }

  @Override
  public Set<CylindricalStorageInput> getCylindricStorages() {

    return (assetInputEntityDataStream(CylindricalStorageInput.class, typeSource.getOperators())
        .map(
            assetInputEntityData ->
                buildThermalUnitInputEntityData(assetInputEntityData, getThermalBuses())
                    .map(dataOpt -> dataOpt.flatMap(cylindricalStorageInputFactory::getEntity)))
        .flatMap(this::filterEmptyOptionals)
        .collect(Collectors.toSet()));
  }

  @Override
  public Set<CylindricalStorageInput> getCylindricStorages(
      Collection<OperatorInput> operators, Collection<ThermalBusInput> thermalBuses) {

    return (assetInputEntityDataStream(CylindricalStorageInput.class, operators)
        .map(
            assetInputEntityData ->
                buildThermalUnitInputEntityData(assetInputEntityData, thermalBuses)
                    .map(dataOpt -> dataOpt.flatMap(cylindricalStorageInputFactory::getEntity)))
        .flatMap(this::filterEmptyOptionals)
        .collect(Collectors.toSet()));
  }

  private Stream<Optional<ThermalUnitInputEntityData>> buildThermalUnitInputEntityData(
      AssetInputEntityData assetInputEntityData, Collection<ThermalBusInput> thermalBuses) {

    // get the raw data
    Map<String, String> fieldsToAttributes = assetInputEntityData.getFieldsToValues();

    // get the thermal bus input for this chp unit
    String thermalBusUuid = fieldsToAttributes.get("thermalbus");
    Optional<ThermalBusInput> thermalBus =
        thermalBuses.stream()
            .filter(storage -> storage.getUuid().toString().equalsIgnoreCase(thermalBusUuid))
            .findFirst();

    // remove fields that are passed as objects to constructor
    fieldsToAttributes.keySet().removeAll(new HashSet<>(Collections.singletonList("thermalbus")));

    // if the type is not present we return an empty element and
    // log a warning
    if (!thermalBus.isPresent()) {
      logSkippingWarning(
          assetInputEntityData.getEntityClass().getSimpleName(),
          fieldsToAttributes.get("uuid"),
          fieldsToAttributes.get("id"),
          "thermalBus: " + thermalBusUuid);
      return Stream.of(Optional.empty());
    }

    // for operator ignore warning for excessive lambda usage in .orElseGet()
    // because of performance (see https://www.baeldung.com/java-optional-or-else-vs-or-else-get=
    // for details)
    return Stream.of(
        Optional.of(
            new ThermalUnitInputEntityData(
                assetInputEntityData.getFieldsToValues(),
                assetInputEntityData.getEntityClass(),
                assetInputEntityData.getOperatorInput(),
                thermalBus.get())));
  }
}
