/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.factory.SimpleEntityFactory;
import edu.ie3.datamodel.io.factory.result.*;
import edu.ie3.datamodel.models.result.NodeResult;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.result.connector.LineResult;
import edu.ie3.datamodel.models.result.connector.SwitchResult;
import edu.ie3.datamodel.models.result.connector.Transformer2WResult;
import edu.ie3.datamodel.models.result.connector.Transformer3WResult;
import edu.ie3.datamodel.models.result.system.*;
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult;
import edu.ie3.datamodel.models.result.thermal.ThermalHouseResult;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Interface that provides the capability to build entities of type {@link ResultEntity} container
 * from .csv files.
 *
 * @version 0.1
 * @since 22 June 2021
 */
public class ResultEntitySource extends EntitySource {

  private final SystemParticipantResultFactory systemParticipantResultFactory;
  private final ThermalResultFactory thermalResultFactory;
  private final SwitchResultFactory switchResultFactory;
  private final NodeResultFactory nodeResultFactory;
  private final ConnectorResultFactory connectorResultFactory;
  private final FlexOptionsResultFactory flexOptionsResultFactory;

  public ResultEntitySource(DataSource dataSource) {
    this.dataSource = dataSource;

    // init factories
    this.systemParticipantResultFactory = new SystemParticipantResultFactory();
    this.thermalResultFactory = new ThermalResultFactory();
    this.switchResultFactory = new SwitchResultFactory();
    this.nodeResultFactory = new NodeResultFactory();
    this.connectorResultFactory = new ConnectorResultFactory();
    this.flexOptionsResultFactory = new FlexOptionsResultFactory();
  }

  public ResultEntitySource(DataSource dataSource, String dtfPattern) {
    this.dataSource = dataSource;

    // init factories
    this.systemParticipantResultFactory = new SystemParticipantResultFactory(dtfPattern);
    this.thermalResultFactory = new ThermalResultFactory();
    this.switchResultFactory = new SwitchResultFactory();
    this.nodeResultFactory = new NodeResultFactory();
    this.connectorResultFactory = new ConnectorResultFactory();
    this.flexOptionsResultFactory = new FlexOptionsResultFactory();
  }

  /**
   * Returns a unique set of {@link NodeResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link NodeResult} which has to be checked manually,
   * as {@link NodeResult#equals(Object)} is NOT restricted by the uuid of {@link NodeResult}.
   *
   * @return a set of object and uuid unique {@link NodeResult} entities
   */
  public Set<NodeResult> getNodeResults() {
    return getResultEntities(NodeResult.class, nodeResultFactory);
  }

  /**
   * Returns a unique set of {@link SwitchResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link SwitchResult} which has to be checked
   * manually, as {@link SwitchResult#equals(Object)} is NOT restricted by the uuid of {@link
   * SwitchResult}.
   *
   * @return a set of object and uuid unique {@link SwitchResult} entities
   */
  public Set<SwitchResult> getSwitchResults() {
    return getResultEntities(SwitchResult.class, switchResultFactory);
  }

  /**
   * Returns a unique set of {@link LineResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link LineResult} which has to be checked manually,
   * as {@link LineResult#equals(Object)} is NOT restricted by the uuid of {@link LineResult}.
   *
   * @return a set of object and uuid unique {@link LineResult} entities
   */
  public Set<LineResult> getLineResults() {
    return getResultEntities(LineResult.class, connectorResultFactory);
  }

  /**
   * Returns a unique set of {@link Transformer2WResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link Transformer2WResult} which has to be checked
   * manually, as {@link Transformer2WResult#equals(Object)} is NOT restricted by the uuid of {@link
   * Transformer2WResult}.
   *
   * @return a set of object and uuid unique {@link Transformer2WResult} entities
   */
  public Set<Transformer2WResult> getTransformer2WResultResults() {
    return getResultEntities(Transformer2WResult.class, connectorResultFactory);
  }

  /**
   * Returns a unique set of {@link Transformer3WResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link Transformer3WResult} which has to be checked
   * manually, as {@link Transformer3WResult#equals(Object)} is NOT restricted by the uuid of {@link
   * Transformer3WResult}.
   *
   * @return a set of object and uuid unique {@link Transformer3WResult} entities
   */
  public Set<Transformer3WResult> getTransformer3WResultResults() {
    return getResultEntities(Transformer3WResult.class, connectorResultFactory);
  }

  /**
   * Returns a unique set of {@link FlexOptionsResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link FlexOptionsResult} which has to be checked
   * manually, as {@link FlexOptionsResult#equals(Object)} is NOT restricted by the uuid of {@link
   * FlexOptionsResult}.
   *
   * @return a set of object and uuid unique {@link FlexOptionsResult} entities
   */
  public Set<FlexOptionsResult> getFlexOptionsResults() {
    return getResultEntities(FlexOptionsResult.class, flexOptionsResultFactory);
  }

  /**
   * Returns a unique set of {@link LoadResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link LoadResult} which has to be checked manually,
   * as {@link LoadResult#equals(Object)} is NOT restricted by the uuid of {@link LoadResult}.
   *
   * @return a set of object and uuid unique {@link LoadResult} entities
   */
  public Set<LoadResult> getLoadResults() {
    return getResultEntities(LoadResult.class, systemParticipantResultFactory);
  }

  /**
   * Returns a unique set of {@link PvResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link PvResult} which has to be checked manually,
   * as {@link PvResult#equals(Object)} is NOT restricted by the uuid of {@link PvResult}.
   *
   * @return a set of object and uuid unique {@link PvResult} entities
   */
  public Set<PvResult> getPvResults() {
    return getResultEntities(PvResult.class, systemParticipantResultFactory);
  }

  /**
   * Returns a unique set of {@link FixedFeedInResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link FixedFeedInResult} which has to be checked
   * manually, as {@link FixedFeedInResult#equals(Object)} is NOT restricted by the uuid of {@link
   * FixedFeedInResult}.
   *
   * @return a set of object and uuid unique {@link FixedFeedInResult} entities
   */
  public Set<FixedFeedInResult> getFixedFeedInResults() {
    return getResultEntities(FixedFeedInResult.class, systemParticipantResultFactory);
  }

  /**
   * Returns a unique set of {@link BmResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link BmResult} which has to be checked manually,
   * as {@link BmResult#equals(Object)} is NOT restricted by the uuid of {@link BmResult}.
   *
   * @return a set of object and uuid unique {@link BmResult} entities
   */
  public Set<BmResult> getBmResults() {
    return getResultEntities(BmResult.class, systemParticipantResultFactory);
  }

  /**
   * Returns a unique set of {@link ChpResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link ChpResult} which has to be checked manually,
   * as {@link ChpResult#equals(Object)} is NOT restricted by the uuid of {@link ChpResult}.
   *
   * @return a set of object and uuid unique {@link ChpResult} entities
   */
  public Set<ChpResult> getChpResults() {
    return getResultEntities(ChpResult.class, systemParticipantResultFactory);
  }

  /**
   * Returns a unique set of {@link WecResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link WecResult} which has to be checked manually,
   * as {@link WecResult#equals(Object)} is NOT restricted by the uuid of {@link WecResult}.
   *
   * @return a set of object and uuid unique {@link WecResult} entities
   */
  public Set<WecResult> getWecResults() {
    return getResultEntities(WecResult.class, systemParticipantResultFactory);
  }

  /**
   * Returns a unique set of {@link StorageResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link StorageResult} which has to be checked
   * manually, as {@link StorageResult#equals(Object)} is NOT restricted by the uuid of {@link
   * StorageResult}.
   *
   * @return a set of object and uuid unique {@link StorageResult} entities
   */
  public Set<StorageResult> getStorageResults() {
    return getResultEntities(StorageResult.class, systemParticipantResultFactory);
  }

  /**
   * Returns a unique set of {@link EvcsResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EvcsResult} which has to be checked manually,
   * as {@link EvcsResult#equals(Object)} is NOT restricted by the uuid of {@link EvcsResult}.
   *
   * @return a set of object and uuid unique {@link EvcsResult} entities
   */
  public Set<EvcsResult> getEvcsResults() {
    return getResultEntities(EvcsResult.class, systemParticipantResultFactory);
  }

  /**
   * Returns a unique set of {@link EvResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EvResult} which has to be checked manually,
   * as {@link EvResult#equals(Object)} is NOT restricted by the uuid of {@link EvResult}.
   *
   * @return a set of object and uuid unique {@link EvResult} entities
   */
  public Set<EvResult> getEvResults() {
    return getResultEntities(EvResult.class, systemParticipantResultFactory);
  }

  /**
   * Returns a unique set of {@link HpResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link HpResult} which has to be checked manually,
   * as {@link HpResult#equals(Object)} is NOT restricted by the uuid of {@link HpResult}.
   *
   * @return a set of object and uuid unique {@link HpResult} entities
   */
  public Set<HpResult> getHpResults() {
    return getResultEntities(HpResult.class, systemParticipantResultFactory);
  }

  /**
   * Returns a unique set of {@link CylindricalStorageResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link CylindricalStorageResult} which has to be
   * checked manually, as {@link CylindricalStorageResult#equals(Object)} is NOT restricted by the
   * uuid of {@link CylindricalStorageResult}.
   *
   * @return a set of object and uuid unique {@link CylindricalStorageResult} entities
   */
  public Set<CylindricalStorageResult> getCylindricalStorageResult() {
    return getResultEntities(CylindricalStorageResult.class, thermalResultFactory);
  }

  /**
   * Returns a unique set of {@link ThermalHouseResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link ThermalHouseResult} which has to be checked
   * manually, as {@link ThermalHouseResult#equals(Object)} is NOT restricted by the uuid of {@link
   * ThermalHouseResult}.
   *
   * @return a set of object and uuid unique {@link ThermalHouseResult} entities
   */
  public Set<ThermalHouseResult> getThermalHouseResults() {
    return getResultEntities(ThermalHouseResult.class, thermalResultFactory);
  }

  /**
   * Returns a unique set of {@link EmResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EmResult} which has to be checked manually,
   * as {@link EmResult#equals(Object)} is NOT restricted by the uuid of {@link EmResult}.
   *
   * @return a set of object and uuid unique {@link EmResult} entities
   */
  public Set<EmResult> getEmResults() {
    return getResultEntities(EmResult.class, systemParticipantResultFactory);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  private <T extends ResultEntity> Set<T> getResultEntities(
      Class<T> entityClass, SimpleEntityFactory<? extends ResultEntity> factory) {
    return simpleEntityDataStream(entityClass)
        .map(
            entityData ->
                factory
                    .get(entityData)
                    .getData()
                    .flatMap(loadResult -> cast(entityClass, loadResult)))
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

  private <T extends ResultEntity> Optional<T> cast(
      Class<T> entityClass, ResultEntity resultEntity) {
    if (resultEntity.getClass().equals(entityClass)) {
      // safe here as a) type is checked and b) csv data stream already filters non-fitting input
      // data
      return Optional.of(entityClass.cast(resultEntity));
    } else {
      return Optional.empty();
    }
  }
}
