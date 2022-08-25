/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.result.NodeResult;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.result.connector.LineResult;
import edu.ie3.datamodel.models.result.connector.SwitchResult;
import edu.ie3.datamodel.models.result.connector.Transformer2WResult;
import edu.ie3.datamodel.models.result.connector.Transformer3WResult;
import edu.ie3.datamodel.models.result.system.*;
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult;
import edu.ie3.datamodel.models.result.thermal.ThermalHouseResult;
import java.util.Set;

/**
 * Interface that provides the capability to build entities of type {@link ResultEntity} container
 * from .csv files.
 *
 * @version 0.1
 * @since 22 June 2021
 */
public interface ResultEntitySource {

  /**
   * Returns a unique set of {@link NodeResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link NodeResult} which has to be checked manually,
   * as {@link NodeResult#equals(Object)} is NOT restricted by the uuid of {@link NodeResult}.
   *
   * @return a set of object and uuid unique {@link NodeResult} entities
   */
  Set<NodeResult> getNodeResults();

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
  Set<SwitchResult> getSwitchResults();

  /**
   * Returns a unique set of {@link LineResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link LineResult} which has to be checked manually,
   * as {@link LineResult#equals(Object)} is NOT restricted by the uuid of {@link LineResult}.
   *
   * @return a set of object and uuid unique {@link LineResult} entities
   */
  Set<LineResult> getLineResults();

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
  Set<Transformer2WResult> getTransformer2WResultResults();

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
  Set<Transformer3WResult> getTransformer3WResultResults();

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
  Set<FlexOptionsResult> getFlexOptionsResults();

  /**
   * Returns a unique set of {@link LoadResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link LoadResult} which has to be checked manually,
   * as {@link LoadResult#equals(Object)} is NOT restricted by the uuid of {@link LoadResult}.
   *
   * @return a set of object and uuid unique {@link LoadResult} entities
   */
  Set<LoadResult> getLoadResults();

  /**
   * Returns a unique set of {@link PvResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link PvResult} which has to be checked manually,
   * as {@link PvResult#equals(Object)} is NOT restricted by the uuid of {@link PvResult}.
   *
   * @return a set of object and uuid unique {@link PvResult} entities
   */
  Set<PvResult> getPvResults();

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
  Set<FixedFeedInResult> getFixedFeedInResults();

  /**
   * Returns a unique set of {@link BmResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link BmResult} which has to be checked manually,
   * as {@link BmResult#equals(Object)} is NOT restricted by the uuid of {@link BmResult}.
   *
   * @return a set of object and uuid unique {@link BmResult} entities
   */
  Set<BmResult> getBmResults();

  /**
   * Returns a unique set of {@link ChpResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link ChpResult} which has to be checked manually,
   * as {@link ChpResult#equals(Object)} is NOT restricted by the uuid of {@link ChpResult}.
   *
   * @return a set of object and uuid unique {@link ChpResult} entities
   */
  Set<ChpResult> getChpResults();

  /**
   * Returns a unique set of {@link WecResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link WecResult} which has to be checked manually,
   * as {@link WecResult#equals(Object)} is NOT restricted by the uuid of {@link WecResult}.
   *
   * @return a set of object and uuid unique {@link WecResult} entities
   */
  Set<WecResult> getWecResults();

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
  Set<StorageResult> getStorageResults();

  /**
   * Returns a unique set of {@link EvcsResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EvcsResult} which has to be checked manually,
   * as {@link EvcsResult#equals(Object)} is NOT restricted by the uuid of {@link EvcsResult}.
   *
   * @return a set of object and uuid unique {@link EvcsResult} entities
   */
  Set<EvcsResult> getEvcsResults();

  /**
   * Returns a unique set of {@link EvResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EvResult} which has to be checked manually,
   * as {@link EvResult#equals(Object)} is NOT restricted by the uuid of {@link EvResult}.
   *
   * @return a set of object and uuid unique {@link EvResult} entities
   */
  Set<EvResult> getEvResults();

  /**
   * Returns a unique set of {@link HpResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link HpResult} which has to be checked manually,
   * as {@link HpResult#equals(Object)} is NOT restricted by the uuid of {@link HpResult}.
   *
   * @return a set of object and uuid unique {@link HpResult} entities
   */
  Set<HpResult> getHpResults();

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
  Set<CylindricalStorageResult> getCylindricalStorageResult();

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
  Set<ThermalHouseResult> getThermalHouseResults();

  /**
   * Returns a unique set of {@link EmResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EmResult} which has to be checked manually,
   * as {@link EmResult#equals(Object)} is NOT restricted by the uuid of {@link EmResult}.
   *
   * @return a set of object and uuid unique {@link EmResult} entities
   */
  Set<EmResult> getEmResults();
}
