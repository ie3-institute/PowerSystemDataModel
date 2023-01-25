/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.factory.timeseries.TimeSeriesMappingFactory;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
//SqlTimeSeriesMappingSource.MappingEntry
public class SqlTimeSeriesMappingSource extends TimeSeriesMappingSource {

  public SqlTimeSeriesMappingSource(
          SqlConnector connector,
          String schemaName,
          DatabaseNamingStrategy databaseNamingStrategy
          //         EntityPersistenceNamingStrategy entityPersistenceNamingStrategy?
  ) {
    super(new SqlDataSource(connector, schemaName, databaseNamingStrategy));
  }


  /*
  private static final TimeSeriesMappingFactory mappingFactory = new TimeSeriesMappingFactory();

  private final EntityPersistenceNamingStrategy entityPersistenceNamingStrategy;
  private final String queryFull;
  //private final String schemaName;

  public SqlTimeSeriesMappingSource(
      SqlConnector connector,
      String schemaName,
      EntityPersistenceNamingStrategy entityPersistenceNamingStrategy
  ) {
    super(connector, schemaName);
    this.entityPersistenceNamingStrategy = entityPersistenceNamingStrategy;

    final String tableName =
        entityPersistenceNamingStrategy.getEntityName(MappingEntry.class).orElseThrow();
    this.queryFull = createBaseQueryString(schemaName, tableName);

    //this.schemaName = schemaName;
  }

  @Override
  public Map<UUID, UUID> getMapping() {
    return queryMapping(queryFull, ps -> {})
            .stream()
            .map(this::createEntity)
            .flatMap(Optional::stream)
            .toList()
            .stream()
            .collect(Collectors.toMap(MappingEntry::getParticipant, MappingEntry::getTimeSeries));
  }

  /**
   * @deprecated since 3.0. Use {@link
   *     SqlTimeSeriesMetaInformationSource#getTimeSeriesMetaInformation()} instead
   */

}
