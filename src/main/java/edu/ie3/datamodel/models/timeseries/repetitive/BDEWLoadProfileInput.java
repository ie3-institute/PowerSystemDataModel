/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.profile.LoadProfileKey;
import edu.ie3.datamodel.models.value.PValue;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BDEWLoadProfileInput extends RepetitiveTimeSeries<BDEWLoadProfileEntry, PValue> {
  private final BdewStandardLoadProfile loadProfile;
  private final Map<Key, Map<Integer, PValue>> valueMapping;

  public BDEWLoadProfileInput(
      BdewStandardLoadProfile loadProfile, Set<BDEWLoadProfileEntry> values) {
    super(UUID.randomUUID(), values);
    this.loadProfile = loadProfile;

    this.valueMapping =
        values.stream()
            .collect(
                Collectors.groupingBy(
                    e -> new Key(e.getSeason(), e.getDayOfWeek()),
                    Collectors.toMap(
                        LoadProfileEntry::getQuarterHourOfDay, LoadProfileEntry::getValue)));
  }

  @Override
  public PValue calc(ZonedDateTime time) {
    Key key = fromTime(time);
    int quarterHour = time.getHour() * 4 + time.getMinute() / 15;

    return valueMapping.get(key).get(quarterHour);
  }

  public BdewStandardLoadProfile getLoadProfile() {
    return loadProfile;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    BDEWLoadProfileInput that = (BDEWLoadProfileInput) o;
    return loadProfile.equals(that.loadProfile) && valueMapping.equals(that.valueMapping);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), loadProfile, valueMapping);
  }

  @Override
  public String toString() {
    return "BDEWLoadProfileInput{"
        + "loadProfile="
        + loadProfile
        + ", valueMapping="
        + valueMapping
        + '}';
  }

  private Key fromTime(ZonedDateTime time) {
    LoadProfileKey.Season season = LoadProfileKey.Season.get(time);

    DayOfWeek day =
        switch (time.getDayOfWeek()) {
          case SATURDAY -> DayOfWeek.SATURDAY;
          case SUNDAY -> DayOfWeek.SUNDAY;
          default -> DayOfWeek.MONDAY;
        };

    return new Key(season, day);
  }

  private record Key(LoadProfileKey.Season season, DayOfWeek dayOfWeek) {}
}
