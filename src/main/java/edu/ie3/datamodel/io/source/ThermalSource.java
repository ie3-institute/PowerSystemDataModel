/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.factory.input.CylindricalStorageInputFactory;
import edu.ie3.datamodel.io.factory.input.ThermalBusInputFactory;
import edu.ie3.datamodel.io.factory.input.ThermalHouseInputFactory;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Interface that provides the capability to build thermal {@link
 * edu.ie3.datamodel.models.input.AssetInput} entities from persistent data e.g. .csv files or
 * databases
 *
 * @version 0.1
 * @since 08.04.20
 */
public class ThermalSource implements DataSource {
  // general fields
  TypeSource typeSource;
  FunctionalDataSource dataSource;

  // factories
  private final ThermalBusInputFactory thermalBusInputFactory;
  private final CylindricalStorageInputFactory cylindricalStorageInputFactory;
  private final ThermalHouseInputFactory thermalHouseInputFactory;

  public ThermalSource(TypeSource typeSource, FunctionalDataSource dataSource) {
    this.typeSource = typeSource;
    this.dataSource = dataSource;

    this.thermalBusInputFactory = new ThermalBusInputFactory();
    this.cylindricalStorageInputFactory = new CylindricalStorageInputFactory();
    this.thermalHouseInputFactory = new ThermalHouseInputFactory();
  }

  /**
   * Returns a unique set of {@link ThermalBusInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link ThermalBusInput} which has to be checked
   * manually, as {@link ThermalBusInput#equals(Object)} is NOT restricted on the uuid of {@link
   * ThermalBusInput}.
   *
   * @return a set of object and uuid unique {@link ThermalBusInput} entities
   */
  public Set<ThermalBusInput> getThermalBuses() {
    return null;
    /*
      return assetInputEntityDataStream(ThermalBusInput.class, typeSource.getOperators())
              .map(thermalBusInputFactory::get)
              .flatMap(Optional::stream)
              .collect(Collectors.toSet());

     */
  }

  /**
   * Returns a set of {@link ThermalBusInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link ThermalBusInput} which has to be checked manually, as {@link
   * ThermalBusInput#equals(Object)} is NOT restricted on the uuid of {@link ThermalBusInput}.
   *
   * <p>In contrast to {@link #getThermalBuses()} this interface provides the ability to pass in an
   * already existing set of {@link OperatorInput} entities, the {@link ThermalBusInput} instances
   * depend on. Doing so, already loaded nodes can be recycled to improve performance and prevent
   * unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @return a set of object and uuid unique {@link ThermalBusInput} entities
   */
  public Set<ThermalBusInput> getThermalBuses(Set<OperatorInput> operators) {
    return null;
    /*
    return assetInputEntityDataStream(ThermalBusInput.class, operators)
        .map(thermalBusInputFactory::get)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());

     */
  }

  /**
   * Returns a unique set of instances of all entities implementing the {@link ThermalStorageInput}
   * abstract class.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link ThermalStorageInput} which has to be checked
   * manually, as {@link ThermalStorageInput#equals(Object)} is NOT restricted on the uuid of {@link
   * ThermalStorageInput}.
   *
   * @return a set of object and uuid unique {@link ThermalStorageInput} entities
   */
  public Set<ThermalStorageInput> getThermalStorages() {
    return null;
    //return new HashSet<>(getCylindricStorages());
  }

  /**
   * Returns a unique set of instances of all entities implementing the {@link ThermalStorageInput}
   * abstract class. This set has to be unique in the sense of object uniqueness but also in the
   * sense of {@link java.util.UUID} uniqueness of the provided {@link ThermalStorageInput} which
   * has to be checked manually, as {@link ThermalStorageInput#equals(Object)} is NOT restricted on
   * the uuid of {@link ThermalStorageInput}.
   *
   * <p>In contrast to {@link #getThermalStorages()} this interface provides the ability to pass in
   * an already existing set of {@link OperatorInput} entities, the {@link ThermalStorageInput}
   * instances depend on. Doing so, already loaded nodes can be recycled to improve performance and
   * prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param thermalBuses a set of object and uuid unique {@link ThermalBusInput} that should be used
   *     for the returning instances
   * @return a set of object and uuid unique {@link ThermalStorageInput} entities
   */
  public Set<ThermalStorageInput> getThermalStorages(Set<OperatorInput> operators, Set<ThermalBusInput> thermalBuses) {
    return null;
    //    return new HashSet<>(getCylindricStorages(operators, thermalBuses));
  }

  /**
   * Returns a unique set of {@link ThermalHouseInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link ThermalHouseInput} which has to be checked
   * manually, as {@link ThermalHouseInput#equals(Object)} is NOT restricted on the uuid of {@link
   * ThermalHouseInput}.
   *
   * @return a set of object and uuid unique {@link ThermalHouseInput} entities
   */
  public Set<ThermalHouseInput> getThermalHouses() {
    return null;
    /*
    assetInputEntityDataStream(ThermalHouseInput.class, typeSource.getOperators())
        .flatMap(
            assetInputEntityData ->
                buildThermalUnitInputEntityData(assetInputEntityData, getThermalBuses())
                    .map(dataOpt -> dataOpt.flatMap(thermalHouseInputFactory::get))
                    .flatMap(Optional::stream))
        .collect(Collectors.toSet());
     */
  }

  /**
   * Returns a set of {@link ThermalHouseInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link ThermalHouseInput} which has to be checked manually, as {@link
   * ThermalHouseInput#equals(Object)} is NOT restricted on the uuid of {@link ThermalHouseInput}.
   *
   * <p>In contrast to {@link #getThermalHouses()} this interface provides the ability to pass in an
   * already existing set of {@link OperatorInput} entities, the {@link ThermalHouseInput} instances
   * depend on. Doing so, already loaded nodes can be recycled to improve performance and prevent
   * unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param thermalBuses a set of object and uuid unique {@link ThermalBusInput} that should be used
   *     for the returning instances
   * @return a set of object and uuid unique {@link ThermalHouseInput} entities
   */
  public Set<ThermalHouseInput> getThermalHouses(
      Set<OperatorInput> operators, Set<ThermalBusInput> thermalBuses) {
    return null;
    /*
    assetInputEntityDataStream(ThermalHouseInput.class, operators)
        .map(
            assetInputEntityData ->
                buildThermalUnitInputEntityData(assetInputEntityData, thermalBuses)
                    .map(dataOpt -> dataOpt.flatMap(thermalHouseInputFactory::get)))
        .flatMap(elements -> elements.flatMap(Optional::stream))
        .collect(Collectors.toSet());
     */
  }

  /**
   * Returns a unique set of {@link CylindricalStorageInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link CylindricalStorageInput} which has to be
   * checked manually, as {@link CylindricalStorageInput#equals(Object)} is NOT restricted on the
   * uuid of {@link CylindricalStorageInput}.
   *
   * @return a set of object and uuid unique {@link CylindricalStorageInput} entities
   */
  public Set<CylindricalStorageInput> getCylindricStorages() {
    return null;
    /* return assetInputEntityDataStream(CylindricalStorageInput.class, typeSource.getOperators())
            .map(
                    assetInputEntityData ->
                            buildThermalUnitInputEntityData(assetInputEntityData, getThermalBuses())
                                    .map(dataOpt -> dataOpt.flatMap(cylindricalStorageInputFactory::get)))
            .flatMap(elements -> elements.flatMap(Optional::stream))
            .collect(Collectors.toSet());

     */
  }

  /**
   * Returns a set of {@link CylindricalStorageInput} instances. This set has to be unique in the
   * sense of object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the
   * provided {@link CylindricalStorageInput} which has to be checked manually, as {@link
   * CylindricalStorageInput#equals(Object)} is NOT restricted on the uuid of {@link
   * CylindricalStorageInput}.
   *
   * <p>In contrast to {@link #getCylindricStorages()} this interface provides the ability to pass
   * in an already existing set of {@link OperatorInput} entities, the {@link
   * CylindricalStorageInput} instances depend on. Doing so, already loaded nodes can be recycled to
   * improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param thermalBuses a set of object and uuid unique {@link ThermalBusInput} that should be used
   *     for the returning instances
   * @return a set of object and uuid unique {@link CylindricalStorageInput} entities
   */
  public Set<CylindricalStorageInput> getCylindricStorages(
      Set<OperatorInput> operators, Set<ThermalBusInput> thermalBuses) {
    return null;
    /*    return assetInputEntityDataStream(CylindricalStorageInput.class, operators)
        .map(
            assetInputEntityData ->
                buildThermalUnitInputEntityData(assetInputEntityData, thermalBuses)
                    .map(dataOpt -> dataOpt.flatMap(cylindricalStorageInputFactory::get)))
        .flatMap(elements -> elements.flatMap(Optional::stream))
        .collect(Collectors.toSet());

     */
  }
}
