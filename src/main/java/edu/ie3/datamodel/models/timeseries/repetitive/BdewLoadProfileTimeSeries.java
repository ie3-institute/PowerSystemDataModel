/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.value.load.BdewLoadValues;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Describes a bdew load profile time series with repetitive values that can be calculated from a
 * pattern
 */
public class BdewLoadProfileTimeSeries extends LoadProfileTimeSeries<BdewLoadValues> {

  public BdewLoadProfileTimeSeries(
      UUID uuid,
      BdewStandardLoadProfile loadProfile,
      Set<LoadProfileEntry<BdewLoadValues>> values) {
    super(uuid, loadProfile, values);
  }

  @Override
  public BdewStandardLoadProfile getLoadProfile() {
    return (BdewStandardLoadProfile) super.getLoadProfile();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String toString() {
    return "BDEWLoadProfileTimeSeries{"
        + "uuid="
        + getUuid()
        + "loadProfile="
        + getLoadProfile()
        + ", valueMapping="
        + getValueMapping()
        + '}';
  }
}
