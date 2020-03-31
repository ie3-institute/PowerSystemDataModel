/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.deserialize;

import edu.ie3.datamodel.exceptions.DeserializationException;
import edu.ie3.datamodel.io.CsvFileDefinition;
import edu.ie3.datamodel.io.processor.input.ValueProcessor;
import edu.ie3.datamodel.io.sink.CsvFileSink;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.value.Value;
import java.util.Collections;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TimeSeriesDeserializer<T extends TimeSeries<V>, V extends Value> {
  protected static final Logger logger = LoggerFactory.getLogger(TimeSeriesDeserializer.class);

  protected final ValueProcessor<V> valueProcessor;

  private static final String CSV_SEP = ",";
  protected final CsvFileSink csvFileSink;

  public TimeSeriesDeserializer(Class<? extends V> valueClass, String baseFolderPath) {
    this.valueProcessor = new ValueProcessor<>(valueClass);
    /* We cannot determine the file definitions on instantiation, as every unique time series gets it's own unique file name */
    this.csvFileSink =
        new CsvFileSink(baseFolderPath, Collections.emptySet(), CSV_SEP, false, true);
  }

  /**
   * Builds a file definition for a unique time series.
   *
   * @return A file definition
   */
  protected abstract CsvFileDefinition determineFileDefinition(UUID uuid)
      throws DeserializationException;

  /**
   * Determine the head line elements / the field names of the model to persist
   *
   * @return An array of Strings denoting the field names
   */
  protected abstract String[] determineHeadLineElements();

  /**
   * Deserializes the given time series
   *
   * @param timeSeries to deserialize
   */
  protected abstract void deserialize(T timeSeries) throws DeserializationException;
}
