/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.factory.SimpleEntityFactory;
import edu.ie3.datamodel.io.factory.result.*;
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy;
import edu.ie3.datamodel.io.source.ResultEntitySource;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
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
 * Source that provides the capability to build entities of type
 * {@link ResultEntity} container from .csv files.
 *
 * <p>This source is <b>not buffered</b> which means each call on a getter method always tries to
 * read all data is necessary to return the requested objects in a hierarchical cascading way.
 *
 * <p>If performance is an issue, it is recommended to read the data cascading starting with reading
 * nodes and then using the getters with arguments to avoid reading the same data multiple times.
 *
 * <p>The resulting sets are always unique on object <b>and</b> UUID base (with distinct UUIDs).
 *
 * @version 0.1
 * @since 22 June 2021
 */
public class CsvResultEntitySource extends CsvDataSource implements ResultEntitySource {

  private final SystemParticipantResultFactory systemParticipantResultFactory;
  private final ThermalResultFactory thermalResultFactory;
  private final SwitchResultFactory switchResultFactory;
  private final NodeResultFactory nodeResultFactory;
  private final ConnectorResultFactory connectorResultFactory;

  public CsvResultEntitySource(
      String csvSep,
      String folderPath,
      EntityPersistenceNamingStrategy entityPersistenceNamingStrategy) {
    super(csvSep, folderPath, entityPersistenceNamingStrategy);

    // init factories
    this.systemParticipantResultFactory = new SystemParticipantResultFactory();
    this.thermalResultFactory = new ThermalResultFactory();
    this.switchResultFactory = new SwitchResultFactory();
    this.nodeResultFactory = new NodeResultFactory();
    this.connectorResultFactory = new ConnectorResultFactory();
  }

  // Grid
  @Override
  public Set<NodeResult> getNodeResults() {
    return getResultEntities(NodeResult.class, nodeResultFactory);
  }

  @Override
  public Set<SwitchResult> getSwitchResults() {
    return getResultEntities(SwitchResult.class, switchResultFactory);
  }

  @Override
  public Set<LineResult> getLineResults() {
    return getResultEntities(LineResult.class, connectorResultFactory);
  }

  @Override
  public Set<Transformer2WResult> getTransformer2WResultResults() {
    return getResultEntities(Transformer2WResult.class, connectorResultFactory);
  }

  @Override
  public Set<Transformer3WResult> getTransformer3WResultResults() {
    return getResultEntities(Transformer3WResult.class, connectorResultFactory);
  }

  // System Participants
  @Override
  public Set<LoadResult> getLoadResults() {
    return getResultEntities(LoadResult.class, systemParticipantResultFactory);
  }

  @Override
  public Set<PvResult> getPvResults() {
    return getResultEntities(PvResult.class, systemParticipantResultFactory);
  }

  @Override
  public Set<FixedFeedInResult> getFixedFeedInResults() {
    return getResultEntities(FixedFeedInResult.class, systemParticipantResultFactory);
  }

  @Override
  public Set<BmResult> getBmResults() {
    return getResultEntities(BmResult.class, systemParticipantResultFactory);
  }

  @Override
  public Set<ChpResult> getChpResults() {
    return getResultEntities(ChpResult.class, systemParticipantResultFactory);
  }

  @Override
  public Set<WecResult> getWecResults() {
    return getResultEntities(WecResult.class, systemParticipantResultFactory);
  }

  @Override
  public Set<StorageResult> getStorageResults() {
    return getResultEntities(StorageResult.class, systemParticipantResultFactory);
  }

  @Override
  public Set<EvcsResult> getEvcsResults() {
    return getResultEntities(EvcsResult.class, systemParticipantResultFactory);
  }

  @Override
  public Set<EvResult> getEvResults() {
    return getResultEntities(EvResult.class, systemParticipantResultFactory);
  }

  @Override
  public Set<HpResult> getHpResults() {
    return getResultEntities(HpResult.class, systemParticipantResultFactory);
  }

  @Override
  public Set<ThermalHouseResult> getThermalHouseResults() {
    return getResultEntities(ThermalHouseResult.class, thermalResultFactory);
  }

  @Override
  public Set<CylindricalStorageResult> getCylindricalStorageResult() {
    return getResultEntities(CylindricalStorageResult.class, thermalResultFactory);
  }

  private <T extends ResultEntity> Set<T> getResultEntities(
      Class<T> entityClass, SimpleEntityFactory<? extends ResultEntity> factory) {
    return buildStreamWithFieldsToAttributesMap(entityClass, connector)
            .map(
                    fieldsToAttributes -> {
                      SimpleEntityData data = new SimpleEntityData(fieldsToAttributes, entityClass);
                      return (Optional<T>) factory.get(data);
                    })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
//    return filterEmptyOptionals(
//            simpleEntityDataStream(entityClass)
//                .map(
//                    entityData ->
//                        factory
//                            .get(entityData)
//                            .flatMap(loadResult -> cast(entityClass, loadResult))))
//        .collect(Collectors.toSet());
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
