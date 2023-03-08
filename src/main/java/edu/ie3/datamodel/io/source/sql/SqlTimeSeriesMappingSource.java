/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import static edu.ie3.datamodel.io.source.sql.SqlDataSource.createBaseQueryString;

import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SqlTimeSeriesMappingSource extends TimeSeriesMappingSource {
  private final EntityPersistenceNamingStrategy entityPersistenceNamingStrategy;
  private final String queryFull;

  private final SqlDataSource dataSource;

  public SqlTimeSeriesMappingSource(
      SqlConnector connector,
      String schemaName,
      EntityPersistenceNamingStrategy entityPersistenceNamingStrategy) {
    this.dataSource =
        new SqlDataSource(
            connector, schemaName, new DatabaseNamingStrategy(entityPersistenceNamingStrategy));
    this.entityPersistenceNamingStrategy = entityPersistenceNamingStrategy;

    final String tableName =
        entityPersistenceNamingStrategy.getEntityName(MappingEntry.class).orElseThrow();
    this.queryFull = createBaseQueryString(schemaName, tableName);
  }

  public Map<UUID, UUID> getMapping() {
    return dataSource.queryToListOfMaps(queryFull, ps -> {}).stream()
        .map(this::createEntity)
        .flatMap(Optional::stream)
        .collect(Collectors.toMap(MappingEntry::getParticipant, MappingEntry::getTimeSeries));
  }

  protected Optional<MappingEntry> createEntity(Map<String, String> fieldToValues) {
    SimpleEntityData entityData = new SimpleEntityData(fieldToValues, MappingEntry.class);
    return mappingFactory.get(entityData);
  }
}
