/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.factory.timeseries.SimpleTimeBasedValueData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory;
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

  /**
   * Factory method to build a source from given meta information
   *
   * @param connector the connector needed for database connection
   * @param schemaName the database schema to use
   * @param tableName the database table to use
   * @param timeSeriesUuid the uuid of the time series
   * @param columnScheme the column scheme of this time series
   * @param timePattern the pattern of time values
   * @return a SqlTimeSeriesSource for given time series table
   * @throws SourceException if the column scheme is not supported
   */
  public static SqlTimeSeriesSource<? extends Value> getSource(
      SqlConnector connector,
      String schemaName,
      String tableName,
      UUID timeSeriesUuid,
      ColumnScheme columnScheme,
      String timePattern)
      throws SourceException {
    if (!TimeSeriesSource.isSchemeAccepted(columnScheme))
      throw new SourceException("Unsupported column scheme '" + columnScheme + "'.");

    Class<? extends Value> valClass = columnScheme.getValueClass();

    return create(connector, schemaName, tableName, timeSeriesUuid, valClass, timePattern);
  }

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
   * Initializes a new SqlTimeSeriesSource
   *
   * @param connector the connector needed for database connection
   * @param schemaName the database schema to use
   * @param tableName the database table to use
   * @param timeSeriesUuid the uuid of the time series
   * @param valueClass the class of returned time series values
   * @param factory a factory that parses the input data
   */
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

  @Override
  public IndividualTimeSeries<V> getTimeSeries() {
    List<TimeBasedValue<V>> timeBasedValues = executeQuery(queryFull, (ps) -> {});
    return new IndividualTimeSeries<>(timeSeriesUuid, new HashSet<>(timeBasedValues));
  }

  @Override
  public IndividualTimeSeries<V> getTimeSeries(ClosedInterval<ZonedDateTime> timeInterval) {
    List<TimeBasedValue<V>> timeBasedValues =
        executeQuery(
            queryTimeInterval,
            (ps) -> {
              ps.setTimestamp(1, Timestamp.from(timeInterval.getLower().toInstant()));
              ps.setTimestamp(2, Timestamp.from(timeInterval.getUpper().toInstant()));
            });
    return new IndividualTimeSeries<>(timeSeriesUuid, new HashSet<>(timeBasedValues));
  }

  @Override
  public Optional<V> getValue(ZonedDateTime time) {
    List<TimeBasedValue<V>> timeBasedValues =
        executeQuery(queryTime, (ps) -> ps.setTimestamp(1, Timestamp.from(time.toInstant())));
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
    SimpleTimeBasedValueData<V> factoryData =
        new SimpleTimeBasedValueData<>(fieldToValues, valueClass);
    return valueFactory.get(factoryData);
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
  private static String createQueryStringForTimeInterval(
      String schemaName, String tableName, String timeColumnName) {
    return createBaseQueryString(schemaName, tableName)
        + WHERE
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
  private String createQueryStringForTime(
      String schemaName, String weatherTableName, String timeColumnName) {
    return createBaseQueryString(schemaName, weatherTableName) + WHERE + timeColumnName + "=?;";
  }
}
