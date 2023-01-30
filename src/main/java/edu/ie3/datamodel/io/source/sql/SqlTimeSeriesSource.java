/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.factory.timeseries.SimpleTimeBasedValueData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.source.TimeSeriesSource;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.utils.TimeSeriesUtils;

import java.util.*;

public class SqlTimeSeriesSource<V extends Value> extends TimeSeriesSource<V> {

  // General fields
  private static final String WHERE = " WHERE ";
  private static final String TIME_SERIES = "time_series";

  private String baseQuery;

  public SqlTimeSeriesSource(
          SqlDataSource sqlDataSource,
          UUID timeSeriesUuid,
          String specialPlace,
          Class<V> valueClass,
          TimeBasedSimpleValueFactory<V> factory
  ) {
    super(sqlDataSource, timeSeriesUuid, specialPlace, valueClass, factory);

    final ColumnScheme columnScheme = ColumnScheme.parse(valueClass).orElseThrow();
    final String tableName = sqlDataSource.getDatabaseNamingStrategy().getTimeSeriesEntityName(columnScheme);
    this.baseQuery = sqlDataSource.createBaseQueryString(sqlDataSource.getSchemaName(), tableName);

    String dbTimeColumnName = sqlDataSource.getDbColumnName(factory.getTimeFieldString(), tableName);

    this.queryFull = createQueryFull(sqlDataSource.getSchemaName(), tableName);
    this.queryTimeInterval = createQueryForTimeInterval(sqlDataSource.getSchemaName(), tableName, dbTimeColumnName);
    this.queryTime = createQueryForTime(sqlDataSource.getSchemaName(), tableName, dbTimeColumnName);
  }


  /**
   * Initializes a new SqlTimeSeriesSource
   *
   * @param connector the connector needed for database connection
   * @param schemaName the database schema to use
   * @param namingStrategy the naming strategy for database entities
   * @param timeSeriesUuid the uuid of the time series
   * @param valueClass the class of returned time series values
   * @param factory a factory that parses the input data
   */
  public SqlTimeSeriesSource(
          SqlConnector connector,
          String schemaName,
          DatabaseNamingStrategy namingStrategy,
          UUID timeSeriesUuid,
          String specialPlace,
          Class<V> valueClass,
          TimeBasedSimpleValueFactory<V> factory) {
    this(new SqlDataSource(connector, schemaName, namingStrategy), timeSeriesUuid, specialPlace, valueClass, factory);
  }


  /**
   * Queries that are available within this source. Motivation to have them as field value is to
   * avoid creating a new string each time, bc they're always the same.
   */
  private final String queryFull;
  private final String queryTimeInterval;
  private final String queryTime;

  /**
   * Factory method to build a source from given meta information
   *
   * @param connector the connector needed for database connection
   * @param schemaName the database schema to use
   * @param namingStrategy the database entity naming strategy to use
   * @param metaInformation the time series meta information
   * @param timePattern the pattern of time values
   * @return a SqlTimeSeriesSource for given time series table
   * @throws SourceException if the column scheme is not supported
   */
  public static SqlTimeSeriesSource<? extends Value> createSource(
      SqlConnector connector,
      String schemaName,
      DatabaseNamingStrategy namingStrategy,
      IndividualTimeSeriesMetaInformation metaInformation,
      String timePattern)
      throws SourceException {
    if (!TimeSeriesUtils.isSchemeAccepted(metaInformation.getColumnScheme()))
      throw new SourceException(
          "Unsupported column scheme '" + metaInformation.getColumnScheme() + "'.");

    Class<? extends Value> valClass = metaInformation.getColumnScheme().getValueClass();

    return create(
        connector, schemaName, namingStrategy, metaInformation.getUuid(), valClass, timePattern);
  }

  private static <T extends Value> SqlTimeSeriesSource<T> create(
      SqlConnector connector,
      String schemaName,
      DatabaseNamingStrategy namingStrategy,
      UUID timeSeriesUuid,
      Class<T> valClass,
      String timePattern) {
    TimeBasedSimpleValueFactory<T> valueFactory =
        new TimeBasedSimpleValueFactory<>(valClass, timePattern);
    return new SqlTimeSeriesSource<>(
        connector, schemaName, namingStrategy, timeSeriesUuid, "", valClass, valueFactory);
  }

  /*
   protected Optional<TimeBasedValue<V>> createEntity(Map<String, String> fieldToValues) {
    fieldToValues.remove("timeSeries");
    SimpleTimeBasedValueData<V> factoryData =
        new SimpleTimeBasedValueData<>(fieldToValues, valueClass);
    return valueFactory.get(factoryData);
  }
  */

  /**
   * Build a {@link TimeBasedValue} of type {@code V}, whereas the underlying {@link Value} does not
   * need any additional information.
   *
   * @param fieldToValues Mapping from field id to values
   * @param valueClass Class of the desired underlying value
   * @param factory Factory to process the "flat" information
   * @return Optional simple time based value
   */

  public Optional<TimeBasedValue<V>> buildTimeBasedValueReduced(
          Map<String, String> fieldToValues,
          Class<V> valueClass,
          TimeBasedSimpleValueFactory<V> factory) {
    fieldToValues.remove("timeSeries");
    return buildTimeBasedValue(fieldToValues, valueClass, factory);
  }

  /**
   * Creates a base query to retrieve all entities for this time series: <br>
   * {@code <base query> WHERE time_series = $timeSeriesUuid AND <time column> BETWEEN ? AND ?;}
   *
   * @param schemaName the name of the database schema
   * @param tableName the name of the database table
   * @return the query string
   */
  private String createQueryFull(String schemaName, String tableName) {
    return baseQuery
        + WHERE
        + TIME_SERIES
        + " = '"
        + timeSeriesUuid.toString()
        + "'";
  }

  /**
   * Creates a base query to retrieve all entities for given time series uuid and in the given time
   * frame with the following pattern: <br>
   * {@code <base query> WHERE time_series = $timeSeriesUuid AND <time column> BETWEEN ? AND ?;}
   *
   * @param schemaName the name of the database schema
   * @param tableName the name of the database table
   * @param timeColumnName the name of the column holding the timestamp info
   * @return the query string
   */
  private String createQueryForTimeInterval(
      String schemaName, String tableName, String timeColumnName) {
    return baseQuery
        + WHERE
        + TIME_SERIES
        + " = '"
        + timeSeriesUuid.toString()
        + "' AND "
        + timeColumnName
        + " BETWEEN ? AND ?;";
  }

  /**
   * Creates a basic query to retrieve an entry for the given time series uuid and time with the
   * following pattern: <br>
   * {@code <base query> WHERE time_series = $timeSeriesUuid AND <time column>=?;}
   *
   * @param schemaName the name of the database schema
   * @param tableName the name of the database table
   * @param timeColumnName the name of the column holding the timestamp info
   * @return the query string
   */
  private String createQueryForTime(String schemaName, String tableName, String timeColumnName) {
    return baseQuery
        + WHERE
        + TIME_SERIES
        + " = '"
        + timeSeriesUuid.toString()
        + "' AND "
        + timeColumnName
        + "=?;";
  }


}