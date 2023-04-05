/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.factory.timeseries.TimeSeriesMetaInformationFactory;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.source.TimeSeriesMetaInformationSource;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/** SQL implementation for retrieving {@link TimeSeriesMetaInformationSource} from the SQL scheme */
public class SqlTimeSeriesMetaInformationSource extends TimeSeriesMetaInformationSource {

  private static final TimeSeriesMetaInformationFactory mappingFactory =
      new TimeSeriesMetaInformationFactory();

  private final DatabaseNamingStrategy namingStrategy;
  private final Map<UUID, IndividualTimeSeriesMetaInformation> mapping;

  protected SqlDataSource dataSource;

  public SqlTimeSeriesMetaInformationSource(
      SqlConnector connector, String schemaName, DatabaseNamingStrategy databaseNamingStrategy) {
    this.dataSource = new SqlDataSource(connector, schemaName, databaseNamingStrategy);
    this.namingStrategy = databaseNamingStrategy;

    String queryComplete = createQueryComplete(schemaName);

    this.mapping =
        dataSource
            .executeQuery(queryComplete)
            .map(this::createEntity)
            .flatMap(Optional::stream)
            .collect(
                Collectors.toMap(
                    IndividualTimeSeriesMetaInformation::getUuid, Function.identity()));
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
        dataSource.getDbTables(schemaName, namingStrategy.getTimeSeriesPrefix() + "%").stream()
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

  public Map<UUID, IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation() {
    return this.mapping;
  }

  /**
   * Get an option on the given time series meta information
   *
   * @param timeSeriesUuid Unique identifier of the time series in question
   * @return An Option on the meta information
   */
  public Optional<IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation(
      UUID timeSeriesUuid) {
    return Optional.ofNullable(this.mapping.get(timeSeriesUuid));
  }

  protected Optional<IndividualTimeSeriesMetaInformation> createEntity(
      Map<String, String> fieldToValues) {
    SimpleEntityData entityData =
        new SimpleEntityData(fieldToValues, IndividualTimeSeriesMetaInformation.class);
    return mappingFactory.get(entityData);
  }
}
