/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

public class InfluxDbConnector implements DataConnector {
  /** Merges two sets of (fieldName -> fieldValue) maps */
  private static final BinaryOperator<Set<Map<String, String>>> mergeSets =
      (maps, maps2) -> {
        maps.addAll(maps2);
        return maps;
      };

  private static final String INFLUXDB_URL = "http://localhost:8086/";
  private static final String INFLUXDB_DATABASE_NAME = "ie3_in";
  private final String databaseName;
  private final String scenarioName;
  private final String url;

  public InfluxDbConnector(String url, String databaseName, String scenarioName) {
    this.url = url;
    this.databaseName = databaseName;
    this.scenarioName = scenarioName;
  }

  /**
   * Initializes a new InfluxDbConnector with the given url and databaseName and no scenario name.
   * Consider using a scenario name if you plan to persist results using this connector.
   *
   * @param url the connection url for the influxDB database
   * @param databaseName the name of the database to which the connection should be established
   */
  public InfluxDbConnector(String url, String databaseName) {
    this(url, databaseName, null);
  }

  /**
   * Initializes a new InfluxDbConnector with the default URL {@value #INFLUXDB_URL}, database name
   * {@value #INFLUXDB_DATABASE_NAME} and no scenario name
   */
  public InfluxDbConnector() {
    this(INFLUXDB_URL, INFLUXDB_DATABASE_NAME);
  }

  /**
   * Checks if the given connection parameters are valid, so that a connection can be established
   *
   * @return true, if the database returned the ping
   */
  public Boolean isConnectionValid() {
    InfluxDB session = getSession();
    if (session == null) return false;
    Pong response = session.ping();
    session.close();
    return !response.getVersion().equalsIgnoreCase("unknown");
  }

  @Override
  public void shutdown() {
    // no cleanup actions necessary
  }

  public String getDatabaseName() {
    return databaseName;
  }

  /**
   * Creates a session using the given connection parameters. If no database with the given name
   * exists, one is created.
   *
   * @return Autocloseable InfluxDB session
   */
  public InfluxDB getSession() {
    InfluxDB session;
    session = InfluxDBFactory.connect(url);
    session.setDatabase(databaseName);
    session.query(new Query("CREATE DATABASE " + databaseName, databaseName));
    session.setLogLevel(InfluxDB.LogLevel.NONE);
    session.enableBatch(100000, 5, TimeUnit.SECONDS);
    return session;
  }

  public String getScenarioName() {
    return scenarioName;
  }

  /**
   * Parses the result of an influxQL query for all measurements (e.g. weather)
   *
   * @param queryResult Result of an influxDB query
   * @return Map of (measurement name : Set of maps of (field name : field value)) for each result
   *     entity)
   */
  public static Map<String, Set<Map<String, String>>> parseQueryResult(QueryResult queryResult) {
    return parseQueryResult(queryResult, new String[0]);
  }

  /**
   * Parses the result of one or multiple influxQL queries for the given measurements (e.g.
   * weather). If no measurement names are given, all results are parsed and returned
   *
   * @param queryResult Result of an influxDB query
   * @param measurementNames Names of measurements that should be parsed. If none are given, all
   *     measurements will be parsed
   * @return Map of (measurement name : Set of maps of (field name : field value) for each result
   *     entity)
   */
  public static Map<String, Set<Map<String, String>>> parseQueryResult(
      QueryResult queryResult, String... measurementNames) {
    HashMap<String, Set<Map<String, String>>> measurementToFields = new HashMap<>();
    queryResult.getResults().stream()
        .map(result -> parseResult(result, measurementNames))
        .forEach(
            measurementMap -> {
              for (Map.Entry<String, Set<Map<String, String>>> measurementEntry :
                  measurementMap.entrySet()) {
                if (measurementToFields.containsKey(measurementEntry.getKey())) {
                  measurementToFields
                      .get(measurementEntry.getKey())
                      .addAll(measurementEntry.getValue());
                } else {
                  measurementToFields.put(measurementEntry.getKey(), measurementEntry.getValue());
                }
              }
            });
    return measurementToFields;
  }

  /**
   * Parses the result of one influxQL query for the given measurements (e.g. weather). If no
   * measurement names are given, all results are parsed and returned
   *
   * @param result Specific result of an influxDB query
   * @param measurementNames Names of measurements that should be parsed. If none are given, all
   *     measurements will be parsed
   * @return Map of (measurement name : Set of maps of (field name : field value) for each result
   *     entity)
   */
  public static Map<String, Set<Map<String, String>>> parseResult(
      QueryResult.Result result, String... measurementNames) {
    Stream<QueryResult.Series> seriesStream = result.getSeries().stream();
    if (measurementNames.length > 0) {
      seriesStream =
          seriesStream.filter(series -> Arrays.asList(measurementNames).contains(series.getName()));
    }
    return seriesStream.collect(
        Collectors.toMap(QueryResult.Series::getName, InfluxDbConnector::parseSeries, mergeSets));
  }

  /**
   * Parses the results for a single measurement series
   *
   * @param series A measurement series of an influxDB query result
   * @return Set of maps of (field name : field value) for each result entity
   */
  public static Set<Map<String, String>> parseSeries(QueryResult.Series series) {
    String[] columns = series.getColumns().toArray(new String[0]);
    return series.getValues().stream()
        .map(valueList -> parseValueList(valueList, columns))
        .collect(Collectors.toSet());
  }

  /**
   * Parses a list of values and maps them to field names using the given column name and order
   *
   * @param valueList List of values, sorted by column in columns
   * @param columns Array of column names
   * @return Map of (field name : field value) for one result entity
   */
  public static Map<String, String> parseValueList(List<?> valueList, String[] columns) {
    Map<String, String> attributeMap = new HashMap<>();
    Object[] valueArr = valueList.toArray();
    for (int i = 0; i < columns.length; i++) {
      attributeMap.put(columns[i], String.valueOf(valueArr[i]));
    }
    return attributeMap;
  }
}
