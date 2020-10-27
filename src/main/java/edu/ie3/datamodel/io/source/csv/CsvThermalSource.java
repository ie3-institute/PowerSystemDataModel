/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.csv.FileNamingStrategy;
import edu.ie3.datamodel.io.factory.input.*;
import edu.ie3.datamodel.io.source.ThermalSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Source that provides the capability to build thermal {@link
 * edu.ie3.datamodel.models.input.AssetInput} entities from .csv files
 *
 * <p>This source is <b>not buffered</b> which means each call on a getter method always tries to
 * read all data is necessary to return the requested objects in a hierarchical cascading way.
 *
 * <p>If performance is an issue, it is recommended to read the data cascading starting with reading
 * nodes and then using the getters with arguments to avoid reading the same data multiple times.
 *
 * <p>The resulting sets are always unique on object <b>and</b> UUID base (with distinct UUIDs).
 *
 * @version 0.1
 * @since 03.04.20
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
  /** {@inheritDoc} */
  @Override
  public Set<ThermalBusInput> getThermalBuses() {
    return filterEmptyOptionals(
            assetInputEntityDataStream(ThermalBusInput.class, typeSource.getOperators())
                .map(thermalBusInputFactory::get))
        .collect(Collectors.toSet());
  }

  /**
   * {@inheritDoc}
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<ThermalBusInput> getThermalBuses(Set<OperatorInput> operators) {
    return filterEmptyOptionals(
            assetInputEntityDataStream(ThermalBusInput.class, operators)
                .map(thermalBusInputFactory::get))
        .collect(Collectors.toSet());
  }
  /** {@inheritDoc} */
  @Override
  public Set<ThermalStorageInput> getThermalStorages() {
    return new HashSet<>(getCylindricStorages());
  }

  /**
   * {@inheritDoc}
   *
   * <p>If the set of {@link ThermalBusInput} entities is not exhaustive for all available {@link
   * ThermalStorageInput} entities (e.g. a {@link ThermalBusInput} entity is missing) or if an error
   * during the building process occurs, the entity that misses something will be skipped (which can
   * be seen as a filtering functionality) but all entities that are able to be built will be
   * returned anyway and the elements that couldn't have been built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<ThermalStorageInput> getThermalStorages(
      Set<OperatorInput> operators, Set<ThermalBusInput> thermalBuses) {
    return new HashSet<>(getCylindricStorages(operators, thermalBuses));
  }
  /** {@inheritDoc} */
  @Override
  public Set<ThermalHouseInput> getThermalHouses() {

    return (assetInputEntityDataStream(ThermalHouseInput.class, typeSource.getOperators())
        .map(
            assetInputEntityData ->
                buildThermalUnitInputEntityData(assetInputEntityData, getThermalBuses())
                    .map(dataOpt -> dataOpt.flatMap(thermalHouseInputFactory::get)))
        .flatMap(this::filterEmptyOptionals)
        .collect(Collectors.toSet()));
  }

  /**
   * {@inheritDoc}
   *
   * <p>If the set of {@link ThermalBusInput} entities is not exhaustive for all available {@link
   * ThermalHouseInput} entities (e.g. a {@link ThermalBusInput} entity is missing) or if an error
   * during the building process occurs, the entity that misses something will be skipped (which can
   * be seen as a filtering functionality) but all entities that are able to be built will be
   * returned anyway and the elements that couldn't have been built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<ThermalHouseInput> getThermalHouses(
      Set<OperatorInput> operators, Set<ThermalBusInput> thermalBuses) {

    return (assetInputEntityDataStream(ThermalHouseInput.class, operators)
        .map(
            assetInputEntityData ->
                buildThermalUnitInputEntityData(assetInputEntityData, thermalBuses)
                    .map(dataOpt -> dataOpt.flatMap(thermalHouseInputFactory::get)))
        .flatMap(this::filterEmptyOptionals)
        .collect(Collectors.toSet()));
  }
  /** {@inheritDoc} */
  @Override
  public Set<CylindricalStorageInput> getCylindricStorages() {

    return (assetInputEntityDataStream(CylindricalStorageInput.class, typeSource.getOperators())
        .map(
            assetInputEntityData ->
                buildThermalUnitInputEntityData(assetInputEntityData, getThermalBuses())
                    .map(dataOpt -> dataOpt.flatMap(cylindricalStorageInputFactory::get)))
        .flatMap(this::filterEmptyOptionals)
        .collect(Collectors.toSet()));
  }

  /**
   * {@inheritDoc}
   *
   * <p>If the set of {@link ThermalBusInput} entities is not exhaustive for all available {@link
   * CylindricalStorageInput} entities (e.g. a {@link ThermalBusInput} entity is missing) or if an
   * error during the building process occurs, the entity that misses something will be skipped
   * (which can be seen as a filtering functionality) but all entities that are able to be built
   * will be returned anyway and the elements that couldn't have been built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<CylindricalStorageInput> getCylindricStorages(
      Set<OperatorInput> operators, Set<ThermalBusInput> thermalBuses) {

    return (assetInputEntityDataStream(CylindricalStorageInput.class, operators)
        .map(
            assetInputEntityData ->
                buildThermalUnitInputEntityData(assetInputEntityData, thermalBuses)
                    .map(dataOpt -> dataOpt.flatMap(cylindricalStorageInputFactory::get)))
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
          assetInputEntityData.getTargetClass().getSimpleName(),
          fieldsToAttributes.get("uuid"),
          fieldsToAttributes.get("id"),
          "thermalBus: " + thermalBusUuid);
      return Stream.of(Optional.empty());
    }

    return Stream.of(
        Optional.of(
            new ThermalUnitInputEntityData(
                assetInputEntityData.getFieldsToValues(),
                assetInputEntityData.getTargetClass(),
                assetInputEntityData.getOperatorInput(),
                thermalBus.get())));
  }
}
