/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import edu.ie3.datamodel.models.Season;
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.value.PValue;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Describes a bdew load profile time series with repetitive values that can be calculated from a
 * pattern
 */
public class BDEWLoadProfileTimeSeries extends LoadProfileTimeSeries<BDEWLoadProfileEntry> {

  public BDEWLoadProfileTimeSeries(
      UUID uuid, BdewStandardLoadProfile loadProfile, Set<BDEWLoadProfileEntry> values) {
    super(uuid, values, loadProfile, e -> new BdewKey(e.getSeason(), e.getDayOfWeek()));
  }

  @Override
  public PValue calc(ZonedDateTime time) {
    if (getLoadProfile() == BdewStandardLoadProfile.H0) {
      PValue value = super.calc(time);

      // TODO: Add factor calculation

      return value;
    } else {
      return super.calc(time);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String toString() {
    return "BDEWLoadProfileInput{"
        + "uuid="
        + getUuid()
        + "loadProfile="
        + getLoadProfile()
        + ", valueMapping="
        + getValueMapping()
        + '}';
  }

  @Override
  protected LoadProfileKey fromTime(ZonedDateTime time) {
    Season season = Season.get(time);

    DayOfWeek day =
        switch (time.getDayOfWeek()) {
          case SATURDAY -> DayOfWeek.SATURDAY;
          case SUNDAY -> DayOfWeek.SUNDAY;
          default -> DayOfWeek.MONDAY;
        };

    return new BdewKey(season, day);
  }

  private record BdewKey(Season season, DayOfWeek dayOfWeek) implements LoadProfileKey {}
}
