/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.load.LoadValues;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Describes a load profile time series with repetitive values that can be calculated from a pattern
 */
public abstract class LoadProfileTimeSeries<V extends LoadValues>
    extends RepetitiveTimeSeries<LoadProfileEntry<V>, PValue> {
  private final LoadProfile loadProfile;
  private final Map<Integer, V> valueMapping;

  public LoadProfileTimeSeries(
      UUID uuid, LoadProfile loadProfile, Set<LoadProfileEntry<V>> entries) {
    super(uuid, entries);
    this.loadProfile = loadProfile;
    this.valueMapping =
        entries.stream()
            .collect(
                Collectors.toMap(LoadProfileEntry::getQuarterHour, LoadProfileEntry::getValue));
  }

  /** Returns the {@link LoadProfile}. */
  public LoadProfile getLoadProfile() {
    return loadProfile;
  }

  @Override
  public Set<LoadProfileEntry<V>> getEntries() {
    // to ensure that the entries are ordered by their quarter-hour
    TreeSet<LoadProfileEntry<V>> set =
        new TreeSet<>(Comparator.comparing(LoadProfileEntry::getQuarterHour));
    set.addAll(super.getEntries());
    return set;
  }

  /** Returns the value mapping. */
  protected Map<Integer, V> getValueMapping() {
    return valueMapping;
  }

  @Override
  protected PValue calc(ZonedDateTime time) {
    int quarterHour = time.getHour() * 4 + time.getMinute() / 15;
    return valueMapping.get(quarterHour).getValue(time, loadProfile);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LoadProfileTimeSeries<?> that = (LoadProfileTimeSeries<?>) o;
    return loadProfile.equals(that.loadProfile) && valueMapping.equals(that.valueMapping);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String toString() {
    return "LoadProfileTimeSeries{"
        + "uuid="
        + getUuid()
        + "loadProfile="
        + getLoadProfile()
        + ", valueMapping="
        + getValueMapping()
        + '}';
  }
}
