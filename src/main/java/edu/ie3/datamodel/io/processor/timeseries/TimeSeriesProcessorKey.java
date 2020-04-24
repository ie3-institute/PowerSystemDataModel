/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor.timeseries;

import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import java.util.Objects;

/**
 * Class to bundle a triple of time series class, entry class and value class for later recognition
 * of supported time series in the {@link TimeSeriesProcessor}
 */
public class TimeSeriesProcessorKey {
  private final Class<? extends TimeSeries> timeSeriesClass;
  private final Class<? extends TimeSeriesEntry> entryClass;
  private final Class<? extends Value> valueClass;

  public TimeSeriesProcessorKey(TimeSeries<? extends TimeSeriesEntry<?>, ?> timeSeries) {
    this.timeSeriesClass = timeSeries.getClass();
    this.entryClass =
        timeSeries.getEntries().stream()
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("Cannot find entries in the time series."))
            .getClass();
    this.valueClass =
        timeSeries.getEntries().stream()
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("Cannot find entries in the time series."))
            .getValue()
            .getClass();
  }

  public TimeSeriesProcessorKey(
      Class<? extends TimeSeries> timeSeriesClass,
      Class<? extends TimeSeriesEntry> entryClass,
      Class<? extends Value> valueClass) {
    this.timeSeriesClass = timeSeriesClass;
    this.entryClass = entryClass;
    this.valueClass = valueClass;
  }

  public Class<? extends TimeSeries> getTimeSeriesClass() {
    return timeSeriesClass;
  }

  public Class<? extends TimeSeriesEntry> getEntryClass() {
    return entryClass;
  }

  public Class<? extends Value> getValueClass() {
    return valueClass;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TimeSeriesProcessorKey that = (TimeSeriesProcessorKey) o;
    return timeSeriesClass.equals(that.timeSeriesClass)
        && entryClass.equals(that.entryClass)
        && valueClass.equals(that.valueClass);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timeSeriesClass, entryClass, valueClass);
  }

  @Override
  public String toString() {
    return "TimeSeriesProcessorKey{"
        + "timeSeriesClass="
        + timeSeriesClass
        + ", entryClass="
        + entryClass
        + ", valueClass="
        + valueClass
        + '}';
  }
}
