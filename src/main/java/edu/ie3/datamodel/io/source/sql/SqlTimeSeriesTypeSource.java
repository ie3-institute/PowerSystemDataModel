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
import edu.ie3.datamodel.io.source.TimeSeriesTypeSource;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/** SQL implementation for retrieving {@link TimeSeriesTypeSource} from the SQL scheme */
public class SqlTimeSeriesTypeSource extends SqlDataSource<TimeSeriesTypeSource.TypeEntry>
    implements TimeSeriesTypeSource {

  private static final TimeSeriesTypeFactory mappingFactory = new TimeSeriesTypeFactory();

  /** Query to retrieve information on all time series that are available */
  private final String queryComplete;

  private final DatabaseNamingStrategy namingStrategy;

  public SqlTimeSeriesTypeSource(
      SqlConnector connector, String schemaName, DatabaseNamingStrategy namingStrategy) {
    super(connector);
    this.namingStrategy = namingStrategy;

    this.queryComplete = createQueryComplete(schemaName);
  }

  /**
   * Creates a query that retrieves all time series uuid from existing time series tables.
   *
   * @param schemaName schema that the time series reside in
   * @return query String
   */
  private String createQueryComplete(String schemaName) {
    Map<String, ColumnScheme> acceptedTableNames =
        TimeSeriesUtils.getAcceptedColumnSchemes().stream()
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
            .toList();

    return String.join("\nUNION\n", selectQueries) + ";";
  }

  @Override
  public Map<UUID, IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation() {
    return executeQuery(queryComplete, ps -> {}).stream()
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
