/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.thermal.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Interface that provides the capability to build thermal {@link
 * edu.ie3.datamodel.models.input.AssetInput} entities from persistent data e.g. .csv files or
 * databases
 *
 * @version 0.1
 * @since 08.04.20
 */
public class ThermalSource extends AssetEntitySource {
  // general fields
  private final TypeSource typeSource;

  public ThermalSource(TypeSource typeSource, DataSource dataSource) {
    super(dataSource);
    this.typeSource = typeSource;
  }

  @Override
  public void validate() throws ValidationException {
    validate(
        dataSource,
        ThermalBusInput.class,
        CylindricalStorageInput.class,
        DomesticHotWaterStorageInput.class,
        ThermalHouseInput.class);
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
    return getEntities(ThermalBusInput.class, dataSource, thermalBusBuilder(operators))
        .collect(toMap());
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
    return Stream.of(getCylindricalStorages(), getDomesticHotWaterStorages())
        .flatMap(Collection::stream)
        .collect(toMap());
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
    return Stream.of(
            getCylindricalStorages(operators, thermalBuses),
            getDomesticHotWaterStorages(operators, thermalBuses))
        .flatMap(Collection::stream)
        .collect(toMap());
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
    return getEntities(
            ThermalHouseInput.class, dataSource, thermalHouseBuildFunction(operators, thermalBuses))
        .collect(toMap());
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
   * Returns a unique set of {@link DomesticHotWaterStorageInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link DomesticHotWaterStorageInput} which has to be
   * checked manually, as {@link DomesticHotWaterStorageInput#equals(Object)} is NOT restricted on
   * the uuid of {@link DomesticHotWaterStorageInput}.
   *
   * @return a set of object- and uuid-unique {@link DomesticHotWaterStorageInput} entities
   */
  public Set<DomesticHotWaterStorageInput> getDomesticHotWaterStorages() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, ThermalBusInput> thermalBuses = getThermalBuses();

    return getDomesticHotWaterStorages(operators, thermalBuses);
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
    return getEntities(
            CylindricalStorageInput.class,
            dataSource,
            cylindricalStorageBuildFunction(operators, thermalBuses))
        .collect(toSet());
  }

  /**
   * Returns a set of {@link DomesticHotWaterStorageInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link DomesticHotWaterStorageInput} which has to be
   * checked manually, as {@link DomesticHotWaterStorageInput#equals(Object)} is NOT restricted on
   * the uuid of {@link DomesticHotWaterStorageInput}.
   *
   * <p>In contrast to {@link #getDomesticHotWaterStorages()} this interface provides the ability to
   * pass in an already existing set of {@link OperatorInput} entities, the {@link
   * DomesticHotWaterStorageInput} instances depend on. Doing so, already loaded nodes can be
   * recycled to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object- and uuid-unique {@link OperatorInput} entities
   * @param thermalBuses a set of object- and uuid-unique {@link ThermalBusInput} entities
   * @return a set of object- and uuid-unique {@link DomesticHotWaterStorageInput} entities
   */
  public Set<DomesticHotWaterStorageInput> getDomesticHotWaterStorages(
      Map<UUID, OperatorInput> operators, Map<UUID, ThermalBusInput> thermalBuses)
      throws SourceException {
    return getEntities(
            DomesticHotWaterStorageInput.class,
            dataSource,
            dhwsBuildFunction(operators, thermalBuses))
        .collect(toSet());
  }

  // build function
  protected static BuildFunction<ThermalInput> thermalBuilder(Map<UUID, OperatorInput> operators) {
    return entityData ->
        entityData
            .zip(assetBuilder(operators))
            .map(
                pair ->
                    new ThermalInput(pair.getRight()) {
                      @Override
                      public AssetInputCopyBuilder<?> copy() {
                        return null;
                      }
                    },
                SourceException.class);
  }

  protected static BuildFunction<ThermalBusInput> thermalBusBuilder(
      Map<UUID, OperatorInput> operators) {
    return thermalBuilder(operators).with(pair -> new ThermalBusInput(pair.getRight()));
  }

  protected static BuildFunction<ThermalUnitInput> thermalUnitBuilder(
      Map<UUID, OperatorInput> operators, Map<UUID, ThermalBusInput> thermalBuses) {
    return entityData ->
        entityData
            .zip(thermalBuilder(operators))
            .map(
                pair ->
                    new ThermalUnitInput(
                        pair.getRight(),
                        extractFunction(
                            pair.getLeft(), ThermalUnitInput.THERMAL_BUS, thermalBuses)) {
                      @Override
                      public ThermalUnitInputCopyBuilder<?> copy() {
                        return null;
                      }
                    },
                SourceException.class);
  }

  protected static BuildFunction<ThermalHouseInput> thermalHouseBuildFunction(
      Map<UUID, OperatorInput> operators, Map<UUID, ThermalBusInput> thermalBuses) {
    return thermalUnitBuilder(operators, thermalBuses)
        .with(
            pair -> {
              EntityData data = pair.getLeft();
              return new ThermalHouseInput(
                  pair.getRight(),
                  data.getQuantity(
                      ThermalHouseInput.ETH_LOSSES, StandardUnits.THERMAL_TRANSMISSION),
                  data.getQuantity(ThermalHouseInput.ETH_CAPA, StandardUnits.HEAT_CAPACITY),
                  data.getQuantity(ThermalHouseInput.TARGET_TEMPERATURE, StandardUnits.TEMPERATURE),
                  data.getQuantity(
                      ThermalHouseInput.UPPER_TEMPERATURE_LIMIT, StandardUnits.TEMPERATURE),
                  data.getQuantity(
                      ThermalHouseInput.LOWER_TEMPERATURE_LIMIT, StandardUnits.TEMPERATURE),
                  data.getField(ThermalHouseInput.HOUSING_TYPE),
                  data.getDouble(ThermalHouseInput.NUMBER_INHABITANTS));
            });
  }

  protected static BuildFunction<AbstractStorageInput> thermalStorageBuilder(
      Map<UUID, OperatorInput> operators, Map<UUID, ThermalBusInput> thermalBuses) {
    return entityData ->
        entityData
            .zip(thermalUnitBuilder(operators, thermalBuses))
            .map(
                pair -> {
                  EntityData data = pair.getLeft();
                  return new AbstractStorageInput(
                      pair.getRight(),
                      data.getQuantity(
                          AbstractStorageInput.STORAGE_VOLUME_LVL, StandardUnits.VOLUME),
                      data.getQuantity(AbstractStorageInput.INLET_TEMP, StandardUnits.TEMPERATURE),
                      data.getQuantity(AbstractStorageInput.RETURN_TEMP, StandardUnits.TEMPERATURE),
                      data.getQuantity(
                          AbstractStorageInput.C, StandardUnits.SPECIFIC_HEAT_CAPACITY),
                      data.getQuantity(
                          AbstractStorageInput.P_THERMAL_MAX, StandardUnits.ACTIVE_POWER_IN)) {
                    @Override
                    public ThermalStorageInputCopyBuilder<?> copy() {
                      return null;
                    }
                  };
                },
                SourceException.class);
  }

  protected static BuildFunction<CylindricalStorageInput> cylindricalStorageBuildFunction(
      Map<UUID, OperatorInput> operators, Map<UUID, ThermalBusInput> thermalBuses) {
    return thermalStorageBuilder(operators, thermalBuses)
        .with(pair -> new CylindricalStorageInput(pair.getRight()));
  }

  protected static BuildFunction<DomesticHotWaterStorageInput> dhwsBuildFunction(
      Map<UUID, OperatorInput> operators, Map<UUID, ThermalBusInput> thermalBuses) {
    return thermalStorageBuilder(operators, thermalBuses)
        .with(pair -> new DomesticHotWaterStorageInput(pair.getRight()));
  }
}
