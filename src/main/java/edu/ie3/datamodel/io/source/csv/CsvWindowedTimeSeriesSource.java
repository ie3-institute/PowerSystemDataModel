/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.TimeSeriesSource;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.util.TimeUtil;
import edu.ie3.util.interval.ClosedInterval;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This csv source for {@link IndividualTimeSeries} utilizes the functionalities of a buffered file
 * reader. That means, that you can only read in your time series linearly from the beginning to the
 * end. As soon, as you have queried an instance in time, you are only able to query future
 * instances! <strong>Moreover, you have to remember to close this source or use it within a
 * try-with-resources!</strong>
 *
 * @param <V> Type of value to be returned
 */
public class CsvWindowedTimeSeriesSource<V extends Value> extends CsvDataSource
    implements TimeSeriesSource<V>, AutoCloseable {
  private static final Logger logger = LoggerFactory.getLogger(CsvWindowedTimeSeriesSource.class);

  private final Duration maximumForeSight;
  private ClosedInterval<ZonedDateTime> coveredInterval;

  private final String filePath;
  private final BufferedReader reader;
  private final Stream<TimeBasedValue<V>> inputStream;

  private final Map<ZonedDateTime, V> buffer = new HashMap<>();

  public CsvWindowedTimeSeriesSource(
      String csvSep,
      String folderPath,
      String filePath,
      FileNamingStrategy fileNamingStrategy,
      Duration maximumForeSight,
      Class<V> valueClass,
      TimeBasedSimpleValueFactory<V> factory) {
    super(csvSep, folderPath, fileNamingStrategy);
    this.maximumForeSight = maximumForeSight;
    this.filePath = filePath;
    try {
      this.reader = super.connector.initReader(filePath);
      this.inputStream =
          filterEmptyOptionals(
              buildStreamWithFieldsToAttributesMap(TimeBasedValue.class, reader)
                  .map(
                      fieldToValue -> this.buildTimeBasedValue(fieldToValue, valueClass, factory)));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(
          "Opening the reader for time series file '" + filePath + "' failed.", e);
    }
  }

  @Override
  public IndividualTimeSeries<V> getTimeSeries() {
    throw new UnsupportedOperationException(
        "This time series source is not able to return a full time series.");
  }

  @Override
  public IndividualTimeSeries<V> getTimeSeries(ClosedInterval<ZonedDateTime> timeInterval) {
    if (!Objects.isNull(coveredInterval)
        && timeInterval.getLower().isBefore(coveredInterval.getLower()))
      throw new RuntimeException(
          "The buffer window already passed the start  '"
              + timeInterval.getLower()
              + "' of your desired time frame.");

    if (Objects.isNull(coveredInterval)
        || timeInterval.getUpper().isAfter(coveredInterval.getUpper())) {
      /* If the buffer is empty, or you can foresee, that the questioned instance isn't within the buffer, fill it up */
      fillUpBuffer(timeInterval.getUpper());
    }

    Set<TimeBasedValue<V>> tbvs =
        getFromBuffer(timeInterval.getLower(), timeInterval.getUpper())
            .map(entry -> new TimeBasedValue<>(entry.getKey(), entry.getValue()))
            .collect(Collectors.toSet());

    /* Reduce the buffer */
    shrinkBuffer(timeInterval.getUpper());

    return new IndividualTimeSeries<>(UUID.randomUUID(), tbvs);
  }

  @Override
  public Optional<V> getValue(ZonedDateTime time) {
    if (!Objects.isNull(coveredInterval) && time.isBefore(coveredInterval.getLower()))
      throw new RuntimeException(
          "The buffer window already passed your desired time instance '" + time + "'.");

    if (Objects.isNull(coveredInterval) || time.isAfter(coveredInterval.getUpper())) {
      /* If the buffer is empty, or you can foresee, that the questioned instance isn't within the buffer, fill it up */
      fillUpBuffer(time);
    }

    Optional<V> value = getFromBuffer(time);

    /* Reduce the buffer */
    shrinkBuffer(time);

    return value;
  }

  /**
   * Get the value for the questioned time from the buffer
   *
   * @param time Questioned time
   * @return Optional value, that might be within the buffer
   */
  private Optional<V> getFromBuffer(ZonedDateTime time) {
    return buffer.entrySet().stream()
        .filter(
            timeToValue ->
                timeToValue.getKey().isBefore(time) || timeToValue.getKey().isEqual(time))
        .max(Map.Entry.comparingByKey())
        .map(Map.Entry::getValue);
  }

  /**
   * Get a defined time slice from buffer
   *
   * @param start Start of the slice (included)
   * @param end End of the slice (included)
   * @return A stream for that slice
   */
  private Stream<Map.Entry<ZonedDateTime, V>> getFromBuffer(
      ZonedDateTime start, ZonedDateTime end) {
    return buffer.entrySet().stream()
        .filter(
            timeToValue ->
                (timeToValue.getKey().isAfter(start) || timeToValue.getKey().isEqual(start))
                    && (timeToValue.getKey().isBefore(end) || timeToValue.getKey().isEqual(end)));
  }

  /**
   * Fills up the buffer. This is done by adding the {@link this#maximumForeSight} to the time of
   * interest. This marks the newest possible instance in the buffer to appear.
   *
   * @param timeOfInterest The time of interest
   */
  private void fillUpBuffer(ZonedDateTime timeOfInterest) {
    ZonedDateTime intendedNewestTimeInBuffer = timeOfInterest.plus(maximumForeSight);
    inputStream
        .filter(
            tbv ->
                tbv.getTime().isBefore(intendedNewestTimeInBuffer)
                    || tbv.getTime().isEqual(intendedNewestTimeInBuffer))
        .forEach(tbv -> buffer.put(tbv.getTime(), tbv.getValue()));
    updateNewestTime(intendedNewestTimeInBuffer);
  }

  /**
   * Removes all entries in the buffer, that are older than the provided instance in time.
   *
   * @param oldestEntry The oldest entry meant to remain within the buffer
   */
  private void shrinkBuffer(ZonedDateTime oldestEntry) {
    List<ZonedDateTime> timesToRemove =
        buffer.keySet().stream()
            .filter(bufferTime -> bufferTime.isBefore(oldestEntry))
            .collect(Collectors.toList());
    timesToRemove.forEach(buffer::remove);
    updateOldestTime(oldestEntry);
  }

  /**
   * Expand the covered time frame
   *
   * @param time Newest covered time
   */
  private void updateNewestTime(ZonedDateTime time) {
    if (Objects.isNull(coveredInterval)) {
      coveredInterval = new ClosedInterval<>(time, time);
    } else {
      coveredInterval = new ClosedInterval<>(coveredInterval.getLower(), time);
    }
  }

  /**
   * Reduce the covered time frame
   *
   * @param time Oldest covered time
   */
  private void updateOldestTime(ZonedDateTime time) {
    if (Objects.isNull(coveredInterval)) {
      coveredInterval = new ClosedInterval<>(time, time);
    } else {
      coveredInterval = new ClosedInterval<>(time, coveredInterval.getUpper());
    }
  }

  /**
   * Determines all available time steps within the given time series
   *
   * @return A list of available {@link ZonedDateTime}s
   */
  public List<ZonedDateTime> getAvailableTimeSteps() {
    return getAvailableTimeSteps("time", "yyyy-MM-dd'T'HH:mm:ss[.S[S][S]]'Z'");
  }

  /**
   * Determines all available time steps within the given time series
   *
   * @param timeField Field, where date time information is located
   * @param timePattern Pattern of the date time strings
   * @return A list of available {@link ZonedDateTime}s
   */
  public List<ZonedDateTime> getAvailableTimeSteps(String timeField, String timePattern) {
    TimeUtil timeUtil = new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, timePattern);
    try (BufferedReader reader = super.connector.initReader(this.filePath)) {
      return buildStreamWithFieldsToAttributesMap(TimeBasedValue.class, reader)
          .map(
              fieldToValue ->
                  Optional.ofNullable(fieldToValue.get(timeField)).map(timeUtil::toZonedDateTime))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toList());
    } catch (IOException e) {
      logger.warn(
          "Opening a reader for time series file '"
              + filePath
              + "' failed. Unable to determine available time steps.",
          e);
    }
    return Collections.emptyList();
  }

  @Override
  public void close() throws Exception {
    inputStream.close();
    reader.close();
  }
}
