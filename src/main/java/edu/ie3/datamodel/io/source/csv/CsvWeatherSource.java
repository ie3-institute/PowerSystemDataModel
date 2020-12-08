/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.csv.FileNamingStrategy;
import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueFactory;
import edu.ie3.datamodel.io.source.IdCoordinateSource;
import edu.ie3.datamodel.io.source.WeatherSource;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.util.interval.ClosedInterval;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jgrapht.alg.util.Pair;
import org.locationtech.jts.geom.Point;

/** Implements a WeatherSource for CSV files by using the CsvTimeSeriesSource as a base */
public class CsvWeatherSource extends CsvDataSource implements WeatherSource {

  private final TimeBasedWeatherValueFactory weatherFactory;

  private final Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries;
  private final IdCoordinateSource coordinateSource;

  /**
   * Initializes a CsvWeatherSource with a {@link CsvIdCoordinateSource} instance and immediately
   * imports weather data, which will be kept for the lifetime of this source
   *
   * @param csvSep the separator string for csv columns
   * @param folderPath path to the folder holding the time series files
   * @param fileNamingStrategy strategy for the naming of time series files
   * @param weatherFactory factory to transfer field to value mapping into actual java object
   *     instances
   */
  public CsvWeatherSource(
      String csvSep,
      String folderPath,
      FileNamingStrategy fileNamingStrategy,
      TimeBasedWeatherValueFactory weatherFactory) {
    this(
        csvSep,
        folderPath,
        fileNamingStrategy,
        new CsvIdCoordinateSource(csvSep, folderPath, fileNamingStrategy),
        weatherFactory);
  }

  /**
   * Initializes a CsvWeatherSource and immediately imports weather data, which will be kept for the
   * lifetime of this source
   *
   * @param csvSep the separator string for csv columns
   * @param folderPath path to the folder holding the time series files
   * @param fileNamingStrategy strategy for the naming of time series files
   * @param coordinateSource a coordinate source to map ids to points
   * @param weatherFactory factory to transfer field to value mapping into actual java object
   *     instances
   */
  public CsvWeatherSource(
      String csvSep,
      String folderPath,
      FileNamingStrategy fileNamingStrategy,
      IdCoordinateSource coordinateSource,
      TimeBasedWeatherValueFactory weatherFactory) {
    super(csvSep, folderPath, fileNamingStrategy);
    this.coordinateSource = coordinateSource;
    this.weatherFactory = weatherFactory;

    coordinateToTimeSeries = getWeatherTimeSeries();
  }

  /**
   * Creates reader for all available weather time series files and then continues to parse them
   *
   * @return a map of coordinates to their time series
   */
  private Map<Point, IndividualTimeSeries<WeatherValue>> getWeatherTimeSeries() {
    /* Get only weather time series reader */
    Map<ColumnScheme, Set<CsvFileConnector.TimeSeriesReadingData>> colTypeToReadingData =
        connector.initTimeSeriesReader(ColumnScheme.WEATHER);

    /* Reading in weather time series */
    Set<CsvFileConnector.TimeSeriesReadingData> weatherReadingData =
        colTypeToReadingData.get(ColumnScheme.WEATHER);

    return readWeatherTimeSeries(weatherReadingData);
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval) {
    return trimMapToInterval(coordinateToTimeSeries, timeInterval);
  }

  @Override
  public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(
      ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
    Map<Point, IndividualTimeSeries<WeatherValue>> filteredMap =
        coordinateToTimeSeries.entrySet().stream()
            .filter(entry -> coordinates.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return trimMapToInterval(filteredMap, timeInterval);
  }

  @Override
  public Optional<TimeBasedValue<WeatherValue>> getWeather(ZonedDateTime date, Point coordinate) {
    IndividualTimeSeries<WeatherValue> timeSeries = coordinateToTimeSeries.get(coordinate);
    if (timeSeries == null) return Optional.empty();
    return timeSeries.getTimeBasedValue(date);
  }

  /**
   * Trims all time series in a map to the given time interval
   *
   * @param map the map to trim the time series value of
   * @param timeInterval the interval to trim the data to
   * @return a map with trimmed time series
   */
  private Map<Point, IndividualTimeSeries<WeatherValue>> trimMapToInterval(
      Map<Point, IndividualTimeSeries<WeatherValue>> map,
      ClosedInterval<ZonedDateTime> timeInterval) {
    // decided against parallel mode here as it likely wouldn't pay off as the expected coordinate
    // count is too low
    return map.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry -> trimTimeSeriesToInterval(entry.getValue(), timeInterval)));
  }

  /**
   * Trims a time series to the given time interval
   *
   * @param timeSeries the time series to trim
   * @param timeInterval the interval to trim the data to
   * @return the trimmed time series
   */
  private IndividualTimeSeries<WeatherValue> trimTimeSeriesToInterval(
      IndividualTimeSeries<WeatherValue> timeSeries, ClosedInterval<ZonedDateTime> timeInterval) {
    return new IndividualTimeSeries<>(
        timeSeries.getUuid(),
        timeSeries.getEntries().stream()
            .parallel()
            .filter(value -> timeInterval.includes(value.getTime()))
            .collect(Collectors.toSet()));
  }

  /**
   * Reads weather data to time series and maps them coordinate wise
   *
   * @param weatherReadingData Data needed for reading
   * @return time series mapped to the represented coordinate
   */
  private Map<Point, IndividualTimeSeries<WeatherValue>> readWeatherTimeSeries(
      Set<CsvFileConnector.TimeSeriesReadingData> weatherReadingData) {
    final Map<Point, IndividualTimeSeries<WeatherValue>> weatherTimeSeries = new HashMap<>();
    Function<Map<String, String>, Optional<TimeBasedValue<WeatherValue>>> fieldToValueFunction =
        this::buildWeatherValue;

    /* Reading in weather time series */
    for (CsvFileConnector.TimeSeriesReadingData data : weatherReadingData) {
      filterEmptyOptionals(
              buildStreamWithFieldsToAttributesMap(TimeBasedValue.class, data.getReader())
                  .map(fieldToValueFunction))
          .collect(Collectors.groupingBy(tbv -> tbv.getValue().getCoordinate()))
          .forEach(
              (point, timeBasedValues) -> {
                // We have to generate a random UUID as we'd risk running into duplicate key issues
                // otherwise
                IndividualTimeSeries<WeatherValue> timeSeries =
                    new IndividualTimeSeries<>(UUID.randomUUID(), new HashSet<>(timeBasedValues));
                if (weatherTimeSeries.containsKey(point)) {
                  IndividualTimeSeries<WeatherValue> mergedTimeSeries =
                      mergeTimeSeries(weatherTimeSeries.get(point), timeSeries);
                  weatherTimeSeries.put(point, mergedTimeSeries);
                } else {
                  weatherTimeSeries.put(point, timeSeries);
                }
              });
    }
    return weatherTimeSeries;
  }

  /**
   * Builds a {@link TimeBasedValue} of type {@link WeatherValue} from given "flat " input
   * information. If the single model cannot be built, an empty optional is handed back.
   *
   * @param fieldToValues "flat " input information as a mapping from field to value
   * @return Optional time based weather value
   */
  private Optional<TimeBasedValue<WeatherValue>> buildWeatherValue(
      Map<String, String> fieldToValues) {
    /* Try to get the coordinate from entries */
    Optional<Point> maybeCoordinate = extractCoordinate(fieldToValues);
    return maybeCoordinate
        .map(
            coordinate -> {
              /* Remove coordinate entry from fields */
              fieldToValues.remove(weatherFactory.getCoordinateIdFieldString());

              /* Build factory data */
              TimeBasedWeatherValueData factoryData =
                  new TimeBasedWeatherValueData(fieldToValues, coordinate);
              return weatherFactory.get(factoryData);
            })
        .orElseGet(
            () -> {
              log.error("Unable to find coordinate for entry '{}'.", fieldToValues);
              return Optional.empty();
            });
  }

  /**
   * Reads the first line (considered to be the headline with headline fields) and returns a stream
   * of (fieldName to fieldValue) mapping where each map represents one row of the .csv file. Since
   * the returning stream is a parallel stream, the order of the elements cannot be guaranteed.
   *
   * <p>This method overrides {@link CsvDataSource#buildStreamWithFieldsToAttributesMap(Class,
   * BufferedReader)} to not do sanity check for available UUID. This is because the weather source
   * might make use of ICON weather data, which don't have a UUID. For weather it is indeed not
   * necessary, to have one unique UUID.
   *
   * @param entityClass the entity class that should be build
   * @param bufferedReader the reader to use
   * @return a parallel stream of maps, where each map represents one row of the csv file with the
   *     mapping (fieldName to fieldValue)
   */
  @Override
  protected Stream<Map<String, String>> buildStreamWithFieldsToAttributesMap(
      Class<? extends UniqueEntity> entityClass, BufferedReader bufferedReader) {
    try (BufferedReader reader = bufferedReader) {
      final String[] headline = parseCsvRow(reader.readLine(), csvSep);

      // by default try-with-resources closes the reader directly when we leave this method (which
      // is wanted to avoid a lock on the file), but this causes a closing of the stream as well.
      // As we still want to consume the data at other places, we start a new stream instead of
      // returning the original one
      Collection<Map<String, String>> allRows = csvRowFieldValueMapping(reader, headline);

      return distinctRowsWithLog(entityClass, allRows).parallelStream();

    } catch (IOException e) {
      log.warn(
          "Cannot read file to build entity '{}': {}", entityClass.getSimpleName(), e.getMessage());
    }

    return Stream.empty();
  }

  /**
   * Returns a collection of maps each representing a row in csv file that can be used to built an
   * instance of a {@link UniqueEntity}. The uniqueness of each row is doubled checked by a) that no
   * duplicated rows are returned that are full (1:1) matches and b) that no rows are returned that
   * have the same composite primary key (in terms of date and coordinate id) but different field
   * values. As the later case (b) is destroying the contract of unique primary keys, an empty set
   * is returned to indicate that these data cannot be processed safely and the error is logged. For
   * case a), only the duplicates are filtered out an a set with unique rows is returned.
   *
   * @param entityClass the entity class that should be built based on the provided (fieldName to
   *     fieldValue) collection
   * @param allRows collection of rows of a csv file an entity should be built from
   * @param <T> type of the entity
   * @return either a set containing only unique rows or an empty set if at least two rows with the
   *     same UUID but different field values exist
   */
  @Override
  protected <T extends UniqueEntity> Set<Map<String, String>> distinctRowsWithLog(
      Class<T> entityClass, Collection<Map<String, String>> allRows) {
    Set<Map<String, String>> allRowsSet = new HashSet<>(allRows);
    // check for duplicated rows that match exactly (full duplicates) -> sanity only, not crucial
    if (allRows.size() != allRowsSet.size()) {
      log.warn(
          "File with '{}' entities contains {} exact duplicated rows. File cleanup is recommended!",
          entityClass.getSimpleName(),
          (allRows.size() - allRowsSet.size()));
    }

    // check for rows that match exactly by their UUID, but have different fields -> crucial, we
    // allow only unique UUID entities
    Set<Map<String, String>> distinctUuidRowSet =
        allRowsSet
            .parallelStream()
            .filter(
                distinctByKey(
                    fieldToValues ->
                        Pair.of(
                            fieldToValues.get(weatherFactory.getTimeFieldString()),
                            fieldToValues.get(weatherFactory.getCoordinateIdFieldString()))))
            .collect(Collectors.toSet());
    if (distinctUuidRowSet.size() != allRowsSet.size()) {
      allRowsSet.removeAll(distinctUuidRowSet);
      String affectedTimesAndCoordinates =
          allRowsSet.stream()
              .map(
                  row ->
                      Pair.of(
                              row.get(weatherFactory.getTimeFieldString()),
                              row.get(weatherFactory.getCoordinateIdFieldString()))
                          .toString())
              .collect(Collectors.joining(",\n"));
      log.error(
          "'{}' entities with duplicated composite primary key, but different field values found! Please review the corresponding input file!\nAffected primary keys:\n{}",
          entityClass.getSimpleName(),
          affectedTimesAndCoordinates);
      // if this happens, we return an empty set to prevent further processing
      return new HashSet<>();
    }

    return allRowsSet;
  }

  /**
   * State full predicate to allow for filtering distinct elements by a key
   *
   * @param keyExtractor Function, that extracts the key, the elements may be distinct in
   * @param <T> Type of elements to filter
   * @return True, if the elements hasn't been seen, yet. False otherwise
   * @see <a href="https://www.baeldung.com/java-streams-distinct-by">This baeldung tutorial</a>
   */
  private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
  }

  /**
   * Extract the coordinate identifier from the field to value mapping and obtain the actual
   * coordinate in collaboration with the source.
   *
   * @param fieldToValues "flat " input information as a mapping from field to value
   * @return Optional time based weather value
   */
  private Optional<Point> extractCoordinate(Map<String, String> fieldToValues) {
    String coordinateString = fieldToValues.get(weatherFactory.getCoordinateIdFieldString());
    if (Objects.isNull(coordinateString) || coordinateString.isEmpty()) {
      log.error(
          "Cannot parse weather value. Unable to find field '{}' in data: {}",
          weatherFactory.getCoordinateIdFieldString(),
          fieldToValues);
      return Optional.empty();
    }
    int coordinateId = Integer.parseInt(coordinateString);
    return coordinateSource.getCoordinate(coordinateId);
  }

  /**
   * Merge two individual time series into a new time series with the UUID of the first parameter
   *
   * @param a the first time series to merge
   * @param b the second time series to merge
   * @return merged time series with a's UUID
   */
  private <V extends Value> IndividualTimeSeries<V> mergeTimeSeries(
      IndividualTimeSeries<V> a, IndividualTimeSeries<V> b) {
    SortedSet<TimeBasedValue<V>> entries = a.getEntries();
    entries.addAll(b.getEntries());
    return new IndividualTimeSeries<>(a.getUuid(), entries);
  }
}
