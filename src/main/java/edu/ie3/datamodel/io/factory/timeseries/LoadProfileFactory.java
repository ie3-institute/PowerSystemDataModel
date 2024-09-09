/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.Factory;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileTimeSeriesMetaInformation;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.load.LoadValues;
import java.util.Optional;
import java.util.Set;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

public abstract class LoadProfileFactory<P extends LoadProfile, V extends LoadValues>
    extends Factory<V, LoadProfileData<V>, LoadProfileEntry<V>> {
  protected static final String QUARTER_HOUR = "quarterHour";

  public LoadProfileFactory(Class<? extends V> valueClass) {
    super(valueClass);
  }

  public abstract LoadProfileTimeSeries<V> build(
      LoadProfileTimeSeriesMetaInformation metaInformation, Set<LoadProfileEntry<V>> entries);

  public abstract P parseProfile(String profile);

  /**
   * Calculates the maximum average power consumption per quarter-hour for a given calculated over
   * all seasons and weekday types of given load profile
   *
   * @param loadProfile given load profile
   * @param entries with power values
   * @return an option for the maximal power
   */
  public abstract Optional<ComparableQuantity<Power>> calculateMaxPower(
      P loadProfile, Set<LoadProfileEntry<V>> entries);

  /** Returns the quarter-hour field. */
  public String getTimeFieldString() {
    return QUARTER_HOUR;
  }
}
