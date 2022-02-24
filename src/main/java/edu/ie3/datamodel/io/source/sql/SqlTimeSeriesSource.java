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
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.util.interval.ClosedInterval;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.*;

public class SqlTimeSeriesSource<V extends Value> extends SqlDataSource<TimeBasedValue<V>>
    implements TimeSeriesSource<V> {
  private static final String WHERE = " WHERE ";

  private final UUID timeSeriesUuid;
  private final Class<V> valueClass;
  private final TimeBasedSimpleValueFactory<V> valueFactory;

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
   * @param tableName the database table to use
   * @param metaInformation the time series meta information
   * @param timePattern the pattern of time values
   * @return a SqlTimeSeriesSource for given time series table
   * @throws SourceException if the column scheme is not supported
   * @deprecated since 3.0. Use {@link #createSource(SqlConnector, String, DatabaseNamingStrategy,
   *     edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation, String)}
   *     instead.
   */
  @Deprecated(since = "3.0", forRemoval = true)
  public static SqlTimeSeriesSource<? extends Value> getSource(
      SqlConnector connector,
      String schemaName,
      String tableName,
      edu.ie3.datamodel.io.csv.timeseries.IndividualTimeSeriesMetaInformation metaInformation,
      String timePattern)
      throws SourceException {
    if (!TimeSeriesSource.isSchemeAccepted(metaInformation.getColumnScheme()))
      throw new SourceException(
          "Unsupported column scheme '" + metaInformation.getColumnScheme() + "'.");

    Class<? extends Value> valClass = metaInformation.getColumnScheme().getValueClass();

    return create(
        connector, schemaName, tableName, metaInformation.getUuid(), valClass, timePattern);
  }

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
    if (!TimeSeriesSource.isSchemeAccepted(metaInformation.getColumnScheme()))
      throw new SourceException(
          "Unsupported column scheme '" + metaInformation.getColumnScheme() + "'.");

    Class<? extends Value> valClass = metaInformation.getColumnScheme().getValueClass();

    return create(
        connector, schemaName, namingStrategy, metaInformation.getUuid(), valClass, timePattern);
  }

  /**
   * @deprecated since 3.0. Use {@link #create(SqlConnector, String, DatabaseNamingStrategy, UUID,
   *     Class, String)} instead
   */
  @Deprecated(since = "3.0", forRemoval = true)
  private static <T extends Value> SqlTimeSeriesSource<T> create(
      SqlConnector connector,
      String schemaName,
      String tableName,
      UUID timeSeriesUuid,
      Class<T> valClass,
      String timePattern) {
    TimeBasedSimpleValueFactory<T> valueFactory =
        new TimeBasedSimpleValueFactory<>(valClass, timePattern);
    return new SqlTimeSeriesSource<>(
        connector, schemaName, tableName, timeSeriesUuid, valClass, valueFactory);
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
        connector, schemaName, namingStrategy, timeSeriesUuid, valClass, valueFactory);
  }

  /**
   * Initializes a new SqlTimeSeriesSource
   *
   * @param connector the connector needed for database connection
   * @param schemaName the database schema to use
   * @param tableName the database table to use
   * @param timeSeriesUuid the uuid of the time series
   * @param valueClass the class of returned time series values
   * @param factory a factory that parses the input data
   * @deprecated since 3.0. Use {@link #SqlTimeSeriesSource(SqlConnector,
   *     String,DatabaseNamingStrategy, UUID, Class, TimeBasedSimpleValueFactory)} instead
   */
  @Deprecated(since = "3.0", forRemoval = true)
  public SqlTimeSeriesSource(
      SqlConnector connector,
      String schemaName,
      String tableName,
      UUID timeSeriesUuid,
      Class<V> valueClass,
      TimeBasedSimpleValueFactory<V> factory) {
    super(connector);
    this.timeSeriesUuid = timeSeriesUuid;
    this.valueClass = valueClass;
    this.valueFactory = factory;

    String dbTimeColumnName = getDbColumnName(factory.getTimeFieldString(), tableName);

    this.queryFull = createBaseQueryString(schemaName, tableName);
    this.queryTimeInterval =
        createQueryStringForTimeInterval(schemaName, tableName, dbTimeColumnName);
    this.queryTime = createQueryStringForTime(schemaName, tableName, dbTimeColumnName);
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
      Class<V> valueClass,
      TimeBasedSimpleValueFactory<V> factory) {
    super(connector);
    this.timeSeriesUuid = timeSeriesUuid;
    this.valueClass = valueClass;
    this.valueFactory = factory;
    final ColumnScheme columnScheme = ColumnScheme.parse(valueClass).orElseThrow();
    final String tableName = namingStrategy.getTimeSeriesEntityName(columnScheme);

    String dbTimeColumnName = getDbColumnName(factory.getTimeFieldString(), tableName);

    this.queryFull = createQueryFull(schemaName, tableName);
    this.queryTimeInterval = createQueryForTimeInterval(schemaName, tableName, dbTimeColumnName);
    this.queryTime = createQueryForTime(schemaName, tableName, dbTimeColumnName);
  }

  @Override
  public IndividualTimeSeries<V> getTimeSeries() {
    List<TimeBasedValue<V>> timeBasedValues = executeQuery(queryFull, ps -> {});
    return new IndividualTimeSeries<>(timeSeriesUuid, new HashSet<>(timeBasedValues));
  }

  @Override
  public IndividualTimeSeries<V> getTimeSeries(ClosedInterval<ZonedDateTime> timeInterval) {
    List<TimeBasedValue<V>> timeBasedValues =
        executeQuery(
            queryTimeInterval,
            ps -> {
              ps.setTimestamp(1, Timestamp.from(timeInterval.getLower().toInstant()));
              ps.setTimestamp(2, Timestamp.from(timeInterval.getUpper().toInstant()));
            });
    return new IndividualTimeSeries<>(timeSeriesUuid, new HashSet<>(timeBasedValues));
  }

  @Override
  public Optional<V> getValue(ZonedDateTime time) {
    List<TimeBasedValue<V>> timeBasedValues =
        executeQuery(queryTime, ps -> ps.setTimestamp(1, Timestamp.from(time.toInstant())));
    if (timeBasedValues.isEmpty()) return Optional.empty();
    if (timeBasedValues.size() > 1)
      log.warn("Retrieved more than one result value, using the first");
    return Optional.of(timeBasedValues.get(0).getValue());
  }

  /**
   * Build a {@link TimeBasedValue} of type {@code V}, whereas the underlying {@link Value} does not
   * need any additional information.
   *
   * @param fieldToValues Mapping from field id to values
   * @return Optional simple time based value
   */
  protected Optional<TimeBasedValue<V>> createEntity(Map<String, String> fieldToValues) {
    fieldToValues.remove("timeSeries");
    SimpleTimeBasedValueData<V> factoryData =
        new SimpleTimeBasedValueData<>(fieldToValues, valueClass);
    return valueFactory.get(factoryData);
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
    return createBaseQueryString(schemaName, tableName)
        + WHERE
        + "time_series = '"
        + timeSeriesUuid.toString()
        + "'";
  }

  /**
   * Creates a base query to retrieve all entities in the given time frame with the following
   * pattern: <br>
   * {@code <base query> WHERE <time column> BETWEEN ? AND ?;}
   *
   * @param schemaName the name of the database schema
   * @param tableName the name of the database table
   * @param timeColumnName the name of the column holding the timestamp info
   * @return the query string
   */
  @Deprecated(since = "3.0", forRemoval = true)
  private static String createQueryStringForTimeInterval(
      String schemaName, String tableName, String timeColumnName) {
    return createBaseQueryString(schemaName, tableName)
        + WHERE
        + timeColumnName
        + " BETWEEN ? AND ?;";
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
    return createBaseQueryString(schemaName, tableName)
        + WHERE
        + "time_series = '"
        + timeSeriesUuid.toString()
        + "' AND "
        + timeColumnName
        + " BETWEEN ? AND ?;";
  }

  /**
   * Creates a basic query to retrieve an entry for the given time with the following pattern: <br>
   * {@code <base query> WHERE <time column>=?;}
   *
   * @param schemaName the name of the database schema
   * @param weatherTableName the name of the database table
   * @param timeColumnName the name of the column holding the timestamp info
   * @return the query string
   */
  @Deprecated(since = "3.0", forRemoval = true)
  private String createQueryStringForTime(
      String schemaName, String weatherTableName, String timeColumnName) {
    return createBaseQueryString(schemaName, weatherTableName) + WHERE + timeColumnName + "=?;";
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
    return createBaseQueryString(schemaName, tableName)
        + WHERE
        + "time_series = '"
        + timeSeriesUuid.toString()
        + "' AND "
        + timeColumnName
        + "=?;";
  }
}
