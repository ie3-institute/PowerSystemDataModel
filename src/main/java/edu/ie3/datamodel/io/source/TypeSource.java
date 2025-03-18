/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.FailedValidationException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.factory.input.OperatorInputFactory;
import edu.ie3.datamodel.io.factory.typeinput.LineTypeInputFactory;
import edu.ie3.datamodel.io.factory.typeinput.SystemParticipantTypeInputFactory;
import edu.ie3.datamodel.io.factory.typeinput.Transformer2WTypeInputFactory;
import edu.ie3.datamodel.io.factory.typeinput.Transformer3WTypeInputFactory;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.utils.Try;
import java.util.ArrayList;
import java.util.List;
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

  private final DataSource dataSource;

  public TypeSource(DataSource dataSource) {
    this.dataSource = dataSource;

    this.operatorInputFactory = new OperatorInputFactory();
    this.transformer2WTypeInputFactory = new Transformer2WTypeInputFactory();
    this.lineTypeInputFactory = new LineTypeInputFactory();
    this.transformer3WTypeInputFactory = new Transformer3WTypeInputFactory();
    this.systemParticipantTypeInputFactory = new SystemParticipantTypeInputFactory();
  }

  @Override
  public void validate() throws ValidationException {
    List<Try<Void, ValidationException>> participantResults =
        new ArrayList<>(
            Stream.of(
                    EvTypeInput.class,
                    HpTypeInput.class,
                    BmTypeInput.class,
                    WecTypeInput.class,
                    ChpTypeInput.class,
                    StorageTypeInput.class)
                .map(c -> validate(c, dataSource, systemParticipantTypeInputFactory))
                .toList());

    participantResults.addAll(
        List.of(
            validate(OperatorInput.class, dataSource, operatorInputFactory),
            validate(LineTypeInput.class, dataSource, lineTypeInputFactory),
            validate(Transformer2WTypeInput.class, dataSource, transformer2WTypeInputFactory),
            validate(Transformer3WTypeInput.class, dataSource, transformer3WTypeInputFactory)));

    Try.scanCollection(participantResults, Void.class)
        .transformF(FailedValidationException::new)
        .getOrThrow();
  }

  /**
   * Returns a set of {@link Transformer2WTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link Transformer2WTypeInput} which has to be checked
   * manually, as {@link Transformer2WTypeInput#equals(Object)} is NOT restricted on the uuid of
   * {@link Transformer2WTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link Transformer2WTypeInput} entities
   */
  public Map<UUID, Transformer2WTypeInput> getTransformer2WTypes() throws SourceException {
    return getEntities(Transformer2WTypeInput.class, dataSource, transformer2WTypeInputFactory);
  }

  /**
   * Returns a set of {@link OperatorInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link OperatorInput} which has to be checked manually, as
   * {@link OperatorInput#equals(Object)} is NOT restricted on the uuid of {@link OperatorInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   */
  public Map<UUID, OperatorInput> getOperators() throws SourceException {
    return getEntities(OperatorInput.class, dataSource, operatorInputFactory);
  }

  /**
   * Returns a set of {@link LineTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link LineTypeInput} which has to be checked manually, as
   * {@link LineTypeInput#equals(Object)} is NOT restricted on the uuid of {@link LineTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link LineTypeInput} entities
   */
  public Map<UUID, LineTypeInput> getLineTypes() throws SourceException {
    return getEntities(LineTypeInput.class, dataSource, lineTypeInputFactory);
  }

  /**
   * Returns a set of {@link Transformer3WTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link Transformer3WTypeInput} which has to be checked
   * manually, as {@link Transformer3WTypeInput#equals(Object)} is NOT restricted on the uuid of
   * {@link Transformer3WTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link Transformer3WTypeInput} entities
   */
  public Map<UUID, Transformer3WTypeInput> getTransformer3WTypes() throws SourceException {
    return getEntities(Transformer3WTypeInput.class, dataSource, transformer3WTypeInputFactory);
  }

  /**
   * Returns a set of {@link BmTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link BmTypeInput} which has to be checked manually, as
   * {@link BmTypeInput#equals(Object)} is NOT restricted on the uuid of {@link BmTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link BmTypeInput} entities
   */
  public Map<UUID, BmTypeInput> getBmTypes() throws SourceException {
    return getEntities(BmTypeInput.class, dataSource, systemParticipantTypeInputFactory);
  }

  /**
   * Returns a set of {@link ChpTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link ChpTypeInput} which has to be checked manually, as
   * {@link ChpTypeInput#equals(Object)} is NOT restricted on the uuid of {@link ChpTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link ChpTypeInput} entities
   */
  public Map<UUID, ChpTypeInput> getChpTypes() throws SourceException {
    return getEntities(ChpTypeInput.class, dataSource, systemParticipantTypeInputFactory);
  }

  /**
   * Returns a set of {@link HpTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link HpTypeInput} which has to be checked manually, as
   * {@link HpTypeInput#equals(Object)} is NOT restricted on the uuid of {@link HpTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link HpTypeInput} entities
   */
  public Map<UUID, HpTypeInput> getHpTypes() throws SourceException {
    return getEntities(HpTypeInput.class, dataSource, systemParticipantTypeInputFactory);
  }

  /**
   * Returns a set of {@link StorageTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link StorageTypeInput} which has to be checked manually, as
   * {@link StorageTypeInput#equals(Object)} is NOT restricted on the uuid of {@link
   * StorageTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link StorageTypeInput} entities
   */
  public Map<UUID, StorageTypeInput> getStorageTypes() throws SourceException {
    return getEntities(StorageTypeInput.class, dataSource, systemParticipantTypeInputFactory);
  }

  /**
   * Returns a set of {@link WecTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link WecTypeInput} which has to be checked manually, as
   * {@link WecTypeInput#equals(Object)} is NOT restricted on the uuid of {@link WecTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link WecTypeInput} entities
   */
  public Map<UUID, WecTypeInput> getWecTypes() throws SourceException {
    return getEntities(WecTypeInput.class, dataSource, systemParticipantTypeInputFactory);
  }

  /**
   * Returns a set of {@link EvTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link EvTypeInput} which has to be checked manually, as
   * {@link EvTypeInput#equals(Object)} is NOT restricted on the uuid of {@link EvTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link EvTypeInput} entities
   */
  public Map<UUID, EvTypeInput> getEvTypes() throws SourceException {
    return getEntities(EvTypeInput.class, dataSource, systemParticipantTypeInputFactory);
  }
}
