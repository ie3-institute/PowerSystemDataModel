/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.value.load.LoadValues;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Describes a load profile time series with repetitive values that can be calculated from a pattern
 *
 * @param <V> type of {@link LoadValues} used in this time series.
 */
public abstract class LoadProfileTimeSeries<V extends LoadValues>
    extends RepetitiveTimeSeries<LoadProfileEntry<V>, V, LoadValues.Provider> {
  protected final LoadProfile loadProfile;
  private final Map<Integer, V> valueMapping;

  /**
   * The maximum average power consumption per quarter-hour calculated over all seasons and weekday
   * types of given load profile.
   */
  private final ComparableQuantity<Power> maxPower;

  /** The profile energy scaling in kWh. */
  private final ComparableQuantity<Energy> profileEnergyScaling;

  protected LoadProfileTimeSeries(
      UUID uuid,
      LoadProfile loadProfile,
      Set<LoadProfileEntry<V>> entries,
      ComparableQuantity<Power> maxPower,
      ComparableQuantity<Energy> profileEnergyScaling) {
    super(uuid, entries);
    this.loadProfile = loadProfile;
    this.valueMapping =
        entries.stream()
            .collect(
                Collectors.toMap(LoadProfileEntry::getQuarterHour, LoadProfileEntry::getValue));

    this.maxPower = maxPower;
    this.profileEnergyScaling = profileEnergyScaling;
  }

  @Override
  protected LoadValues.Provider calc(ZonedDateTime time) {
    int quarterHour = TimeSeriesUtils.calculateQuarterHourOfDay(time);
    return buildFunction(valueMapping.get(quarterHour), time);
  }

  /**
   * Method to build a {@link LoadValues.Provider} for the given {@link LoadValues} and time.
   *
   * @param loadValue used for the provider
   * @param time used for the provider
   * @return a {@link LoadValues.Provider}
   */
  protected abstract LoadValues.Provider buildFunction(V loadValue, ZonedDateTime time);

  /**
   * Returns the maximum average power consumption per quarter-hour calculated over all seasons and
   * weekday types of given load profile in Watt.
   */
  public Optional<ComparableQuantity<Power>> maxPower() {
    return Optional.ofNullable(maxPower);
  }

  /** Returns the profile energy scaling in kWh. */
  public Optional<ComparableQuantity<Energy>> loadProfileScaling() {
    return Optional.ofNullable(profileEnergyScaling);
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

  @Override
  protected Optional<ZonedDateTime> getPreviousDateTime(ZonedDateTime time) {
    return Optional.of(time.minusMinutes(15));
  }

  @Override
  protected Optional<ZonedDateTime> getNextDateTime(ZonedDateTime time) {
    return Optional.of(time.plusMinutes(15));
  }

  @Override
  public List<ZonedDateTime> getTimeKeysAfter(ZonedDateTime time) {
    return List.of(time.plusMinutes(15)); // dummy value that will return next quarter-hour value
  }

  /** Returns the value mapping. */
  protected Map<Integer, V> getValueMapping() {
    return valueMapping;
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
