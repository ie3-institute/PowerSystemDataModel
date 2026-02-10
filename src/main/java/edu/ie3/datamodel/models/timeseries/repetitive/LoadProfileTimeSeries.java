/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import edu.ie3.datamodel.models.profile.PowerProfileKey;
import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.load.LoadValues;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Describes a load profile time series with repetitive values that can be calculated from a pattern
 */
public class LoadProfileTimeSeries<V extends LoadValues>
    extends RepetitiveTimeSeries<LoadProfileEntry<V>, V, PValue> {
  protected final PowerProfileKey powerProfileKey;
  protected final Map<Integer, V> valueMapping;

  /**
   * The maximum average power consumption per quarter-hour calculated over all seasons and weekday
   * types of given load profile.
   */
  private final ComparableQuantity<Power> maxPower;

  /** The profile energy scaling in kWh. */
  private final ComparableQuantity<Energy> profileEnergyScaling;

  public LoadProfileTimeSeries(
      PowerProfileKey powerProfileKey,
      Set<LoadProfileEntry<V>> entries,
      ComparableQuantity<Power> maxPower,
      ComparableQuantity<Energy> profileEnergyScaling) {
    super(entries);
    this.powerProfileKey = powerProfileKey;
    this.valueMapping =
        entries.stream()
            .collect(
                Collectors.toMap(LoadProfileEntry::getQuarterHour, LoadProfileEntry::getValue));

    this.maxPower = maxPower;
    this.profileEnergyScaling = profileEnergyScaling;
  }

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

  /** Returns the {@link PowerProfileKey}. */
  public PowerProfileKey getPowerProfileKey() {
    return powerProfileKey;
  }

  @Override
  public Set<LoadProfileEntry<V>> getEntries() {
    // to ensure that the entries are ordered by their quarter-hour
    TreeSet<LoadProfileEntry<V>> set =
        new TreeSet<>(Comparator.comparing(LoadProfileEntry::getQuarterHour));
    set.addAll(super.getEntries());
    return set;
  }

  /**
   * Method to get a supplier for the next power value based on the provided input time. Depending
   * on the implementation the supplier will either always return the same value or each time a
   * random value. To return one constant value please use {@link #getValue(ZonedDateTime)}.
   *
   * @param time Queried time.
   * @return A supplier for an option on the value at the given time step.
   */
  public Supplier<Optional<PValue>> supplyValue(ZonedDateTime time) {
    int quarterHour = TimeSeriesUtils.calculateQuarterHourOfDay(time);
    LoadValues loadValue = valueMapping.get(quarterHour);
    return () -> Optional.ofNullable(loadValue.getValue(time, powerProfileKey));
  }

  @Override
  public Optional<ZonedDateTime> getPreviousDateTime(ZonedDateTime time) {
    return Optional.of(time.minusMinutes(15));
  }

  @Override
  public Optional<ZonedDateTime> getNextDateTime(ZonedDateTime time) {
    return Optional.of(time.plusMinutes(15));
  }

  /** Returns the value mapping. */
  protected Map<Integer, V> getValueMapping() {
    return valueMapping;
  }

  @Override
  protected PValue calc(ZonedDateTime time) {
    int quarterHour = TimeSeriesUtils.calculateQuarterHourOfDay(time);
    return valueMapping.get(quarterHour).getValue(time, powerProfileKey);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LoadProfileTimeSeries<?> that = (LoadProfileTimeSeries<?>) o;
    return powerProfileKey.equals(that.powerProfileKey) && valueMapping.equals(that.valueMapping);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String toString() {
    return "LoadProfileTimeSeries{"
        + "loadProfile="
        + getPowerProfileKey().getValue()
        + ", valueMapping="
        + getValueMapping()
        + '}';
  }
}
