/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.timeseries.TimeSeriesMetaInformationFactory;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation;
import edu.ie3.datamodel.io.source.TimeSeriesMetaInformationSource;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/** SQL implementation for retrieving {@link TimeSeriesMetaInformationSource} from the SQL scheme */
public class SqlTimeSeriesMetaInformationSource extends TimeSeriesMetaInformationSource {

  private static final TimeSeriesMetaInformationFactory metaInformationFactory =
      new TimeSeriesMetaInformationFactory();

  private final Map<UUID, IndividualTimeSeriesMetaInformation> timeSeriesMetaInformation;

  private final DatabaseNamingStrategy namingStrategy;
  private final SqlDataSource dataSource;

  public SqlTimeSeriesMetaInformationSource(
      SqlConnector connector, String schemaName, DatabaseNamingStrategy databaseNamingStrategy) {
    this.dataSource = new SqlDataSource(connector, schemaName, databaseNamingStrategy);
    this.namingStrategy = databaseNamingStrategy;

    String queryComplete = createQueryComplete(schemaName);
    String loadMetaInformationQuery = createLoadProfileQueryComplete(schemaName);

    this.timeSeriesMetaInformation =
        dataSource
            .executeQuery(queryComplete)
            .map(this::createEntity)
            .flatMap(Optional::stream)
            .collect(
                Collectors.toMap(
                    IndividualTimeSeriesMetaInformation::getUuid, Function.identity()));

    this.loadProfileMetaInformation =
        dataSource
            .executeQuery(loadMetaInformationQuery)
            .map(this::createLoadProfileEntity)
            .flatMap(Optional::stream)
            .collect(Collectors.toMap(LoadProfileMetaInformation::getProfile, Function.identity()));
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

    List<String> selectQueries =
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

    return selectQueries.isEmpty() ? ";" : String.join("\nUNION\n", selectQueries) + ";";
  }

  /**
   * Creates a query that retrieves all time series uuid from existing time series tables.
   *
   * @param schemaName schema that the time series reside in
   * @return query String
   */
  private String createLoadProfileQueryComplete(String schemaName) {
    String tableName = namingStrategy.getLoadProfileTableName();
    boolean isNotPresent = dataSource.getDbTables(schemaName, tableName).isEmpty();

    return isNotPresent
        ? ";"
        : "SELECT DISTINCT load_profile FROM " + schemaName + "." + tableName + ";";
  }

  @Override
  public Map<UUID, IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation() {
    return timeSeriesMetaInformation;
  }

  @Override
  public Optional<IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation(
      UUID timeSeriesUuid) {
    return Optional.ofNullable(timeSeriesMetaInformation.get(timeSeriesUuid));
  }

  private Optional<IndividualTimeSeriesMetaInformation> createEntity(
      Map<String, String> fieldToValues) {
    EntityData entityData =
        new EntityData(fieldToValues, IndividualTimeSeriesMetaInformation.class);
    return metaInformationFactory
        .get(entityData)
        .map(IndividualTimeSeriesMetaInformation.class::cast)
        .getData();
  }

  private Optional<LoadProfileMetaInformation> createLoadProfileEntity(
      Map<String, String> fieldToValues) {
    EntityData entityData = new EntityData(fieldToValues, LoadProfileMetaInformation.class);
    return metaInformationFactory
        .get(entityData)
        .map(LoadProfileMetaInformation.class::cast)
        .getData();
  }
}
