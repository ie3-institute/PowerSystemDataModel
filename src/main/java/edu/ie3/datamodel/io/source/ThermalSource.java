/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.input.*;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import edu.ie3.datamodel.utils.Try;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
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
    super(dataSource);
    this.typeSource = typeSource;

    this.thermalBusInputFactory = new ThermalBusInputFactory();
    this.cylindricalStorageInputFactory = new CylindricalStorageInputFactory();
    this.thermalHouseInputFactory = new ThermalHouseInputFactory();
  }

  /**
   * Returns a unique set of {@link ThermalBusInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link ThermalBusInput} which has to be checked
   * manually, as {@link ThermalBusInput#equals(Object)} is NOT restricted on the uuid of {@link
   * ThermalBusInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link ThermalBusInput} entities
   */
  public Map<UUID, ThermalBusInput> getThermalBuses() throws SourceException {
    return getThermalBuses(typeSource.getOperators());
  }

  /**
   * Returns a unique set of {@link ThermalBusInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link ThermalBusInput} which has to be checked
   * manually, as {@link ThermalBusInput#equals(Object)} is NOT restricted on the uuid of {@link
   * ThermalBusInput}.
   *
   * <p>In contrast to {@link #getThermalBuses()} this interface provides the ability to pass in an
   * already existing set of {@link OperatorInput} entities, the {@link ThermalBusInput} instances
   * depend on. Doing so, already loaded nodes can be recycled to improve performance and prevent
   * unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object- and uuid-unique {@link OperatorInput} entities
   * @return a map of UUID to object- and uuid-unique {@link ThermalBusInput} entities
   */
  public Map<UUID, ThermalBusInput> getThermalBuses(Map<UUID, OperatorInput> operators)
      throws SourceException {
    return unpackMap(
        buildAssetInputEntityData(ThermalBusInput.class, operators)
            .map(thermalBusInputFactory::get),
        ThermalBusInput.class);
  }

  /**
   * Returns a unique set of instances of all entities implementing the {@link ThermalStorageInput}
   * abstract class within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link ThermalStorageInput} which has to be checked
   * manually, as {@link ThermalStorageInput#equals(Object)} is NOT restricted on the uuid of {@link
   * ThermalStorageInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link ThermalStorageInput} entities
   */
  public Map<UUID, ThermalStorageInput> getThermalStorages() throws SourceException {
    return getCylindricalStorages().stream()
        .collect(Collectors.toMap(UniqueEntity::getUuid, Function.identity()));
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
   * @param operators a set of object- and uuid-unique {@link OperatorInput} entities
   * @param thermalBuses a set of object- and uuid-unique {@link ThermalBusInput} entities
   * @return a map of UUID to object- and uuid-unique {@link ThermalStorageInput} entities
   */
  public Map<UUID, ThermalStorageInput> getThermalStorages(
      Map<UUID, OperatorInput> operators, Map<UUID, ThermalBusInput> thermalBuses)
      throws SourceException {
    return getCylindricalStorages(operators, thermalBuses).stream()
        .collect(Collectors.toMap(UniqueEntity::getUuid, Function.identity()));
  }

  /**
   * Returns a unique set of {@link ThermalHouseInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link ThermalHouseInput} which has to be checked
   * manually, as {@link ThermalHouseInput#equals(Object)} is NOT restricted on the uuid of {@link
   * ThermalHouseInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link ThermalHouseInput} entities
   */
  public Map<UUID, ThermalHouseInput> getThermalHouses() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, ThermalBusInput> thermalBuses = getThermalBuses();

    return getThermalHouses(operators, thermalBuses);
  }

  /**
   * Returns a set of {@link ThermalHouseInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link ThermalHouseInput} which has to be checked
   * manually, as {@link ThermalHouseInput#equals(Object)} is NOT restricted on the uuid of {@link
   * ThermalHouseInput}.
   *
   * <p>In contrast to {@link #getThermalHouses()} this interface provides the ability to pass in an
   * already existing set of {@link OperatorInput} entities, the {@link ThermalHouseInput} instances
   * depend on. Doing so, already loaded nodes can be recycled to improve performance and prevent
   * unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object- and uuid-unique {@link OperatorInput} entities
   * @param thermalBuses a set of object- and uuid-unique {@link ThermalBusInput} entities
   * @return a map of UUID to object- and uuid-unique {@link ThermalHouseInput} entities
   */
  public Map<UUID, ThermalHouseInput> getThermalHouses(
      Map<UUID, OperatorInput> operators, Map<UUID, ThermalBusInput> thermalBuses)
      throws SourceException {
    return unpackMap(
        thermalUnitInputEntityDataStream(
                buildAssetInputEntityData(ThermalHouseInput.class, operators), thermalBuses)
            .map(thermalHouseInputFactory::get),
        ThermalHouseInput.class);
  }

  /**
   * Returns a unique set of {@link CylindricalStorageInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link CylindricalStorageInput} which has to be
   * checked manually, as {@link CylindricalStorageInput#equals(Object)} is NOT restricted on the
   * uuid of {@link CylindricalStorageInput}.
   *
   * @return a set of object- and uuid-unique {@link CylindricalStorageInput} entities
   */
  public Set<CylindricalStorageInput> getCylindricalStorages() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, ThermalBusInput> thermalBuses = getThermalBuses();

    return getCylindricalStorages(operators, thermalBuses);
  }

  /**
   * Returns a set of {@link CylindricalStorageInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link CylindricalStorageInput} which has to be
   * checked manually, as {@link CylindricalStorageInput#equals(Object)} is NOT restricted on the
   * uuid of {@link CylindricalStorageInput}.
   *
   * <p>In contrast to {@link #getCylindricalStorages()} this interface provides the ability to pass
   * in an already existing set of {@link OperatorInput} entities, the {@link
   * CylindricalStorageInput} instances depend on. Doing so, already loaded nodes can be recycled to
   * improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object- and uuid-unique {@link OperatorInput} entities
   * @param thermalBuses a set of object- and uuid-unique {@link ThermalBusInput} entities
   * @return a set of object- and uuid-unique {@link CylindricalStorageInput} entities
   */
  public Set<CylindricalStorageInput> getCylindricalStorages(
      Map<UUID, OperatorInput> operators, Map<UUID, ThermalBusInput> thermalBuses)
      throws SourceException {
    return unpackSet(
        thermalUnitInputEntityDataStream(
                buildAssetInputEntityData(CylindricalStorageInput.class, operators), thermalBuses)
            .map(cylindricalStorageInputFactory::get),
        CylindricalStorageInput.class);
  }

  /**
   * Enriches a given stream of {@link AssetInputEntityData} {@link Try} objects with a type of
   * {@link ThermalBusInput} based on the provided collection of types and the fields to values
   * mapping that inside the already provided {@link AssetInputEntityData} instance.
   *
   * @param assetInputEntityDataStream the data stream of {@link AssetInputEntityData} {@link Try}
   *     objects
   * @param thermalBuses the thermal buses that should be used for enrichment and to build {@link
   *     ThermalUnitInputEntityData} from
   * @return a stream of tries of {@link ThermalUnitInputEntityData} instances
   */
  private static Stream<Try<ThermalUnitInputEntityData, SourceException>>
      thermalUnitInputEntityDataStream(
          Stream<Try<AssetInputEntityData, SourceException>> assetInputEntityDataStream,
          Map<UUID, ThermalBusInput> thermalBuses) {
    return assetInputEntityDataStream
        .parallel()
        .map(
            assetInputEntityDataTry ->
                assetInputEntityDataTry.flatMap(
                    assetInputEntityData ->
                        enrichEntityData(
                            assetInputEntityData,
                            "thermalbus",
                            thermalBuses,
                            ThermalUnitInputEntityData::new)));
  }
}
