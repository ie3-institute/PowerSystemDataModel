/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.input.EvcsInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import java.util.Optional;
import java.util.Set;

/**
 * Interface that provides the capability to build entities of type {@link SystemParticipantInput}
 * as well as {@link SystemParticipants} container from .csv files.
 *
 * @version 0.1
 * @since 08.04.20
 */
public interface SystemParticipantSource extends DataSource {

  /**
   * Should return either a consistent instance of {@link SystemParticipants} wrapped in {@link
   * Optional} or an empty {@link Optional}. The decision to use {@link Optional} instead of
   * returning the {@link SystemParticipants} instance directly is motivated by the fact, that a
   * {@link SystemParticipants} is a container instance that depends on several other entities.
   * Without being complete, it is useless for further processing.
   *
   * <p>Hence, whenever at least one entity {@link SystemParticipants} depends on cannot be
   * provided, {@link Optional#empty()} should be returned and extensive logging should provide
   * enough information to debug the error and fix the persistent data that has been failed to
   * processed.
   *
   * <p>Furthermore, it is expected, that the specific implementation of this method ensures not
   * only the completeness of the resulting {@link SystemParticipants} instance, but also its
   * validity e.g. in the sense that not duplicate UUIDs exist within all entities contained in the
   * returning instance.
   *
   * @return either a valid, complete {@link SystemParticipants} optional or {@link
   *     Optional#empty()}
   */
  Optional<SystemParticipants> getSystemParticipants();

  /**
   * Returns a unique set of {@link FixedFeedInInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link FixedFeedInInput} which has to be checked
   * manually, as {@link FixedFeedInInput#equals(Object)} is NOT restricted on the uuid of {@link
   * FixedFeedInInput}.
   *
   * @return a set of object and uuid unique {@link FixedFeedInInput} entities
   */
  Set<FixedFeedInInput> getFixedFeedIns();

  /**
   * Returns a set of {@link FixedFeedInInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link FixedFeedInInput} which has to be checked manually, as {@link
   * FixedFeedInInput#equals(Object)} is NOT restricted on the uuid of {@link FixedFeedInInput}.
   *
   * <p>In contrast to {@link #getFixedFeedIns()} this interface provides the ability to pass in an
   * already existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link
   * FixedFeedInInput} instances depend on. Doing so, already loaded nodes can be recycled to
   * improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link FixedFeedInInput} entities
   */
  Set<FixedFeedInInput> getFixedFeedIns(Set<NodeInput> nodes, Set<OperatorInput> operators);

  /**
   * Returns a unique set of {@link PvInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link PvInput} which has to be checked manually, as
   * {@link PvInput#equals(Object)} is NOT restricted on the uuid of {@link PvInput}.
   *
   * @return a set of object and uuid unique {@link PvInput} entities
   */
  Set<PvInput> getPvPlants();

  /**
   * Returns a set of {@link PvInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * PvInput} which has to be checked manually, as {@link PvInput#equals(Object)} is NOT restricted
   * on the uuid of {@link PvInput}.
   *
   * <p>In contrast to {@link #getPvPlants()} this interface provides the ability to pass in an
   * already existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link
   * PvInput} instances depend on. Doing so, already loaded nodes can be recycled to improve
   * performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link PvInput} entities
   */
  Set<PvInput> getPvPlants(Set<NodeInput> nodes, Set<OperatorInput> operators);

  /**
   * Returns a unique set of {@link LoadInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link LoadInput} which has to be checked manually,
   * as {@link LoadInput#equals(Object)} is NOT restricted on the uuid of {@link LoadInput}.
   *
   * @return a set of object and uuid unique {@link LoadInput} entities
   */
  Set<LoadInput> getLoads();

  /**
   * Returns a set of {@link LoadInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * LoadInput} which has to be checked manually, as {@link LoadInput#equals(Object)} is NOT
   * restricted on the uuid of {@link LoadInput}.
   *
   * <p>In contrast to {@link #getLoads()} this interface provides the ability to pass in an already
   * existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link LoadInput}
   * instances depend on. Doing so, already loaded nodes can be recycled to improve performance and
   * prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link LoadInput} entities
   */
  Set<LoadInput> getLoads(Set<NodeInput> nodes, Set<OperatorInput> operators);

  /**
   * Returns a unique set of {@link EvcsInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EvcsInput} which has to be checked manually,
   * as {@link EvcsInput#equals(Object)} is NOT restricted on the uuid of {@link EvcsInput}.
   *
   * @return a set of object and uuid unique {@link EvcsInput} entities
   */
  Set<EvcsInput> getEvCS();

  /**
   * Returns a set of {@link EvcsInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * EvcsInput} which has to be checked manually, as {@link EvcsInput#equals(Object)} is NOT
   * restricted on the uuid of {@link EvcsInput}.
   *
   * <p>In contrast to {@link #getEvCS()} this interface provides the ability to pass in an already
   * existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link EvcsInput}
   * instances depend on. Doing so, already loaded nodes can be recycled to improve performance and
   * prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link EvcsInput} entities
   */
  Set<EvcsInput> getEvCS(Set<NodeInput> nodes, Set<OperatorInput> operators);

  /**
   * Returns a unique set of {@link BmInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link BmInput} which has to be checked manually, as
   * {@link BmInput#equals(Object)} is NOT restricted on the uuid of {@link BmInput}.
   *
   * @return a set of object and uuid unique {@link BmInput} entities
   */
  Set<BmInput> getBmPlants();

  /**
   * Returns a set of {@link BmInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * BmInput} which has to be checked manually, as {@link BmInput#equals(Object)} is NOT restricted
   * on the uuid of {@link BmInput}.
   *
   * <p>In contrast to {@link #getBmPlants()} this interface provides the ability to pass in an
   * already existing set of {@link NodeInput}, {@link BmTypeInput} and {@link OperatorInput}
   * entities, the {@link BmInput} instances depend on. Doing so, already loaded nodes can be
   * recycled to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param types a set of object and uuid unique {@link BmTypeInput} entities
   * @return a set of object and uuid unique {@link BmInput} entities
   */
  Set<BmInput> getBmPlants(
      Set<NodeInput> nodes, Set<OperatorInput> operators, Set<BmTypeInput> types);

  /**
   * Returns a unique set of {@link StorageInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link StorageInput} which has to be checked
   * manually, as {@link StorageInput#equals(Object)} is NOT restricted on the uuid of {@link
   * StorageInput}.
   *
   * @return a set of object and uuid unique {@link StorageInput} entities
   */
  Set<StorageInput> getStorages();

  /**
   * Returns a set of {@link StorageInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link StorageInput} which has to be checked manually, as {@link StorageInput#equals(Object)}
   * is NOT restricted on the uuid of {@link StorageInput}.
   *
   * <p>In contrast to {@link #getStorages()} this interface provides the ability to pass in an
   * already existing set of {@link NodeInput}, {@link StorageTypeInput} and {@link OperatorInput}
   * entities, the {@link StorageInput} instances depend on. Doing so, already loaded nodes can be
   * recycled to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param types a set of object and uuid unique {@link StorageTypeInput} entities
   * @return a set of object and uuid unique {@link StorageInput} entities
   */
  Set<StorageInput> getStorages(
      Set<NodeInput> nodes, Set<OperatorInput> operators, Set<StorageTypeInput> types);

  /**
   * Returns a unique set of {@link WecInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link WecInput} which has to be checked manually,
   * as {@link WecInput#equals(Object)} is NOT restricted on the uuid of {@link WecInput}.
   *
   * @return a set of object and uuid unique {@link WecInput} entities
   */
  Set<WecInput> getWecPlants();

  /**
   * Returns a set of {@link WecInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * WecInput} which has to be checked manually, as {@link WecInput#equals(Object)} is NOT
   * restricted on the uuid of {@link WecInput}.
   *
   * <p>In contrast to {@link #getWecPlants()} this interface provides the ability to pass in an
   * already existing set of {@link NodeInput}, {@link WecTypeInput} and {@link OperatorInput}
   * entities, the {@link WecInput} instances depend on. Doing so, already loaded nodes can be
   * recycled to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param types a set of object and uuid unique {@link WecTypeInput} entities
   * @return a set of object and uuid unique {@link WecInput} entities
   */
  Set<WecInput> getWecPlants(
      Set<NodeInput> nodes, Set<OperatorInput> operators, Set<WecTypeInput> types);

  /**
   * Returns a unique set of {@link EvInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EvInput} which has to be checked manually, as
   * {@link EvInput#equals(Object)} is NOT restricted on the uuid of {@link EvInput}.
   *
   * @return a set of object and uuid unique {@link EvInput} entities
   */
  Set<EvInput> getEvs();

  /**
   * Returns a set of {@link EvInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * EvInput} which has to be checked manually, as {@link EvInput#equals(Object)} is NOT restricted
   * on the uuid of {@link EvInput}.
   *
   * <p>In contrast to {@link #getEvs()} this interface provides the ability to pass in an already
   * existing set of {@link NodeInput}, {@link EvTypeInput} and {@link OperatorInput} entities, the
   * {@link EvInput} instances depend on. Doing so, already loaded nodes can be recycled to improve
   * performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param types a set of object and uuid unique {@link EvTypeInput} entities
   * @return a set of object and uuid unique {@link EvInput} entities
   */
  Set<EvInput> getEvs(Set<NodeInput> nodes, Set<OperatorInput> operators, Set<EvTypeInput> types);

  /**
   * Returns a unique set of {@link ChpInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link ChpInput} which has to be checked manually,
   * as {@link ChpInput#equals(Object)} is NOT restricted on the uuid of {@link ChpInput}.
   *
   * @return a set of object and uuid unique {@link ChpInput} entities
   */
  Set<ChpInput> getChpPlants();

  /**
   * Returns a set of {@link ChpInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * ChpInput} which has to be checked manually, as {@link ChpInput#equals(Object)} is NOT
   * restricted on the uuid of {@link ChpInput}.
   *
   * <p>In contrast to {@link #getChpPlants()} this interface provides the ability to pass in an
   * already existing set of {@link NodeInput}, {@link ChpTypeInput}, {@link ThermalBusInput},
   * {@link ThermalStorageInput} and {@link OperatorInput} entities, the {@link ChpInput} instances
   * depend on. Doing so, already loaded nodes can be recycled to improve performance and prevent
   * unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param types a set of object and uuid unique {@link ChpTypeInput} entities
   * @param thermalBuses a set of object and uuid unique {@link ThermalBusInput} entities
   * @param thermalStorages a set of object and uuid unique {@link ThermalStorageInput} entities
   * @return a set of object and uuid unique {@link ChpInput} entities
   */
  Set<ChpInput> getChpPlants(
      Set<NodeInput> nodes,
      Set<OperatorInput> operators,
      Set<ChpTypeInput> types,
      Set<ThermalBusInput> thermalBuses,
      Set<ThermalStorageInput> thermalStorages);

  /**
   * Returns a unique set of {@link HpInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link HpInput} which has to be checked manually, as
   * {@link HpInput#equals(Object)} is NOT restricted on the uuid of {@link HpInput}.
   *
   * @return a set of object and uuid unique {@link HpInput} entities
   */
  Set<HpInput> getHeatPumps();

  /**
   * Returns a set of {@link HpInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * HpInput} which has to be checked manually, as {@link HpInput#equals(Object)} is NOT restricted
   * on the uuid of {@link HpInput}.
   *
   * <p>In contrast to {@link #getHeatPumps()} this interface provides the ability to pass in an
   * already existing set of {@link NodeInput}, {@link HpTypeInput}, {@link ThermalBusInput}, {@link
   * ThermalStorageInput} and {@link OperatorInput} entities, the {@link HpInput} instances depend
   * on. Doing so, already loaded nodes can be recycled to improve performance and prevent
   * unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param types a set of object and uuid unique {@link HpTypeInput} entities
   * @param thermalBuses a set of object and uuid unique {@link ThermalBusInput} entities
   * @return a set of object and uuid unique {@link HpInput} entities
   */
  Set<HpInput> getHeatPumps(
      Set<NodeInput> nodes,
      Set<OperatorInput> operators,
      Set<HpTypeInput> types,
      Set<ThermalBusInput> thermalBuses);
}
