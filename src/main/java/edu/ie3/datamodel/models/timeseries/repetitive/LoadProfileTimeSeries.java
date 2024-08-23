/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.value.PValue;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Describes a load profile time series with repetitive values that can be calculated from a pattern
 */
public abstract class LoadProfileTimeSeries<E extends LoadProfileEntry>
    extends RepetitiveTimeSeries<E, PValue> {
  private final LoadProfile loadProfile;
  private final Map<LoadProfileKey, Map<Integer, PValue>> valueMapping;

  public LoadProfileTimeSeries(
      UUID uuid, Set<E> entries, LoadProfile loadProfile, Function<E, LoadProfileKey> extractor) {
    super(uuid, entries);
    this.loadProfile = loadProfile;

    this.valueMapping =
        entries.stream()
            .collect(
                Collectors.groupingBy(
                    extractor,
                    Collectors.toMap(
                        LoadProfileEntry::getQuarterHourOfDay, LoadProfileEntry::getValue)));
  }

  /** Returns the {@link LoadProfile}. */
  public LoadProfile getLoadProfile() {
    return loadProfile;
  }

  /** Returns the value mapping. */
  protected Map<LoadProfileKey, Map<Integer, PValue>> getValueMapping() {
    return valueMapping;
  }

  @Override
  protected PValue calc(ZonedDateTime time) {
    LoadProfileKey key = fromTime(time);
    int quarterHour = time.getHour() * 4 + time.getMinute() / 15;

    return valueMapping.get(key).get(quarterHour);
  }

  protected abstract LoadProfileKey fromTime(ZonedDateTime time);

  public interface LoadProfileKey {}

  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LoadProfileTimeSeries<E> that = (LoadProfileTimeSeries<E>) o;
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
