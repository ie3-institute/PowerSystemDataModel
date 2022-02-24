/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.factory.timeseries.TimeSeriesTypeFactory;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.source.TimeSeriesConstants;
import edu.ie3.datamodel.io.source.TimeSeriesTypeSource;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SqlTimeSeriesTypeSource extends SqlDataSource<TimeSeriesTypeSource.TypeEntry>
    implements TimeSeriesTypeSource {

  private static final TimeSeriesTypeFactory mappingFactory = new TimeSeriesTypeFactory();

  private final String queryFull;
  private final DatabaseNamingStrategy namingStrategy;

  protected SqlTimeSeriesTypeSource(
      SqlConnector connector, String schemaName, DatabaseNamingStrategy namingStrategy) {
    super(connector);
    this.namingStrategy = namingStrategy;

    this.queryFull = createQueryFull(schemaName);
  }

  private String createQueryFull(String schemaName) {
    Map<String, ColumnScheme> acceptedTableNames =
        TimeSeriesConstants.ACCEPTED_COLUMN_SCHEMES.stream()
            .collect(
                Collectors.toMap(
                    namingStrategy::getTimeSeriesEntityName, columnScheme -> columnScheme));

    Iterable<String> selectQueries =
        getDbTables(schemaName, namingStrategy.getTimeSeriesPrefix() + "%").stream()
            .map(
                tableName ->
                    Optional.ofNullable(acceptedTableNames.get(tableName))
                        .map(
                            columnScheme ->
                                "SELECT DISTINCT time_series, '"
                                    + columnScheme.getScheme()
                                    + "' as column_scheme FROM "
                                    + schemaName
                                    + "."
                                    + tableName))
            .flatMap(Optional::stream)
            .collect(Collectors.toList());

    return String.join("\nUNION\n", selectQueries) + ";";
  }

  @Override
  public Map<UUID, IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation() {
    return executeQuery(queryFull, ps -> {}).stream()
        .collect(
            Collectors.toMap(
                TypeEntry::getTimeSeries,
                entry ->
                    new IndividualTimeSeriesMetaInformation(
                        entry.getTimeSeries(), entry.getColumnScheme())));
  }

  @Override
  protected Optional<TypeEntry> createEntity(Map<String, String> fieldToValues) {
    SimpleEntityData entityData = new SimpleEntityData(fieldToValues, TypeEntry.class);
    return mappingFactory.get(entityData);
  }
}
