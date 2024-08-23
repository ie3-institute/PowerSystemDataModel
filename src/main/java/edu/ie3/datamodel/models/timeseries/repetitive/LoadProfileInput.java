/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import edu.ie3.datamodel.models.profile.StandardLoadProfile;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

// TODO This is a sample implementation, please implement a real scenario
@Deprecated
public class LoadProfileInput extends LoadProfileTimeSeries<LoadProfileEntry> {
  public LoadProfileInput(UUID uuid, StandardLoadProfile type, Set<LoadProfileEntry> values) {
    super(uuid, values, type, e -> new Key(e.getDayOfWeek()));
  }

  public LoadProfileInput(StandardLoadProfile type, Set<LoadProfileEntry> values) {
    this(UUID.randomUUID(), type, values);
  }

  @Override
  protected LoadProfileKey fromTime(ZonedDateTime time) {
    return new Key(time.getDayOfWeek());
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
    return "LoadProfileInput{"
        + "uuid="
        + getUuid()
        + "type="
        + getLoadProfile()
        + ", dayOfWeekToHourlyValues="
        + getValueMapping()
        + '}';
  }

  private record Key(DayOfWeek dayOfWeek) implements LoadProfileKey {}
}
