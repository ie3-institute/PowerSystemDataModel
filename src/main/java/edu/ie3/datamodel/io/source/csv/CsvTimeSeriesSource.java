/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.connectors.CsvFileConnector.TimeSeriesReadingData;
import edu.ie3.datamodel.io.csv.FileNamingStrategy;
import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.factory.timeseries.*;
import edu.ie3.datamodel.io.source.IdCoordinateSource;
import edu.ie3.datamodel.io.source.TimeSeriesSource;
import edu.ie3.datamodel.models.timeseries.TimeSeriesContainer;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.timeseries.mapping.TimeSeriesMapping;
import edu.ie3.datamodel.models.value.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Point;

/** Source that is capable of providing information around time series from csv files. */
public class CsvTimeSeriesSource extends CsvDataSource implements TimeSeriesSource {
  /* Available factories */
  private final TimeSeriesMappingFactory mappingFactory = new TimeSeriesMappingFactory();
  private final TimeBasedWeatherValueFactory weatherFactory = new TimeBasedWeatherValueFactory();
  private final TimeBasedSimpleValueFactory<EnergyPriceValue> energyPriceFactory =
      new TimeBasedSimpleValueFactory<>(EnergyPriceValue.class);
  private final TimeBasedSimpleValueFactory<HeatAndSValue> heatAndSValueFactory =
      new TimeBasedSimpleValueFactory<>(HeatAndSValue.class);
  private final TimeBasedSimpleValueFactory<HeatAndPValue> heatAndPValueFactory =
      new TimeBasedSimpleValueFactory<>(HeatAndPValue.class);
  private final TimeBasedSimpleValueFactory<HeatDemandValue> heatDemandValueFactory =
      new TimeBasedSimpleValueFactory<>(HeatDemandValue.class);
  private final TimeBasedSimpleValueFactory<SValue> sValueFactory =
      new TimeBasedSimpleValueFactory<>(SValue.class);
  private final TimeBasedSimpleValueFactory<PValue> pValueFactory =
      new TimeBasedSimpleValueFactory<>(PValue.class);

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

  /**
   * Acquire all available time series
   *
   * @return A container with all relevant time series
   */
  @Override
  public TimeSeriesContainer getTimeSeries() {
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

    /* Reading in energy price time series */
    Set<IndividualTimeSeries<EnergyPriceValue>> energyPriceTimeSeries =
        read(
            colTypeToReadingData.get(ColumnScheme.ENERGY_PRICE),
            EnergyPriceValue.class,
            energyPriceFactory);

    /* Reading in heat and apparent power time series */
    Set<IndividualTimeSeries<HeatAndSValue>> heatAndApparentPowerTimeSeries =
        read(
            colTypeToReadingData.get(ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND),
            HeatAndSValue.class,
            heatAndSValueFactory);

    /* Reading in heat time series */
    Set<IndividualTimeSeries<HeatDemandValue>> heatTimeSeries =
        read(
            colTypeToReadingData.get(ColumnScheme.HEAT_DEMAND),
            HeatDemandValue.class,
            heatDemandValueFactory);

    /* Reading in heat and active power time series */
    Set<IndividualTimeSeries<HeatAndPValue>> heatAndActivePowerTimeSeries =
        read(
            colTypeToReadingData.get(ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND),
            HeatAndPValue.class,
            heatAndPValueFactory);

    /* Reading in apparent power time series */
    Set<IndividualTimeSeries<SValue>> apparentPowerTimeSeries =
        read(colTypeToReadingData.get(ColumnScheme.APPARENT_POWER), SValue.class, sValueFactory);

    /* Reading in active power time series */
    Set<IndividualTimeSeries<PValue>> activePowerTimeSeries =
        read(colTypeToReadingData.get(ColumnScheme.ACTIVE_POWER), PValue.class, pValueFactory);

    return new TimeSeriesContainer(
        weatherTimeSeries,
        energyPriceTimeSeries,
        heatAndApparentPowerTimeSeries,
        heatAndActivePowerTimeSeries,
        heatTimeSeries,
        apparentPowerTimeSeries,
        activePowerTimeSeries);
  }

  /**
   * Reads in time series of a specified class from given {@link TimeSeriesReadingData} utilising a
   * provided {@link TimeBasedSimpleValueFactory}.
   *
   * @param readingData Data needed for reading
   * @param valueClass Class of the target value within the time series
   * @param factory Factory to utilize
   * @param <V> Type of the value
   * @return A set of {@link IndividualTimeSeries}
   */
  private <V extends Value> Set<IndividualTimeSeries<V>> read(
      Set<TimeSeriesReadingData> readingData,
      Class<V> valueClass,
      TimeBasedSimpleValueFactory<V> factory) {
    Set<IndividualTimeSeries<V>> timeSeries = Collections.emptySet();
    if (!readingData.isEmpty()) {
      Function<Map<String, String>, Optional<TimeBasedValue<V>>> valueFunction =
          fieldToValue -> this.buildTimeBasedValue(fieldToValue, valueClass, factory);
      timeSeries =
          readingData
              .parallelStream()
              .map(data -> buildIndividualTimeSeries(data, valueFunction))
              .collect(Collectors.toSet());
    }
    return timeSeries;
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
   * @return Optional time based weather value
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

  /**
   * Build a {@link TimeBasedValue} of type {@code V}, whereas the underlying {@link Value} does not
   * need any additional information.
   *
   * @param fieldToValues Mapping from field id to values
   * @param valueClass Class of the desired underlying value
   * @param factory Factory to process the "flat" information
   * @param <V> Type of the underlying value
   * @return Optional simple time based value
   */
  private <V extends Value> Optional<TimeBasedValue<V>> buildTimeBasedValue(
      Map<String, String> fieldToValues,
      Class<V> valueClass,
      TimeBasedSimpleValueFactory<V> factory) {
    SimpleTimeBasedValueData<V> factoryData =
        new SimpleTimeBasedValueData<>(fieldToValues, valueClass);
    return factory.get(factoryData);
  }
}
