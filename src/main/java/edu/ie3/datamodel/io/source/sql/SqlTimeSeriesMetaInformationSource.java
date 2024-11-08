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
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.source.TimeSeriesMetaInformationSource;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/** SQL implementation for retrieving {@link TimeSeriesMetaInformationSource} from the SQL scheme */
public class SqlTimeSeriesMetaInformationSource implements TimeSeriesMetaInformationSource {

  private static final TimeSeriesMetaInformationFactory mappingFactory =
      new TimeSeriesMetaInformationFactory();

  private final DatabaseNamingStrategy namingStrategy;
  private final Map<UUID, IndividualTimeSeriesMetaInformation> mapping;
  private final Map<String, LoadProfileTimeSeriesMetaInformation>
      loadProfileTimeSeriesMetaInformation;

  private final SqlDataSource dataSource;

  public SqlTimeSeriesMetaInformationSource(
      SqlConnector connector, String schemaName, DatabaseNamingStrategy databaseNamingStrategy) {
    this.dataSource = new SqlDataSource(connector, schemaName, databaseNamingStrategy);
    this.namingStrategy = databaseNamingStrategy;

    String queryComplete = createQueryComplete(schemaName);
    String loadMetaInformationQuery = createLoadProfileQueryComplete(schemaName);

    this.mapping =
        dataSource
            .executeQuery(queryComplete)
            .map(this::createEntity)
            .flatMap(Optional::stream)
            .collect(
                Collectors.toMap(
                    IndividualTimeSeriesMetaInformation::getUuid, Function.identity()));

    this.loadProfileTimeSeriesMetaInformation =
        dataSource
            .executeQuery(loadMetaInformationQuery)
            .map(this::createLoadProfileEntity)
            .flatMap(Optional::stream)
            .collect(
                Collectors.toMap(
                    LoadProfileTimeSeriesMetaInformation::getProfile, Function.identity()));
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

  /**
   * Creates a query that retrieves all time series uuid from existing time series tables.
   *
   * @param schemaName schema that the time series reside in
   * @return query String
   */
  private String createLoadProfileQueryComplete(String schemaName) {
    return "SELECT DISTINCT load_profile FROM " + schemaName + ".load_profiles;";
  }

  @Override
  public Map<UUID, IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation() {
    return this.mapping;
  }

  @Override
  public Optional<IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation(
      UUID timeSeriesUuid) {
    return Optional.ofNullable(this.mapping.get(timeSeriesUuid));
  }

  @Override
  public Map<String, LoadProfileTimeSeriesMetaInformation> getLoadProfileMetaInformation() {
    return loadProfileTimeSeriesMetaInformation;
  }

  @Override
  public Optional<LoadProfileTimeSeriesMetaInformation> getLoadProfileMetaInformation(
      LoadProfile loadProfile) {
    return Optional.ofNullable(loadProfileTimeSeriesMetaInformation.get(loadProfile.getKey()));
  }

  private Optional<IndividualTimeSeriesMetaInformation> createEntity(
      Map<String, String> fieldToValues) {
    EntityData entityData =
        new EntityData(fieldToValues, IndividualTimeSeriesMetaInformation.class);
    return mappingFactory
        .get(entityData)
        .map(meta -> (IndividualTimeSeriesMetaInformation) meta)
        .getData();
  }

  private Optional<LoadProfileTimeSeriesMetaInformation> createLoadProfileEntity(
      Map<String, String> fieldToValues) {
    EntityData entityData =
        new EntityData(fieldToValues, LoadProfileTimeSeriesMetaInformation.class);
    return mappingFactory
        .get(entityData)
        .map(meta -> (LoadProfileTimeSeriesMetaInformation) meta)
        .getData();
  }
}
