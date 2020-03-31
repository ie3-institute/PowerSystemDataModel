/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.deserialize;

import edu.ie3.datamodel.exceptions.DeserializationException;
import edu.ie3.datamodel.io.CsvFileDefinition;
import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.processor.input.TimeBasedValueProcessor;
import edu.ie3.datamodel.models.timeseries.IndividualTimeSeries;
import edu.ie3.datamodel.models.value.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IndividualTimeSeriesDeserializer<V extends Value>
    extends TimeSeriesDeserializer<IndividualTimeSeries<V>, V> {
  private final TimeBasedValueProcessor timeBasedValueProcessor = new TimeBasedValueProcessor();
  private final String[] headLineElements;

  public IndividualTimeSeriesDeserializer(Class<? extends V> valueClass, String baseFolderPath) {
    super(valueClass, baseFolderPath);
    this.headLineElements = determineHeadLineElements();
  }

  @Override
  protected CsvFileDefinition determineFileDefinition(UUID uuid) throws DeserializationException {
    FileNamingStrategy fileNamingStrategy = new FileNamingStrategy();
    String fileName =
        fileNamingStrategy
            .getIndividualTimeSeriesFileName(uuid)
            .orElseThrow(
                () ->
                    new DeserializationException(
                        "Cannot determine file name for individual time series with uuid=" + uuid));
    return new CsvFileDefinition(fileName, headLineElements);
  }

  @Override
  protected String[] determineHeadLineElements() {
    return Stream.of(
            timeBasedValueProcessor.getHeaderElements(), valueProcessor.getHeaderElements())
        .flatMap(Arrays::stream)
        .toArray(String[]::new);
  }

  @Override
  protected void deserialize(IndividualTimeSeries<V> timeSeries) throws DeserializationException {
    /* Get all entries */
    SortedSet<TimeBasedValue<V>> entries = timeSeries.getAllEntries();

    Set<LinkedHashMap<String, String>> result =
        Collections.unmodifiableSet(
            entries.stream().map(this::handleTimeBasedValue).collect(Collectors.toSet()));

    /* Prepare and do writing */
    CsvFileDefinition destination = determineFileDefinition(timeSeries.getUuid());
    csvFileSink.persistAll(destination, result);
  }

  /**
   * Disassemble the time based value to container and actual value and process it in the equivalent
   * processors
   *
   * @param timeBasedValue The time based value to handle
   * @return A mapping from field name to value as String representation
   */
  protected LinkedHashMap<String, String> handleTimeBasedValue(TimeBasedValue<V> timeBasedValue) {
    /* Build the mapping from field name to value for the containing class */
    Optional<LinkedHashMap<String, String>> outerResult =
        timeBasedValueProcessor.handleEntity(timeBasedValue);
    if (!outerResult.isPresent()) {
      logger.error("Cannot deserialize a time based value \"{}\".", timeBasedValue);
      return new LinkedHashMap<>();
    }

    Optional<LinkedHashMap<String, String>> innerResult =
        valueProcessor.handleEntity(timeBasedValue.getValue());
    if (!innerResult.isPresent()) {
      logger.error("Cannot deserialize a time value \"{}\".", timeBasedValue.getValue());
      return new LinkedHashMap<>();
    }

    LinkedHashMap<String, String> interMediateResult = outerResult.get();
    interMediateResult.putAll(innerResult.get());
    return interMediateResult;
  }
}
