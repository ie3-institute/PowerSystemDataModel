/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.FailureException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.input.*;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Interface that provides the capability to build thermal {@link
 * edu.ie3.datamodel.models.input.AssetInput} entities from persistent data e.g. .csv files or
 * databases
 *
 * @version 0.1
 * @since 08.04.20
 */
public class ThermalSource extends EntitySource {
  // general fields
  private final TypeSource typeSource;

  // factories
  private final ThermalBusInputFactory thermalBusInputFactory;
  private final CylindricalStorageInputFactory cylindricalStorageInputFactory;
  private final ThermalHouseInputFactory thermalHouseInputFactory;

  public ThermalSource(TypeSource typeSource, DataSource dataSource) {
    this.typeSource = typeSource;
    this.dataSource = dataSource;

    this.thermalBusInputFactory = new ThermalBusInputFactory();
    this.cylindricalStorageInputFactory = new CylindricalStorageInputFactory();
    this.thermalHouseInputFactory = new ThermalHouseInputFactory();
  }

  @Override
  public void validate() {
    List<FactoryException> exceptions =
        Try.getExceptions(
            validate(ThermalBusInput.class, thermalBusInputFactory),
            validate(CylindricalStorageInput.class, cylindricalStorageInputFactory),
            validate(ThermalHouseInput.class, thermalHouseInputFactory));

    exceptions.forEach(e -> log.warn("The following exception was thrown while validating: ", e));
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
  public Set<ThermalBusInput> getThermalBuses() throws SourceException {
    return getThermalBuses(typeSource.getOperators());
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
  public Set<ThermalBusInput> getThermalBuses(Set<OperatorInput> operators) throws SourceException {
    return Try.scanCollection(
            buildAssetInputEntities(ThermalBusInput.class, thermalBusInputFactory, operators),
            ThermalBusInput.class)
        .transformF(SourceException::new)
        .getOrThrow();
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
  public Set<ThermalStorageInput> getThermalStorages() throws SourceException {
    return new HashSet<>(getCylindricStorages());
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
  public Set<ThermalStorageInput> getThermalStorages(
      Set<OperatorInput> operators, Set<ThermalBusInput> thermalBuses) throws SourceException {
    return new HashSet<>(getCylindricStorages(operators, thermalBuses));
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
  public Set<ThermalHouseInput> getThermalHouses() throws SourceException {
    return buildThermalHouseInputEntities(thermalHouseInputFactory)
        .transformF(SourceException::new)
        .getOrThrow();
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
      Set<OperatorInput> operators, Set<ThermalBusInput> thermalBuses) throws SourceException {
    return buildThermalHouseInputEntities(thermalHouseInputFactory, operators, thermalBuses)
        .transformF(SourceException::new)
        .getOrThrow();
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
  public Set<CylindricalStorageInput> getCylindricStorages() throws SourceException {
    return buildCylindricalStorageInputEntities(cylindricalStorageInputFactory)
        .transformF(SourceException::new)
        .getOrThrow();
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
      Set<OperatorInput> operators, Set<ThermalBusInput> thermalBuses) throws SourceException {
    return Try.scanCollection(
            buildCylindricalStorageInputEntities(
                cylindricalStorageInputFactory, operators, thermalBuses),
            CylindricalStorageInput.class)
        .transformF(SourceException::new)
        .getOrThrow();
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  protected Stream<Try<ThermalUnitInputEntityData, SourceException>>
      buildThermalUnitInputEntityData(
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
    if (thermalBus.isEmpty()) {
      String skippingMessage =
          buildSkippingMessage(
              assetInputEntityData.getTargetClass().getSimpleName(),
              fieldsToAttributes.get("uuid"),
              fieldsToAttributes.get("id"),
              "thermalBus: " + thermalBusUuid);
      return Stream.of(new Failure<>(new SourceException("Failure due to: " + skippingMessage)));
    }

    return Stream.of(
        new Success<>(
            new ThermalUnitInputEntityData(
                assetInputEntityData.getFieldsToValues(),
                assetInputEntityData.getTargetClass(),
                assetInputEntityData.getOperatorInput(),
                thermalBus.get())));
  }

  public Try<Set<ThermalHouseInput>, FailureException> buildThermalHouseInputEntities(
      ThermalHouseInputFactory factory) throws SourceException {
    Set<ThermalBusInput> thermalBuses = getThermalBuses();

    return Try.scanCollection(
        assetInputEntityDataStream(ThermalHouseInput.class, typeSource.getOperators())
            .flatMap(
                assetInputEntityData ->
                    buildThermalUnitInputEntityData(assetInputEntityData, thermalBuses)
                        .map(factory::get))
            .collect(Collectors.toSet()),
        ThermalHouseInput.class);
  }

  public Try<Set<ThermalHouseInput>, FailureException> buildThermalHouseInputEntities(
      ThermalHouseInputFactory factory,
      Collection<OperatorInput> operators,
      Collection<ThermalBusInput> thermalBuses) {
    return Try.scanCollection(
        assetInputEntityDataStream(ThermalHouseInput.class, operators)
            .flatMap(
                assetInputEntityData ->
                    buildThermalUnitInputEntityData(assetInputEntityData, thermalBuses)
                        .map(factory::get))
            .collect(Collectors.toSet()),
        ThermalHouseInput.class);
  }

  public Try<Set<CylindricalStorageInput>, FailureException> buildCylindricalStorageInputEntities(
      CylindricalStorageInputFactory factory) throws SourceException {
    Set<ThermalBusInput> thermalBuses = getThermalBuses();

    return Try.scanCollection(
        assetInputEntityDataStream(CylindricalStorageInput.class, typeSource.getOperators())
            .flatMap(
                assetInputEntityData ->
                    buildThermalUnitInputEntityData(assetInputEntityData, thermalBuses)
                        .map(factory::get))
            .collect(Collectors.toSet()),
        CylindricalStorageInput.class);
  }

  public Set<Try<CylindricalStorageInput, FactoryException>> buildCylindricalStorageInputEntities(
      CylindricalStorageInputFactory factory,
      Collection<OperatorInput> operators,
      Collection<ThermalBusInput> thermalBuses) {
    return assetInputEntityDataStream(CylindricalStorageInput.class, operators)
        .flatMap(
            assetInputEntityData ->
                buildThermalUnitInputEntityData(assetInputEntityData, thermalBuses)
                    .map(factory::get))
        .collect(Collectors.toSet());
  }
}
