/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import edu.ie3.datamodel.models.profile.LoadProfileKey;
import edu.ie3.datamodel.models.value.PValue;
import java.time.DayOfWeek;
import java.util.Objects;

/** Unique entry to a {@link BDEWLoadProfileInput} */
public class BDEWLoadProfileEntry extends LoadProfileEntry {
  private final LoadProfileKey.Season season;

  public BDEWLoadProfileEntry(
      PValue value, LoadProfileKey.Season season, DayOfWeek dayOfWeek, int quarterHourOfDay) {
    super(value, dayOfWeek, quarterHourOfDay);
    this.season = season;
  }

  public LoadProfileKey.Season getSeason() {
    return season;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    BDEWLoadProfileEntry that = (BDEWLoadProfileEntry) o;
    return getQuarterHourOfDay() == that.getQuarterHourOfDay()
        && season == that.season
        && getDayOfWeek() == that.getDayOfWeek();
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), season);
  }

  @Override
  public String toString() {
    return "BDEWLoadProfileEntry{"
        + "dayOfWeek="
        + getDayOfWeek()
        + "season="
        + season
        + ", quarterHourOfDay="
        + getQuarterHourOfDay()
        + ", value="
        + value
        + '}';
  }
}
