/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.load.LoadValues;
import java.util.Objects;

/** Unique entry to a {@link LoadProfileTimeSeries} */
public class LoadProfileEntry<L extends LoadValues> extends TimeSeriesEntry<L> {
  private final int quarterHour;

  public LoadProfileEntry(L values, int quarterHour) {
    super(values);
    this.quarterHour = quarterHour;
  }

  public int getQuarterHour() {
    return quarterHour;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LoadProfileEntry<?> that = (LoadProfileEntry<?>) o;
    return quarterHour == that.quarterHour;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), quarterHour);
  }

  @Override
  public String toString() {
    return "LoadProfileEntry{" + "quarterHour=" + quarterHour + ", value=" + value + '}';
  }
}
