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
  /** The constant QUARTER_HOUR. */
  protected static final String QUARTER_HOUR = "quarterHour";

  /**
   * Instantiates a new Load profile factory.
   *
   * @param valueClass the value class
   */
  public LoadProfileFactory(Class<? extends V> valueClass) {
    super(valueClass);
  }

  /**
   * Instantiates a new Load profile factory.
   *
   * @param valueClass the value class
   */
  @SafeVarargs
  protected LoadProfileFactory(Class<? extends V>... valueClass) {
    super(valueClass);
  }

  /**
   * Build load profile time series.
   *
   * @param metaInformation the meta information
   * @param entries the entries
   * @return the load profile time series
   */
  public abstract LoadProfileTimeSeries<V> build(
      LoadProfileMetaInformation metaInformation, Set<LoadProfileEntry<V>> entries);

  /**
   * Parse profile p.
   *
   * @param profile the profile
   * @return the p
   */
  public abstract P parseProfile(String profile);

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

  /**
   * Returns the quarter-hour field.
   *
   * @return the time field string
   */
  public String getTimeFieldString() {
    return QUARTER_HOUR;
  }

  /**
   * Returns the load profile energy scaling. The default value is 1000 kWh @param loadProfile the
   * load profile
   *
   * @return the load profile energy scaling
   */
  public ComparableQuantity<Energy> getLoadProfileEnergyScaling(P loadProfile) {
    return Quantities.getQuantity(1000d, PowerSystemUnits.KILOWATTHOUR);
  }
}
