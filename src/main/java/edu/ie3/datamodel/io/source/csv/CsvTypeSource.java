/*
 * © 2020. TU Dortmund University,
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
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.system.type.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * //ToDo: Class Description // todo hint that set does NOT check for uuid uniqueness!
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

  @Override
  public Set<Transformer2WTypeInput> getTransformer2WTypes() {
    return readSimpleEntities(Transformer2WTypeInput.class, transformer2WTypeInputFactory);
  }

  @Override
  public Set<OperatorInput> getOperators() {
    return readSimpleEntities(OperatorInput.class, operatorInputFactory);
  }

  @Override
  public Set<LineTypeInput> getLineTypes() {
    return readSimpleEntities(LineTypeInput.class, lineTypeInputFactory);
  }

  @Override
  public Set<Transformer3WTypeInput> getTransformer3WTypes() {
    return readSimpleEntities(Transformer3WTypeInput.class, transformer3WTypeInputFactory);
  }

  @Override
  public Set<BmTypeInput> getBmTypes() {
    return readSimpleEntities(BmTypeInput.class, systemParticipantTypeInputFactory);
  }

  @Override
  public Set<ChpTypeInput> getChpTypes() {
    return readSimpleEntities(ChpTypeInput.class, systemParticipantTypeInputFactory);
  }

  @Override
  public Set<HpTypeInput> getHpTypes() {
    return readSimpleEntities(HpTypeInput.class, systemParticipantTypeInputFactory);
  }

  @Override
  public Set<StorageTypeInput> getStorageTypes() {
    return readSimpleEntities(StorageTypeInput.class, systemParticipantTypeInputFactory);
  }

  @Override
  public Set<WecTypeInput> getWecTypes() {
    return readSimpleEntities(WecTypeInput.class, systemParticipantTypeInputFactory);
  }

  @Override
  public Set<EvTypeInput> getEvTypes() {
    return readSimpleEntities(EvTypeInput.class, systemParticipantTypeInputFactory);
  }

  @SuppressWarnings("unchecked cast")
  private <T extends InputEntity> Set<T> readSimpleEntities(
      Class<? extends UniqueEntity> entityClass,
      EntityFactory<? extends UniqueEntity, SimpleEntityData> factory) {
    return (Set<T>)
        buildStreamWithFieldsToAttributesMap(entityClass, connector)
            .map(
                fieldsToAttributes -> {
                  SimpleEntityData data = new SimpleEntityData(fieldsToAttributes, entityClass);
                  return factory.getEntity(data);
                })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
  }
}
