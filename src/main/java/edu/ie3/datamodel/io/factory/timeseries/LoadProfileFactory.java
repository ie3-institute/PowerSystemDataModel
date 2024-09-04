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
import java.util.Set;

public abstract class LoadProfileFactory<P extends LoadProfile, V extends LoadValues>
    extends Factory<V, LoadProfileData<V>, LoadProfileEntry<V>> {
  public static final String QUARTER_HOUR = "quarterHour";

  public LoadProfileFactory(Class<? extends V> valueClass) {
    super(valueClass);
  }

  public abstract LoadProfileTimeSeries<V> build(
      LoadProfileTimeSeriesMetaInformation metaInformation, Set<LoadProfileEntry<V>> data);

  public abstract P parseProfile(String profile);
}
