/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.csv.FileNamingStrategy;
import edu.ie3.datamodel.io.csv.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.factory.timeseries.*;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
import edu.ie3.datamodel.io.source.TimeSeriesSource;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Source that is capable of providing information around time series from csv files. */
public class CsvTimeSeriesSource extends CsvDataSource implements TimeSeriesSource {
  private static final Logger logger = LoggerFactory.getLogger(CsvTimeSeriesSource.class);

  private final TimeSeriesMappingSource mappingSource;

  /* Available factories */
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

  /**
   * Initializes a new CsvTimeSeriesSource
   *
   * @param csvSep the separator string for csv columns
   * @param folderPath path to the folder holding the time series files
   * @param fileNamingStrategy strategy for the naming of time series files
   */
  public CsvTimeSeriesSource(
      String csvSep, String folderPath, FileNamingStrategy fileNamingStrategy) {
    super(csvSep, folderPath, fileNamingStrategy);
    this.mappingSource = new CsvTimeSeriesMappingSource(csvSep, folderPath, fileNamingStrategy);
  }

  /**
   * Initializes a new CsvTimeSeriesSource
   *
   * @param csvSep the separator string for csv columns
   * @param folderPath path to the folder holding the time series files
   * @param fileNamingStrategy strategy for the naming of time series files
   * @param mappingSource Source for mapping between models and time series
   */
  public CsvTimeSeriesSource(
      String csvSep,
      String folderPath,
      FileNamingStrategy fileNamingStrategy,
      TimeSeriesMappingSource mappingSource) {
    super(csvSep, folderPath, fileNamingStrategy);
    this.mappingSource = mappingSource;
  }

  @Override
  public Optional<UUID> getTimeSeriesUuid(UUID modelUuid) {
    return mappingSource.getTimeSeriesUuid(modelUuid);
  }

  @Override
  public Optional<IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation(
      UUID timeSeriesUuid) {
    return connector.getIndividualTimeSeriesMetaInformation(timeSeriesUuid);
  }

  @Override
  public Optional<IndividualTimeSeries<? extends Value>> getTimeSeries(
      IndividualTimeSeriesMetaInformation metaInformation) {
    if (!CsvFileConnector.CsvIndividualTimeSeriesMetaInformation.class.isAssignableFrom(
        metaInformation.getClass())) return Optional.empty();

    CsvFileConnector.CsvIndividualTimeSeriesMetaInformation csvMetaInformation =
        (CsvFileConnector.CsvIndividualTimeSeriesMetaInformation) metaInformation;

    try {
      IndividualTimeSeries<? extends Value> timeSeries;
      switch (metaInformation.getColumnScheme()) {
        case ACTIVE_POWER:
          timeSeries =
              buildIndividualTimeSeries(
                  metaInformation.getUuid(),
                  csvMetaInformation.getFullFilePath(),
                  fieldToValue ->
                      this.buildTimeBasedValue(fieldToValue, PValue.class, pValueFactory));
          break;
        case APPARENT_POWER:
          timeSeries =
              buildIndividualTimeSeries(
                  metaInformation.getUuid(),
                  csvMetaInformation.getFullFilePath(),
                  fieldToValue ->
                      this.buildTimeBasedValue(fieldToValue, SValue.class, sValueFactory));
          break;
        case ACTIVE_POWER_AND_HEAT_DEMAND:
          timeSeries =
              buildIndividualTimeSeries(
                  metaInformation.getUuid(),
                  csvMetaInformation.getFullFilePath(),
                  fieldToValue ->
                      this.buildTimeBasedValue(
                          fieldToValue, HeatAndPValue.class, heatAndPValueFactory));
          break;
        case APPARENT_POWER_AND_HEAT_DEMAND:
          timeSeries =
              buildIndividualTimeSeries(
                  metaInformation.getUuid(),
                  csvMetaInformation.getFullFilePath(),
                  fieldToValue ->
                      this.buildTimeBasedValue(
                          fieldToValue, HeatAndSValue.class, heatAndSValueFactory));
          break;
        case HEAT_DEMAND:
          timeSeries =
              buildIndividualTimeSeries(
                  metaInformation.getUuid(),
                  csvMetaInformation.getFullFilePath(),
                  fieldToValue ->
                      this.buildTimeBasedValue(
                          fieldToValue, HeatDemandValue.class, heatDemandValueFactory));
          break;
        case ENERGY_PRICE:
          timeSeries =
              buildIndividualTimeSeries(
                  metaInformation.getUuid(),
                  csvMetaInformation.getFullFilePath(),
                  fieldToValue ->
                      this.buildTimeBasedValue(
                          fieldToValue, EnergyPriceValue.class, energyPriceFactory));
          break;
        default:
          timeSeries = null;
      }

      return Optional.ofNullable(timeSeries);
    } catch (SourceException e) {
      logger.error("Error during reading of time series '{}'.", metaInformation.getUuid(), e);
      return Optional.empty();
    }
  }

  /**
   * Attempts to read a time series with given unique identifier and file path. Single entries are
   * obtained entries with the help of {@code fieldToValueFunction}.
   *
   * @param timeSeriesUuid unique identifier of the time series
   * @param filePath path to the file to read
   * @param fieldToValueFunction function, that is able to transfer a mapping (from field to value)
   *     onto a specific instance of the targeted entry class
   * @param <V> Type parameter of the obtained inner values
   * @throws SourceException If the file cannot be read properly
   * @return An option onto an individual time series
   */
  private <V extends Value> IndividualTimeSeries<V> buildIndividualTimeSeries(
      UUID timeSeriesUuid,
      String filePath,
      Function<Map<String, String>, Optional<TimeBasedValue<V>>> fieldToValueFunction)
      throws SourceException {
    try (BufferedReader reader = connector.initReader(filePath)) {
      Set<TimeBasedValue<V>> timeBasedValues =
          filterEmptyOptionals(
                  buildStreamWithFieldsToAttributesMap(TimeBasedValue.class, reader)
                      .map(fieldToValueFunction))
              .collect(Collectors.toSet());

      return new IndividualTimeSeries<>(timeSeriesUuid, timeBasedValues);
    } catch (FileNotFoundException e) {
      throw new SourceException("Unable to find a file with path '" + filePath + "'.", e);
    } catch (IOException e) {
      throw new SourceException("Error during reading of file'" + filePath + "'.", e);
    }
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
