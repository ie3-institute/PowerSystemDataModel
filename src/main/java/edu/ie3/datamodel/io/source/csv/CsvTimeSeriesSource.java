/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.connectors.TimeSeriesReadingData;
import edu.ie3.datamodel.io.csv.FileNamingStrategy;
import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedWeatherValueFactory;
import edu.ie3.datamodel.io.factory.timeseries.TimeSeriesMappingFactory;
import edu.ie3.datamodel.io.source.IdCoordinateSource;
import edu.ie3.datamodel.io.source.TimeSeriesSource;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.timeseries.mapping.TimeSeriesMapping;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.models.value.WeatherValue;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Point;

/** Source that is capable of providing information around time series from csv files. */
public class CsvTimeSeriesSource extends CsvDataSource implements TimeSeriesSource {
  /* Available factories */
  private final TimeSeriesMappingFactory mappingFactory = new TimeSeriesMappingFactory();
  private final TimeBasedWeatherValueFactory weatherFactory = new TimeBasedWeatherValueFactory();

  private final IdCoordinateSource coordinateSource;

  private static final String COORDINATE_FIELD = "coordinate";

  public CsvTimeSeriesSource(
      String csvSep,
      String folderPath,
      FileNamingStrategy fileNamingStrategy,
      IdCoordinateSource coordinateSource) {
    super(csvSep, folderPath, fileNamingStrategy);
    this.coordinateSource = coordinateSource;
  }

  /**
   * Receive a set of time series mapping entries from participant uuid to time series uuid.
   *
   * @return A set of time series mapping entries from participant uuid to time series uuid
   */
  @Override
  public Set<TimeSeriesMapping.Entry> getMapping() {
    return filterEmptyOptionals(
            buildStreamWithFieldsToAttributesMap(TimeSeriesMapping.Entry.class, connector)
                .map(
                    fieldToValues -> {
                      SimpleEntityData entityData =
                          new SimpleEntityData(fieldToValues, TimeSeriesMapping.Entry.class);
                      return mappingFactory.get(entityData);
                    }))
        .collect(Collectors.toSet());
  }

  /* FIXME: Under Construction */
  public void getTimeSeries() {
    /* Get all time series reader */
    Map<ColumnScheme, Set<TimeSeriesReadingData>> colTypeToReadingData =
        connector.initTimeSeriesReader();

    /* Reading in weather time series */
    Set<TimeSeriesReadingData> weatherReadingData = colTypeToReadingData.get(ColumnScheme.WEATHER);
    Set<IndividualTimeSeries<WeatherValue>> weatherTimeSeries = Collections.emptySet();
    if (!weatherReadingData.isEmpty()) {
      Function<Map<String, String>, Optional<TimeBasedValue<WeatherValue>>> weatherValueFunction =
          this::buildWeatherValue;
      weatherTimeSeries =
          weatherReadingData
              .parallelStream()
              .map(data -> buildIndividualTimeSeries(data, weatherValueFunction))
              .collect(Collectors.toSet());
    }
  }

  /**
   * Builds an individual time series, by obtaining the single entries (with the help of {@code
   * fieldToValueFunction} and putting everything together in the {@link IndividualTimeSeries}
   * container.
   *
   * @param data Needed data to read in the content of the specific, underlying file
   * @param fieldToValueFunction Function, that is able to transfer a mapping (from field to value)
   *     onto a specific instance of the targeted entry class
   * @param <V> Type of the {@link Value}, that will be contained in each time series, timely
   *     located entry
   * @return An {@link IndividualTimeSeries} with {@link TimeBasedValue} of type {@code V}.
   */
  private <V extends Value> IndividualTimeSeries<V> buildIndividualTimeSeries(
      TimeSeriesReadingData data,
      Function<Map<String, String>, Optional<TimeBasedValue<V>>> fieldToValueFunction) {
    Set<TimeBasedValue<V>> timeBasedValues =
        filterEmptyOptionals(
                buildStreamWithFieldsToAttributesMap(TimeBasedValue.class, data.getReader())
                    .map(fieldToValueFunction))
            .collect(Collectors.toSet());

    return new IndividualTimeSeries<>(data.getUuid(), timeBasedValues);
  }

  /**
   * Builds a {@link TimeBasedValue} of type {@link WeatherValue} from given "flat " input
   * information. If the single model cannot be built, an empty optionl is handed back.
   *
   * @param fieldToValues "flat " input information as a mapping from field to value
   * @return Optional time based weahter value
   */
  private Optional<TimeBasedValue<WeatherValue>> buildWeatherValue(
      Map<String, String> fieldToValues) {
    /* Try to get the coordinate from entries */
    String coordinateString = fieldToValues.get(COORDINATE_FIELD);
    if (Objects.isNull(coordinateString) || coordinateString.isEmpty()) {
      log.error(
          "Cannot parse weather value. Unable to find field '{}' in data: {}",
          COORDINATE_FIELD,
          fieldToValues);
      return Optional.empty();
    }
    int coordinateId = Integer.parseInt(coordinateString);
    Point coordinate = coordinateSource.getCoordinate(coordinateId);
    if (Objects.isNull(coordinate)) {
      log.error("Unable to find coordinate with id '{}'.", coordinateId);
      return Optional.empty();
    }

    /* Remove coordinate entry from fields */
    fieldToValues.remove(COORDINATE_FIELD);

    /* Build factory data */
    TimeBasedWeatherValueData factoryData =
        new TimeBasedWeatherValueData(fieldToValues, coordinate);
    return weatherFactory.get(factoryData);
  }
}
