/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import static edu.ie3.datamodel.io.source.sql.SqlDataSource.createBaseQueryString;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.factory.timeseries.LoadProfileFactory;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.source.LoadProfileSource;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.models.value.load.LoadValues;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sql source for {@link LoadProfileTimeSeries}.
 *
 * @param <P> type of load profile
 * @param <V> type of load values
 */
public class SqlLoadProfileSource<P extends LoadProfile, V extends LoadValues>
    extends LoadProfileSource<P, V> {
  protected static final Logger log = LoggerFactory.getLogger(SqlTimeSeriesSource.class);
  private final SqlDataSource dataSource;
  private final String tableName;

  private final LoadProfileTimeSeriesMetaInformation metaInformation;
  private final P loadProfile;

  // General fields
  private static final String WHERE = " WHERE ";
  private static final String TIME_SERIES = "time_series";
  private static final String LOAD_PROFILE = "load_profile";

  /**
   * Queries that are available within this source. Motivation to have them as field value is to
   * avoid creating a new string each time, bc they're always the same.
   */
  private final String queryFull;

  private final String queryTime;

  public SqlLoadProfileSource(
      SqlDataSource dataSource,
      LoadProfileTimeSeriesMetaInformation metaInformation,
      Class<V> entryClass,
      LoadProfileFactory<P, V> entryFactory) {
    super(entryClass, entryFactory);
    this.dataSource = dataSource;

    this.tableName = dataSource.databaseNamingStrategy.getLoadProfileTimeSeriesEntityName();
    this.metaInformation = metaInformation;
    this.loadProfile = entryFactory.parseProfile(metaInformation.getProfile());

    String dbTimeColumnName =
        dataSource.getDbColumnName(entryFactory.getTimeFieldString(), tableName);

    this.queryFull = createQueryFull(dataSource.schemaName, tableName);
    this.queryTime = createQueryForTime(dataSource.schemaName, tableName, dbTimeColumnName);
  }

  public SqlLoadProfileSource(
      SqlConnector connector,
      String schemaName,
      DatabaseNamingStrategy namingStrategy,
      LoadProfileTimeSeriesMetaInformation metaInformation,
      Class<V> entryClass,
      LoadProfileFactory<P, V> entryFactory) {
    this(
        new SqlDataSource(connector, schemaName, namingStrategy),
        metaInformation,
        entryClass,
        entryFactory);
  }

  @Override
  public void validate() throws ValidationException {
    validate(entryClass, () -> dataSource.getSourceFields(tableName), entryFactory);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  @Override
  public LoadProfileTimeSeries<V> getTimeSeries() {
    Set<LoadProfileEntry<V>> entries = getEntries(queryFull, ps -> {});
    return entryFactory.build(metaInformation, entries);
  }

  @Override
  public List<ZonedDateTime> getTimeKeysAfter(ZonedDateTime time) {
    return List.of(time.plusMinutes(15));
  }

  @Override
  public Optional<PValue> getValue(ZonedDateTime time) throws SourceException {
    Set<LoadProfileEntry<V>> entries =
        getEntries(queryTime, ps -> ps.setInt(1, TimeSeriesUtils.calculateQuarterHourOfDay(time)));
    if (entries.isEmpty()) return Optional.empty();
    if (entries.size() > 1) log.warn("Retrieved more than one result value, using the first");
    return Optional.of(entries.stream().toList().get(0).getValue().getValue(time, loadProfile));
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
   * Creates a set of {@link LoadProfileEntry} from database.
   *
   * @param query to execute
   * @param addParams additional parameters
   * @return a set of {@link LoadProfileEntry}
   */
  private Set<LoadProfileEntry<V>> getEntries(String query, SqlDataSource.AddParams addParams) {
    return dataSource
        .executeQuery(query, addParams)
        .map(this::createEntity)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

  /**
   * Build a {@link LoadProfileEntry} of type {@code V}, whereas the underlying {@link Value} does
   * not need any additional information.
   *
   * @param fieldToValues Mapping from field id to values
   * @return optional {@link LoadProfileEntry}
   */
  private Optional<LoadProfileEntry<V>> createEntity(Map<String, String> fieldToValues) {
    fieldToValues.remove("timeSeries");
    return createEntries(fieldToValues).getData();
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
        + TIME_SERIES
        + " = '"
        + metaInformation.getUuid().toString()
        + "' AND "
        + LOAD_PROFILE
        + " = '"
        + loadProfile.getKey()
        + "'";
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
        + TIME_SERIES
        + " = '"
        + metaInformation.getUuid().toString()
        + "' AND "
        + LOAD_PROFILE
        + " = '"
        + loadProfile.getKey()
        + "' AND "
        + timeColumnName
        + "=?;";
  }
}
