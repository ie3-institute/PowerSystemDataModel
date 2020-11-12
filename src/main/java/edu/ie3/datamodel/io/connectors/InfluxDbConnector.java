/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

/**
 * Implements a DataConnector for InfluxDB. InfluxDB is a time series database and as such, it can
 * only handle time based data. <br>
 * Entities will be persisted as <i>measurement points</i>, which consist of a time, one or more
 * tags, one or more values and a measurement name. In contrast to values, tags should only contain
 * metadata. A measurement name is the equivalent of the name of a table in relational data models.
 */
public class InfluxDbConnector implements DataConnector {
  /** Merges two sets of (fieldName to fieldValue) maps */
  private static final BinaryOperator<Set<Map<String, String>>> mergeSets =
      (maps, maps2) -> {
        maps.addAll(maps2);
        return maps;
      };

  // todo JH remove
  //    private static final String INFLUXDB_URL              = "http://localhost:8086/";
  //    private static final String INFLUXDB_DATABASE_NAME    = "ie3_in";
  //    private static final String INFLUXDB_DEFAULT_SCENARIO = "no_scenario";

  private final String scenarioName;
  private final InfluxDB session;

  /**
   * Initializes a new InfluxDbConnector with the given url, databaseName and scenario name.
   *
   * @param url the connection url for the influxDB database
   * @param scenarioName the name of the simulation scenario which will be used in influxDB
   *     measurement names
   * @param databaseName the name of the database the session should be set to
   * @param createDb true if the connector should create the database if it doesn't exist yet, false
   *     otherwise
   * @param logLevel log level of the {@link InfluxDB.LogLevel} logger
   * @param batchOptions write options to write batch operations
   */
  public InfluxDbConnector(
      String url,
      String databaseName,
      String scenarioName,
      boolean createDb,
      InfluxDB.LogLevel logLevel,
      BatchOptions batchOptions) {
    this.scenarioName = scenarioName;

    // init session
    this.session = InfluxDBFactory.connect(url);
    session.setDatabase(databaseName);

    // create database on init if it doesn't exist yet
    if (createDb) createDb(databaseName);
    session.setLogLevel(logLevel);
    session.enableBatch(batchOptions);
  }

  /**
   * Initializes a new InfluxDbConnector with the given influxDb session, the databaseName and
   * scenario name.
   *
   * @param session the influxdb session that should be managed by this connector
   * @param scenarioName the name of the scenario
   * @param databaseName the name of the database the session should be set to
   * @param createDb true if the connector should create the database if it doesn't exist yet, false
   *     otherwise
   */
  public InfluxDbConnector(
      InfluxDB session, String scenarioName, String databaseName, boolean createDb) {
    this.scenarioName = scenarioName;
    this.session = session;

    session.setDatabase(databaseName);
    if (createDb) createDb(databaseName);
  }

  /**
   * Initializes a new InfluxDbConnector with the given url, databaseName and scenario name.
   *
   * @param url the connection url for the influxDB database
   * @param databaseName the name of the database to which the connection should be established
   * @param scenarioName the name of the simulation scenario which will be used in influxDB
   *     measurement names
   */
  public InfluxDbConnector(String url, String databaseName, String scenarioName) {
    this(url, databaseName, scenarioName, true, InfluxDB.LogLevel.NONE, BatchOptions.DEFAULTS);
  }

  /**
   * Create the database of this connector if it doesn't exist yet
   *
   * @param databaseName the name of the database that should be created
   * @return the result of the create database query
   */
  public QueryResult createDb(String databaseName) {
    return session.query(new Query("CREATE DATABASE " + databaseName, databaseName));
  }

  /**
   * Checks if the given connection parameters are valid, so that a connection can be established
   *
   * @return true, if the database returned the ping
   */
  public Boolean isConnectionValid() {
    Pong response = session.ping();
    session.close();
    return !response.getVersion().equalsIgnoreCase("unknown");
  }

  @Override
  public void shutdown() {
    session.close();
  }

  /**
   * Return the session of this connector
   *
   * @return influx db session
   */
  public InfluxDB getSession() {
    return session;
  }

  public String getScenarioName() {
    return scenarioName;
  }

  /**
   * Parses the result of an influxQL query for all measurements (e.g. weather)
   *
   * @param queryResult Result of an influxDB query
   * @return Map of (measurement name to Set of maps of (field name : field value)) for each result
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
   * @return Map of (measurement name to Set of maps of (field name : field value) for each result
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
   * @return Map of (measurement name to Set of maps of (field name : field value) for each result
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
   * @return Set of maps of (field name to field value) for each result entity
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
   * @return Map of (field name to field value) for one result entity
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
