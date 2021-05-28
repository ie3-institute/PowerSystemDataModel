/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.factory.input.OperatorInputFactory;
import edu.ie3.datamodel.io.factory.typeinput.LineTypeInputFactory;
import edu.ie3.datamodel.io.factory.typeinput.SystemParticipantTypeInputFactory;
import edu.ie3.datamodel.io.factory.typeinput.Transformer2WTypeInputFactory;
import edu.ie3.datamodel.io.factory.typeinput.Transformer3WTypeInputFactory;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.system.type.*;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Source that provides the capability to build entities of type {@link SystemParticipantTypeInput}
 * and {@link OperatorInput} from .csv files
 *
 * @version 0.1
 * @since 05.04.20
 */
public class CsvTypeSource extends CsvDataSource implements TypeSource {

  // factories
  private final OperatorInputFactory operatorInputFactory;
  private final Transformer2WTypeInputFactory transformer2WTypeInputFactory;
  private final LineTypeInputFactory lineTypeInputFactory;
  private final Transformer3WTypeInputFactory transformer3WTypeInputFactory;
  private final SystemParticipantTypeInputFactory systemParticipantTypeInputFactory;

  public CsvTypeSource(
      String csvSep, String typeFolderPath, FileNamingStrategy fileNamingStrategy) {
    super(csvSep, typeFolderPath, fileNamingStrategy);

    // init factories
    operatorInputFactory = new OperatorInputFactory();
    transformer2WTypeInputFactory = new Transformer2WTypeInputFactory();
    lineTypeInputFactory = new LineTypeInputFactory();
    transformer3WTypeInputFactory = new Transformer3WTypeInputFactory();
    systemParticipantTypeInputFactory = new SystemParticipantTypeInputFactory();
  }
  /** {@inheritDoc} */
  @Override
  public Set<Transformer2WTypeInput> getTransformer2WTypes() {
    return buildSimpleEntities(Transformer2WTypeInput.class, transformer2WTypeInputFactory);
  }
  /** {@inheritDoc} */
  @Override
  public Set<OperatorInput> getOperators() {
    return buildSimpleEntities(OperatorInput.class, operatorInputFactory);
  }
  /** {@inheritDoc} */
  @Override
  public Set<LineTypeInput> getLineTypes() {
    return buildSimpleEntities(LineTypeInput.class, lineTypeInputFactory);
  }
  /** {@inheritDoc} */
  @Override
  public Set<Transformer3WTypeInput> getTransformer3WTypes() {
    return buildSimpleEntities(Transformer3WTypeInput.class, transformer3WTypeInputFactory);
  }
  /** {@inheritDoc} */
  @Override
  public Set<BmTypeInput> getBmTypes() {
    return buildSimpleEntities(BmTypeInput.class, systemParticipantTypeInputFactory);
  }
  /** {@inheritDoc} */
  @Override
  public Set<ChpTypeInput> getChpTypes() {
    return buildSimpleEntities(ChpTypeInput.class, systemParticipantTypeInputFactory);
  }
  /** {@inheritDoc} */
  @Override
  public Set<HpTypeInput> getHpTypes() {
    return buildSimpleEntities(HpTypeInput.class, systemParticipantTypeInputFactory);
  }
  /** {@inheritDoc} */
  @Override
  public Set<StorageTypeInput> getStorageTypes() {
    return buildSimpleEntities(StorageTypeInput.class, systemParticipantTypeInputFactory);
  }
  /** {@inheritDoc} */
  @Override
  public Set<WecTypeInput> getWecTypes() {
    return buildSimpleEntities(WecTypeInput.class, systemParticipantTypeInputFactory);
  }
  /** {@inheritDoc} */
  @Override
  public Set<EvTypeInput> getEvTypes() {
    return buildSimpleEntities(EvTypeInput.class, systemParticipantTypeInputFactory);
  }

  /**
   * Tries to build a set of {@link InputEntity}s of the provided entity class based on the provided
   * factory. To do so, first entity data of type {@link SimpleEntityData} is constructed based on
   * the input .csv file that can be derived from the entity class. This data is than passed to the
   * factory and used to build the corresponding entities.
   *
   * <p>Be careful, that always a factory that is able to produce an entity of type <T> is passed
   * into as argument. Otherwise, a casting exception will be thrown.
   *
   * @param entityClass the concrete class of the {@link InputEntity} that should be built
   * @param factory the entity factory that should be used
   * @param <T> the type of the resulting entity
   * @return a set containing all entities that could have been built or an empty set if no entity
   *     could been built
   */
  private <T extends InputEntity> Set<T> buildSimpleEntities(
      Class<T> entityClass, EntityFactory<? extends InputEntity, SimpleEntityData> factory) {
    return buildStreamWithFieldsToAttributesMap(entityClass, connector)
        .map(
            fieldsToAttributes -> {
              SimpleEntityData data = new SimpleEntityData(fieldsToAttributes, entityClass);
              return (Optional<T>) factory.get(data);
            })
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toSet());
  }
}
