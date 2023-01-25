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
public class SqlTimeSeriesMetaInformationSource
    extends TimeSeriesMetaInformationSource {

  private final Map<UUID, edu.ie3.datamodel.io.csv.CsvIndividualTimeSeriesMetaInformation> timeSeriesMetaInformation;
  public SqlTimeSeriesMetaInformationSource(
          SqlConnector connector,
          String schemaName,
          DatabaseNamingStrategy databaseNamingStrategy
  ) {
    super(new SqlDataSource(connector, schemaName, databaseNamingStrategy));
    this.timeSeriesMetaInformation = null;

    /*
    this.mapping = queryMapping(queryComplete, ps -> {})
            .stream()
            .map(this::createEntity)
            .flatMap(Optional::stream)
            .toList()
            .stream()
            .collect(
                    Collectors.toMap(
                            IndividualTimeSeriesMetaInformation::getUuid, Function.identity()));

     */
  }


  /*
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

   */


  public Map<UUID, IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation() {
    return null;
  }


  public Optional<IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation(
      UUID timeSeriesUuid) {
    return null;
  }

  protected Optional<IndividualTimeSeriesMetaInformation> createEntity(
      Map<String, String> fieldToValues) {
    return null;
  }
}
