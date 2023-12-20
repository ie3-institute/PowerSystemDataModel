/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.input.OperatorInputFactory;
import edu.ie3.datamodel.io.factory.typeinput.LineTypeInputFactory;
import edu.ie3.datamodel.io.factory.typeinput.SystemParticipantTypeInputFactory;
import edu.ie3.datamodel.io.factory.typeinput.Transformer2WTypeInputFactory;
import edu.ie3.datamodel.io.factory.typeinput.Transformer3WTypeInputFactory;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.utils.Try;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Interface that provides the capability to build entities of type {@link
 * SystemParticipantTypeInput} and {@link OperatorInput} from different data sources e.g. .csv files
 * or databases
 *
 * @version 0.1
 * @since 08.04.20
 */
public class TypeSource extends EntitySource {
  // factories
  private final OperatorInputFactory operatorInputFactory;
  private final Transformer2WTypeInputFactory transformer2WTypeInputFactory;
  private final LineTypeInputFactory lineTypeInputFactory;
  private final Transformer3WTypeInputFactory transformer3WTypeInputFactory;
  private final SystemParticipantTypeInputFactory systemParticipantTypeInputFactory;

  public TypeSource(DataSource dataSource) {
    super(dataSource);

    this.operatorInputFactory = new OperatorInputFactory();
    this.transformer2WTypeInputFactory = new Transformer2WTypeInputFactory();
    this.lineTypeInputFactory = new LineTypeInputFactory();
    this.transformer3WTypeInputFactory = new Transformer3WTypeInputFactory();
    this.systemParticipantTypeInputFactory = new SystemParticipantTypeInputFactory();
  }

  /**
   * Returns a set of {@link Transformer2WTypeInput} instances. This set has to be unique in the
   * sense of object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the
   * provided {@link Transformer2WTypeInput} which has to be checked manually, as {@link
   * Transformer2WTypeInput#equals(Object)} is NOT restricted on the uuid of {@link
   * Transformer2WTypeInput}.
   *
   * @return a set of object and uuid unique {@link Transformer2WTypeInput} entities
   */
  public Map<UUID, Transformer2WTypeInput> getTransformer2WTypes() throws SourceException {
    return unpackMap(
        buildEntityData(Transformer2WTypeInput.class).map(transformer2WTypeInputFactory::get),
        Transformer2WTypeInput.class);
  }

  /**
   * Returns a set of {@link OperatorInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link OperatorInput} which has to be checked manually, as {@link OperatorInput#equals(Object)}
   * is NOT restricted on the uuid of {@link OperatorInput}.
   *
   * @return a set of object and uuid unique {@link OperatorInput} entities
   */
  public Map<UUID, OperatorInput> getOperators() throws SourceException {
    return unpackMap(
        buildEntityData(OperatorInput.class).map(operatorInputFactory::get), OperatorInput.class);
  }

  /**
   * Returns a set of {@link LineTypeInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link LineTypeInput} which has to be checked manually, as {@link LineTypeInput#equals(Object)}
   * is NOT restricted on the uuid of {@link LineTypeInput}.
   *
   * @return a set of object and uuid unique {@link LineTypeInput} entities
   */
  public Map<UUID, LineTypeInput> getLineTypes() throws SourceException {
    return unpackMap(
        buildEntityData(LineTypeInput.class).map(lineTypeInputFactory::get), LineTypeInput.class);
  }

  /**
   * Returns a set of {@link Transformer3WTypeInput} instances. This set has to be unique in the
   * sense of object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the
   * provided {@link Transformer3WTypeInput} which has to be checked manually, as {@link
   * Transformer3WTypeInput#equals(Object)} is NOT restricted on the uuid of {@link
   * Transformer3WTypeInput}.
   *
   * @return a set of object and uuid unique {@link Transformer3WTypeInput} entities
   */
  public Map<UUID, Transformer3WTypeInput> getTransformer3WTypes() throws SourceException {
    return unpackMap(
        buildEntityData(Transformer3WTypeInput.class).map(transformer3WTypeInputFactory::get),
        Transformer3WTypeInput.class);
  }

  /**
   * Returns a set of {@link BmTypeInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link BmTypeInput} which has to be checked manually, as {@link BmTypeInput#equals(Object)} is
   * NOT restricted on the uuid of {@link BmTypeInput}.
   *
   * @return a set of object and uuid unique {@link BmTypeInput} entities
   */
  public Map<UUID, BmTypeInput> getBmTypes() throws SourceException {
    return unpackMap(
        buildEntities(BmTypeInput.class, systemParticipantTypeInputFactory), BmTypeInput.class);
  }

  /**
   * Returns a set of {@link ChpTypeInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link ChpTypeInput} which has to be checked manually, as {@link ChpTypeInput#equals(Object)}
   * is NOT restricted on the uuid of {@link ChpTypeInput}.
   *
   * @return a set of object and uuid unique {@link ChpTypeInput} entities
   */
  public Map<UUID, ChpTypeInput> getChpTypes() throws SourceException {
    return unpackMap(
        buildEntities(ChpTypeInput.class, systemParticipantTypeInputFactory), ChpTypeInput.class);
  }

  /**
   * Returns a set of {@link HpTypeInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link HpTypeInput} which has to be checked manually, as {@link HpTypeInput#equals(Object)} is
   * NOT restricted on the uuid of {@link HpTypeInput}.
   *
   * @return a set of object and uuid unique {@link HpTypeInput} entities
   */
  public Map<UUID, HpTypeInput> getHpTypes() throws SourceException {
    return unpackMap(
        buildEntities(HpTypeInput.class, systemParticipantTypeInputFactory), HpTypeInput.class);
  }

  /**
   * Returns a set of {@link StorageTypeInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link StorageTypeInput} which has to be checked manually, as {@link
   * StorageTypeInput#equals(Object)} is NOT restricted on the uuid of {@link StorageTypeInput}.
   *
   * @return a set of object and uuid unique {@link StorageTypeInput} entities
   */
  public Map<UUID, StorageTypeInput> getStorageTypes() throws SourceException {
    return unpackMap(
        buildEntities(StorageTypeInput.class, systemParticipantTypeInputFactory),
        StorageTypeInput.class);
  }

  /**
   * Returns a set of {@link WecTypeInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link WecTypeInput} which has to be checked manually, as {@link WecTypeInput#equals(Object)}
   * is NOT restricted on the uuid of {@link WecTypeInput}.
   *
   * @return a set of object and uuid unique {@link WecTypeInput} entities
   */
  public Map<UUID, WecTypeInput> getWecTypes() throws SourceException {
    return unpackMap(
        buildEntities(WecTypeInput.class, systemParticipantTypeInputFactory), WecTypeInput.class);
  }

  /**
   * Returns a set of {@link EvTypeInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link EvTypeInput} which has to be checked manually, as {@link EvTypeInput#equals(Object)} is
   * NOT restricted on the uuid of {@link EvTypeInput}.
   *
   * @return a set of object and uuid unique {@link EvTypeInput} entities
   */
  public Map<UUID, EvTypeInput> getEvTypes() throws SourceException {
    return unpackMap(
        buildEntities(EvTypeInput.class, systemParticipantTypeInputFactory), EvTypeInput.class);
  }

  /**
   * Build and cast entities to the correct type, since {@link SystemParticipantTypeInputFactory}
   * outputs {@link SystemParticipantTypeInput} of general type.
   *
   * @param entityClass
   * @param factory
   * @return
   * @param <T>
   */
  @SuppressWarnings("unchecked")
  private <T extends AssetTypeInput> Stream<Try<T, FactoryException>> buildEntities(
      Class<T> entityClass, EntityFactory<? extends InputEntity, EntityData> factory) {
    return buildEntityData(entityClass).map(data -> (Try<T, FactoryException>) factory.get(data));
  }
}
