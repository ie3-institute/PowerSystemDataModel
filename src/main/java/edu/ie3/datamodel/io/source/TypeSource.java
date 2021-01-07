/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.system.type.*;
import java.util.Set;

/**
 * Interface that provides the capability to build entities of type {@link
 * SystemParticipantTypeInput} and {@link OperatorInput} from different data sources e.g. .csv files
 * or databases
 *
 * @version 0.1
 * @since 08.04.20
 */
public interface TypeSource extends DataSource {

  /**
   * Returns a set of {@link Transformer2WTypeInput} instances. This set has to be unique in the
   * sense of object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the
   * provided {@link Transformer2WTypeInput} which has to be checked manually, as {@link
   * Transformer2WTypeInput#equals(Object)} is NOT restricted on the uuid of {@link
   * Transformer2WTypeInput}.
   *
   * @return a set of object and uuid unique {@link Transformer2WTypeInput} entities
   */
  Set<Transformer2WTypeInput> getTransformer2WTypes();

  /**
   * Returns a set of {@link OperatorInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link OperatorInput} which has to be checked manually, as {@link OperatorInput#equals(Object)}
   * is NOT restricted on the uuid of {@link OperatorInput}.
   *
   * @return a set of object and uuid unique {@link OperatorInput} entities
   */
  Set<OperatorInput> getOperators();

  /**
   * Returns a set of {@link LineTypeInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link LineTypeInput} which has to be checked manually, as {@link LineTypeInput#equals(Object)}
   * is NOT restricted on the uuid of {@link LineTypeInput}.
   *
   * @return a set of object and uuid unique {@link LineTypeInput} entities
   */
  Set<LineTypeInput> getLineTypes();

  /**
   * Returns a set of {@link Transformer3WTypeInput} instances. This set has to be unique in the
   * sense of object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the
   * provided {@link Transformer3WTypeInput} which has to be checked manually, as {@link
   * Transformer3WTypeInput#equals(Object)} is NOT restricted on the uuid of {@link
   * Transformer3WTypeInput}.
   *
   * @return a set of object and uuid unique {@link Transformer3WTypeInput} entities
   */
  Set<Transformer3WTypeInput> getTransformer3WTypes();

  /**
   * Returns a set of {@link BmTypeInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link BmTypeInput} which has to be checked manually, as {@link BmTypeInput#equals(Object)} is
   * NOT restricted on the uuid of {@link BmTypeInput}.
   *
   * @return a set of object and uuid unique {@link BmTypeInput} entities
   */
  Set<BmTypeInput> getBmTypes();

  /**
   * Returns a set of {@link ChpTypeInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link ChpTypeInput} which has to be checked manually, as {@link ChpTypeInput#equals(Object)}
   * is NOT restricted on the uuid of {@link ChpTypeInput}.
   *
   * @return a set of object and uuid unique {@link ChpTypeInput} entities
   */
  Set<ChpTypeInput> getChpTypes();

  /**
   * Returns a set of {@link HpTypeInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link HpTypeInput} which has to be checked manually, as {@link HpTypeInput#equals(Object)} is
   * NOT restricted on the uuid of {@link HpTypeInput}.
   *
   * @return a set of object and uuid unique {@link HpTypeInput} entities
   */
  Set<HpTypeInput> getHpTypes();

  /**
   * Returns a set of {@link StorageTypeInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link StorageTypeInput} which has to be checked manually, as {@link
   * StorageTypeInput#equals(Object)} is NOT restricted on the uuid of {@link StorageTypeInput}.
   *
   * @return a set of object and uuid unique {@link StorageTypeInput} entities
   */
  Set<StorageTypeInput> getStorageTypes();

  /**
   * Returns a set of {@link WecTypeInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link WecTypeInput} which has to be checked manually, as {@link WecTypeInput#equals(Object)}
   * is NOT restricted on the uuid of {@link WecTypeInput}.
   *
   * @return a set of object and uuid unique {@link WecTypeInput} entities
   */
  Set<WecTypeInput> getWecTypes();

  /**
   * Returns a set of {@link EvTypeInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link EvTypeInput} which has to be checked manually, as {@link EvTypeInput#equals(Object)} is
   * NOT restricted on the uuid of {@link EvTypeInput}.
   *
   * @return a set of object and uuid unique {@link EvTypeInput} entities
   */
  Set<EvTypeInput> getEvTypes();
}
