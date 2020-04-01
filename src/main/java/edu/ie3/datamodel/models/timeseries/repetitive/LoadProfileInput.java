/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import static java.time.temporal.ChronoUnit.HOURS;

import edu.ie3.datamodel.models.StandardLoadProfile;
import edu.ie3.datamodel.models.value.PValue;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

// TODO This is a sample implementation, please implement a real scenario
public class LoadProfileInput extends RepetitiveTimeSeries<LoadProfileEntry, PValue> {
  private final StandardLoadProfile type;
  private final Map<DayOfWeek, Map<Integer, PValue>> dayOfWeekToHourlyValues;

  public LoadProfileInput(UUID uuid, StandardLoadProfile type, Set<LoadProfileEntry> values) {
    super(uuid, values);
    this.type = type;
    this.dayOfWeekToHourlyValues =
        entries.stream()
            .collect(
                Collectors.groupingBy(
                    LoadProfileEntry::getDayOfWeek,
                    Collectors.toMap(
                        LoadProfileEntry::getQuarterHourOfDay, LoadProfileEntry::getValue)));
  }

  public LoadProfileInput(StandardLoadProfile type, Set<LoadProfileEntry> values) {
    this(UUID.randomUUID(), type, values);
  }

  @Override
  public PValue calc(ZonedDateTime time) {
    return dayOfWeekToHourlyValues.get(time.getDayOfWeek()).get(time.getHour());
  }

  @Override
  protected Optional<ZonedDateTime> getPreviousDateTime(ZonedDateTime time) {
    return Optional.of(time.minus(1, HOURS));
  }

  @Override
  protected Optional<ZonedDateTime> getNextDateTime(ZonedDateTime time) {
    return Optional.of(time.plus(1, HOURS));
  }

  public StandardLoadProfile getType() {
    return type;
  }

  @Override
  public Set<LoadProfileEntry> getEntries() {
    return entries;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LoadProfileInput that = (LoadProfileInput) o;
    return type.equals(that.type) &&
            dayOfWeekToHourlyValues.equals(that.dayOfWeekToHourlyValues);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, dayOfWeekToHourlyValues);
  }

  @Override
  public String toString() {
    return "LoadProfileInput{"
        + "type="
        + type
        + ", dayOfWeekToHourlyValues="
        + dayOfWeekToHourlyValues
        + '}';
  }
}
