/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.connectors.TimeSeriesReadingData;
import edu.ie3.datamodel.io.csv.FileNamingStrategy;
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
import org.locationtech.jts.geom.Point;

import java.util.*;
import java.util.stream.Collectors;

/** Source that is capable of providing information around time series from csv files. */
public class CsvTimeSeriesSource extends CsvDataSource implements TimeSeriesSource {
    /* Available factories */
  private final TimeSeriesMappingFactory mappingFactory = new TimeSeriesMappingFactory();
  private final TimeBasedWeatherValueFactory weatherFactory = new TimeBasedWeatherValueFactory();

  private final IdCoordinateSource coordinateSource;

  private static final String COORDINATE_FIELD = "coordinate";

  public CsvTimeSeriesSource(
      String csvSep, String folderPath, FileNamingStrategy fileNamingStrategy, IdCoordinateSource coordinateSource) {
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

  private IndividualTimeSeries<Value> buildIndividualTimeSeries(TimeSeriesReadingData data) {
      Set<TimeBasedValue<Value>> timeBasedValues = filterEmptyOptionals(buildStreamWithFieldsToAttributesMap(TimeBasedValue.class, data.getReader()).map(fieldToValues -> {
          switch (data.getColumnScheme()) {
              case WEATHER:
                  return buildWeatherValue(fieldToValues);
              default:
                  return Optional.empty();
          }
      })).collect(Collectors.toSet());

      return new IndividualTimeSeries<>(data.getUuid(), timeBasedValues);
  }

  private Optional<TimeBasedValue<Value>> buildWeatherValue(Map<String, String> fieldToValues) {
      /* Try to get the coordinate from entries */
      String coordinateString = fieldToValues.get(COORDINATE_FIELD);
      if (Objects.isNull(coordinateString) || coordinateString.isEmpty()) {
          log.error("Cannot parse weather value. Unable to find field '{}' in data: {}", COORDINATE_FIELD, fieldToValues);
          return Optional.empty();
      }
      int coordinateId = Integer.parseInt(coordinateString);
      Point coordinate = coordinateSource.getCoordinate(coordinateId);
      if (Objects.isNull(coordinate)) {
          log.error("Unable to find coordinate with id '{}'.", coordinateId);
          return Optional.empty();
      }

      /* Build factory data */
      TimeBasedWeatherValueData factoryData = new TimeBasedWeatherValueData(fieldToValues, coordinate);
      return weatherFactory.get(factoryData);
  }
}
