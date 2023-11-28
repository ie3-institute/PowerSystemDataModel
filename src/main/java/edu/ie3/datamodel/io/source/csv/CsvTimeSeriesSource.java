/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.FailureException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.csv.CsvIndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.factory.timeseries.*;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.TimeSeriesSource;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.*;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.util.TimeUtil;
import edu.ie3.util.interval.ClosedInterval;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Time;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/** Source that is capable of providing information around time series from csv files. */
public class CsvTimeSeriesSource<V extends Value> extends TimeSeriesSource<V> {
  private final IndividualTimeSeries<V> timeSeries;
  private final CsvDataSource dataSource;

  /**
   * Factory method to build a source from given meta information
   *
   * @param csvSep the separator string for csv columns
   * @param folderPath path to the folder holding the time series files
   * @param fileNamingStrategy strategy for the file naming of time series files / data sinks
   * @param metaInformation The given meta information
   * @throws SourceException If the given meta information are not supported
   * @return The source
   */
  public static CsvTimeSeriesSource<? extends Value> getSource(
          String csvSep,
          String folderPath,
          FileNamingStrategy fileNamingStrategy,
          CsvIndividualTimeSeriesMetaInformation metaInformation) throws SourceException {
          return getSource(csvSep, folderPath, fileNamingStrategy, metaInformation, TimeUtil.withDefaults.getDtfPattern());
  }

  /**
   * Factory method to build a source from given meta information
   *
   * @param csvSep the separator string for csv columns
   * @param folderPath path to the folder holding the time series files
   * @param fileNamingStrategy strategy for the file naming of time series files / data sinks
   * @param metaInformation The given meta information
   * @param dtfPattern The date time format pattern for the time based value factory
   * @throws SourceException If the given meta information are not supported
   * @return The source
   */
  public static CsvTimeSeriesSource<? extends Value> getSource(
      String csvSep,
      Path folderPath,
      FileNamingStrategy fileNamingStrategy,
      CsvIndividualTimeSeriesMetaInformation metaInformation,
      String dtfPattern)
      throws SourceException {
    if (!TimeSeriesUtils.isSchemeAccepted(metaInformation.getColumnScheme()))
      throw new SourceException(
          "Unsupported column scheme '" + metaInformation.getColumnScheme() + "'.");

    Class<? extends Value> valClass = metaInformation.getColumnScheme().getValueClass();

    return create(csvSep, folderPath, fileNamingStrategy, metaInformation, valClass, dtfPattern);
  }

  private static <T extends Value> CsvTimeSeriesSource<T> create(
      String csvSep,
      Path folderPath,
      FileNamingStrategy fileNamingStrategy,
      CsvIndividualTimeSeriesMetaInformation metaInformation,
      Class<T> valClass,
      String dtfPattern) {
    TimeBasedSimpleValueFactory<T> valueFactory = new TimeBasedSimpleValueFactory<>(valClass, dtfPattern);
    return new CsvTimeSeriesSource<>(
        csvSep,
        folderPath,
        fileNamingStrategy,
        metaInformation.getUuid(),
        metaInformation.getFullFilePath(),
        valClass,
        valueFactory);
  }

  /**
   * Initializes a new CsvTimeSeriesSource
   *
   * @param csvSep the separator string for csv columns
   * @param folderPath path to the folder holding the time series files
   * @param fileNamingStrategy strategy for the file naming of time series files / data sinks
   * @param timeSeriesUuid Unique identifier of the time series
   * @param filePath Path of the file, excluding extension and being relative to {@code folderPath}
   * @param valueClass Class of the value
   * @param factory The factory implementation to use for actual parsing of input data
   */
  public CsvTimeSeriesSource(
      String csvSep,
      Path folderPath,
      FileNamingStrategy fileNamingStrategy,
      UUID timeSeriesUuid,
      Path filePath,
      Class<V> valueClass,
      TimeBasedSimpleValueFactory<V> factory) {
    super(valueClass, factory);
    this.dataSource = new CsvDataSource(csvSep, folderPath, fileNamingStrategy);

    /* Read in the full time series */
    try {
      this.timeSeries =
          buildIndividualTimeSeries(timeSeriesUuid, filePath, this::createTimeBasedValue);
    } catch (SourceException e) {
      throw new IllegalArgumentException(
          "Unable to obtain time series with UUID '"
              + timeSeriesUuid
              + "'. Please check arguments!",
          e);
    }
  }

  @Override
  public IndividualTimeSeries<V> getTimeSeries() {
    return timeSeries;
  }

  @Override
  public IndividualTimeSeries<V> getTimeSeries(ClosedInterval<ZonedDateTime> timeInterval) {
    return TimeSeriesUtils.trimTimeSeriesToInterval(timeSeries, timeInterval);
  }

  @Override
  public Optional<V> getValue(ZonedDateTime time) {
    return timeSeries.getValue(time);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
   * Attempts to read a time series with given unique identifier and file path. Single entries are
   * obtained entries with the help of {@code fieldToValueFunction}.
   *
   * @param timeSeriesUuid unique identifier of the time series
   * @param filePath path to the file to read
   * @param fieldToValueFunction function, that is able to transfer a mapping (from field to value)
   *     onto a specific instance of the targeted entry class
   * @throws SourceException If the file cannot be read properly
   * @return an individual time series
   */
  protected IndividualTimeSeries<V> buildIndividualTimeSeries(
      UUID timeSeriesUuid,
      Path filePath,
      Function<Map<String, String>, Try<TimeBasedValue<V>, FactoryException>> fieldToValueFunction)
      throws SourceException {
    try (BufferedReader reader = dataSource.connector.initReader(filePath)) {
      Try<Stream<TimeBasedValue<V>>, FailureException> timeBasedValues =
          Try.scanStream(
              dataSource
                  .buildStreamWithFieldsToAttributesMap(TimeBasedValue.class, reader)
                  .map(fieldToValueFunction),
              "TimeBasedValue<V>");
      return new IndividualTimeSeries<>(
          timeSeriesUuid, new HashSet<>(timeBasedValues.getOrThrow().toList()));
    } catch (FileNotFoundException e) {
      throw new SourceException("Unable to find a file with path '" + filePath + "'.", e);
    } catch (IOException e) {
      throw new SourceException("Error during reading of file'" + filePath + "'.", e);
    } catch (FailureException e) {
      throw new SourceException("Unable to build individual time series. ", e.getCause());
    }
  }
}
