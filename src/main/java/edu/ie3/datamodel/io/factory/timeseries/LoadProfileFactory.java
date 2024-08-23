/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.Factory;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileTimeSeries;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class LoadProfileFactory<P extends LoadProfile, E extends LoadProfileEntry>
    extends Factory<E, LoadProfileData<E>, List<E>> {
  public static final String QUARTER_HOUR = "quarterHour";

  public LoadProfileFactory(Class<? extends E> valueClass) {
    super(valueClass);
  }

  public abstract LoadProfileTimeSeries<E> build(UUID uuid, P loadProfile, Set<E> entries);
}
