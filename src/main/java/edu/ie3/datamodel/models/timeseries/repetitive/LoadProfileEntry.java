/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.PValue;
import java.time.DayOfWeek;
import java.util.Objects;

/** Unique entry to a {@link LoadProfileInput} */
public class LoadProfileEntry extends TimeSeriesEntry<PValue> {
  private final DayOfWeek dayOfWeek;
  private final int quarterHourOfDay;

  public LoadProfileEntry(PValue value, DayOfWeek dayOfWeek, int quarterHourOfDay) {
    super(value);
    this.dayOfWeek = dayOfWeek;
    this.quarterHourOfDay = quarterHourOfDay;
  }

  public DayOfWeek getDayOfWeek() {
    return dayOfWeek;
  }

  public int getQuarterHourOfDay() {
    return quarterHourOfDay;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LoadProfileEntry that = (LoadProfileEntry) o;
    return quarterHourOfDay == that.quarterHourOfDay && dayOfWeek == that.dayOfWeek;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), dayOfWeek, quarterHourOfDay);
  }

  @Override
  public String toString() {
    return "LoadProfileEntry{"
        + "dayOfWeek="
        + dayOfWeek
        + ", quarterHourOfDay="
        + quarterHourOfDay
        + ", value="
        + value
        + '}';
  }
}
