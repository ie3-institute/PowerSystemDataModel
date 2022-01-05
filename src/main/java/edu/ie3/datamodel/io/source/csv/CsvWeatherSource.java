/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.factory.timeseries.IdCoordinateFactory;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueFactory;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.IdCoordinateSource;
import edu.ie3.datamodel.io.source.WeatherSource;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.datamodel.utils.TimeSeriesUtil;
import edu.ie3.util.interval.ClosedInterval;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
   * @param fileNamingStrategy strategy for the file naming of time series files / data sinks
   * @param weatherFactory factory to transfer field to value mapping into actual java object
   *     instances
   * @param coordinateFactory factory to build coordinate id to coordinate mapping
   */
  public CsvWeatherSource(
      String csvSep,
      String folderPath,
      FileNamingStrategy fileNamingStrategy,
      TimeBasedWeatherValueFactory weatherFactory,
      IdCoordinateFactory coordinateFactory) {
    this(
        csvSep,
        folderPath,
        fileNamingStrategy,
        new CsvIdCoordinateSource(csvSep, folderPath, fileNamingStrategy, coordinateFactory),
        weatherFactory);
  }

  /**
   * Initializes a CsvWeatherSource and immediately imports weather data, which will be kept for the
   * lifetime of this source
   *
   * @param csvSep the separator string for csv columns
   * @param folderPath path to the folder holding the time series files
   * @param fileNamingStrategy strategy for the file naming of time series files / data sinks
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
    /* Get only weather time series meta information */
    Map<ColumnScheme, Set<CsvFileConnector.CsvIndividualTimeSeriesMetaInformation>>
        colTypeToMetaData =
            connector.getCsvIndividualTimeSeriesMetaInformation(ColumnScheme.WEATHER);

    /* Reading in weather time series */
    Set<CsvFileConnector.CsvIndividualTimeSeriesMetaInformation> weatherCsvMetaInformation =
        colTypeToMetaData.get(ColumnScheme.WEATHER);

    return readWeatherTimeSeries(weatherCsvMetaInformation, connector);
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
                entry -> TimeSeriesUtil.trimTimeSeriesToInterval(entry.getValue(), timeInterval)));
  }

  /**
   * Reads weather data to time series and maps them coordinate wise
   *
   * @param weatherMetaInformation Data needed for reading
   * @return time series mapped to the represented coordinate
   */
  private Map<Point, IndividualTimeSeries<WeatherValue>> readWeatherTimeSeries(
      Set<CsvFileConnector.CsvIndividualTimeSeriesMetaInformation> weatherMetaInformation,
      CsvFileConnector connector) {
    final Map<Point, IndividualTimeSeries<WeatherValue>> weatherTimeSeries = new HashMap<>();
    Function<Map<String, String>, Optional<TimeBasedValue<WeatherValue>>> fieldToValueFunction =
        this::buildWeatherValue;
    /* Reading in weather time series */
    for (CsvFileConnector.CsvIndividualTimeSeriesMetaInformation data : weatherMetaInformation) {
      // we need a reader for each file
      try (BufferedReader reader = connector.initReader(data.getFullFilePath())) {
        filterEmptyOptionals(
                buildStreamWithFieldsToAttributesMap(TimeBasedValue.class, reader)
                    .map(fieldToValueFunction))
            .collect(Collectors.groupingBy(tbv -> tbv.getValue().getCoordinate()))
            .forEach(
                (point, timeBasedValues) -> {
                  // We have to generate a random UUID as we'd risk running into duplicate key
                  // issues
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
      } catch (FileNotFoundException e) {
        log.error("Cannot read file {}. File not found!", data.getFullFilePath());
      } catch (IOException e) {
        log.error("Cannot read file {}. Exception: {}", data.getFullFilePath(), e);
      }
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

      Function<Map<String, String>, String> timeCoordinateIdExtractor =
          fieldToValues ->
              fieldToValues
                  .get(weatherFactory.getTimeFieldString())
                  .concat(fieldToValues.get(weatherFactory.getCoordinateIdFieldString()));
      return distinctRowsWithLog(
          allRows, timeCoordinateIdExtractor, entityClass.getSimpleName(), "UUID")
          .parallelStream();

    } catch (IOException e) {
      log.warn(
          "Cannot read file to build entity '{}': {}", entityClass.getSimpleName(), e.getMessage());
    }

    return Stream.empty();
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
