/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.Factory;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.load.LoadValues;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.time.ZonedDateTime;
import java.util.Set;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

/**
 * Base factory for all {@link LoadProfileTimeSeries}.
 *
 * @param <P> type of load profile
 * @param <V> type of load values
 */
public abstract class LoadProfileFactory<P extends LoadProfile, V extends LoadValues>
    extends Factory<V, LoadProfileData<V>, LoadProfileEntry<V>> {
  protected static final String QUARTER_HOUR = "quarterHour";

  public LoadProfileFactory(Class<? extends V> valueClass) {
    super(valueClass);
  }

  public abstract LoadProfileTimeSeries<V> build(
      LoadProfileMetaInformation metaInformation, Set<LoadProfileEntry<V>> entries);

  public abstract P parseProfile(String profile);

  /**
   * Method to build a {@link LoadValues.Provider}.
   *
   * @param loadValue used for the provider
   * @param time used for the provider
   * @param loadProfile used for the provider
   * @return a value provider
   */
  public abstract LoadValues.Provider buildProvider(V loadValue, ZonedDateTime time, P loadProfile);

  /**
   * Calculates the maximum average power consumption per quarter-hour for a given calculated over
   * all seasons and weekday types of given load profile
   *
   * @param loadProfile given load profile
   * @param entries with power values
   * @return the maximal average power
   */
  public abstract ComparableQuantity<Power> calculateMaxPower(
      P loadProfile, Set<LoadProfileEntry<V>> entries);

  /** Returns the quarter-hour field. */
  public String getTimeFieldString() {
    return QUARTER_HOUR;
  }

  /** Returns the load profile energy scaling. The default value is 1000 kWh */
  public ComparableQuantity<Energy> getLoadProfileEnergyScaling(P loadProfile) {
    return Quantities.getQuantity(1000d, PowerSystemUnits.KILOWATTHOUR);
  }
}
