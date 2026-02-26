/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.Factory;
import edu.ie3.datamodel.models.profile.PowerProfileKey;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.load.LoadValues;
import java.util.Set;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Base factory for all {@link LoadProfileTimeSeries}.
 *
 * @param <V> type of load values
 */
public abstract class LoadProfileFactory<V extends LoadValues>
    extends Factory<V, LoadProfileData<V>, LoadProfileEntry<V>> {
  protected static final String QUARTER_HOUR = "quarterHour";

  protected LoadProfileFactory(Class<? extends V> valueClass) {
    super(valueClass);
  }

  @SafeVarargs
  protected LoadProfileFactory(Class<? extends V>... valueClass) {
    super(valueClass);
  }

  public abstract LoadProfileTimeSeries<V> build(
      PowerProfileKey powerProfileKey, Set<LoadProfileEntry<V>> entries);

  /**
   * Calculates the maximum average power consumption per quarter-hour for a given calculated over
   * all seasons and weekday types of given load profile
   *
   * @param powerProfileKey given load profile key
   * @param entries with power values
   * @return the maximal average power
   */
  public abstract ComparableQuantity<Power> calculateMaxPower(
      PowerProfileKey powerProfileKey, Set<LoadProfileEntry<V>> entries);

  /** Returns the quarter-hour field. */
  public String getTimeFieldString() {
    return QUARTER_HOUR;
  }

  /** Returns the load profile energy scaling. */
  public abstract ComparableQuantity<Energy> getLoadProfileEnergyScaling(
      PowerProfileKey powerProfileKey);
}
