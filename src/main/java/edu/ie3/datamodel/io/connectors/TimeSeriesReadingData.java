/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme;
import java.io.BufferedReader;
import java.util.Objects;
import java.util.UUID;

/** Class to bundle all information, that are necessary to read a single time series */
public class TimeSeriesReadingData {
  private final UUID uuid;
  private final ColumnScheme columnScheme;
  private final BufferedReader reader;

  public TimeSeriesReadingData(UUID uuid, ColumnScheme columnScheme, BufferedReader reader) {
    this.uuid = uuid;
    this.columnScheme = columnScheme;
    this.reader = reader;
  }

  public UUID getUuid() {
    return uuid;
  }

  public ColumnScheme getColumnScheme() {
    return columnScheme;
  }

  public BufferedReader getReader() {
    return reader;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TimeSeriesReadingData)) return false;
    TimeSeriesReadingData that = (TimeSeriesReadingData) o;
    return uuid.equals(that.uuid)
        && columnScheme == that.columnScheme
        && reader.equals(that.reader);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, columnScheme, reader);
  }

  @Override
  public String toString() {
    return "TimeSeriesReadingData{"
        + "uuid="
        + uuid
        + ", columnScheme="
        + columnScheme
        + ", reader="
        + reader
        + '}';
  }
}
