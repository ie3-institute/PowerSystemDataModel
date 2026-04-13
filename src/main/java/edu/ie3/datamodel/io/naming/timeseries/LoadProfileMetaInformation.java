/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming.timeseries;

import edu.ie3.datamodel.models.profile.PowerProfileKey;
import java.util.Objects;
import java.util.UUID;

/** Specific meta information, that can be derived from a load profile time series file */
public class LoadProfileMetaInformation extends TimeSeriesMetaInformation {
  private final PowerProfileKey profileKey;

  public LoadProfileMetaInformation(String profileKey) {
    super(UUID.randomUUID());
    this.profileKey = new PowerProfileKey(profileKey);
  }

  public LoadProfileMetaInformation(PowerProfileKey powerProfileKey) {
    super(UUID.randomUUID());
    this.profileKey = powerProfileKey;
  }

  public PowerProfileKey getProfileKey() {
    return profileKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LoadProfileMetaInformation that)) return false;
    if (!super.equals(o)) return false;
    return profileKey.equals(that.profileKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), profileKey);
  }

  @Override
  public String toString() {
    return "LoadProfileTimeSeriesMetaInformation{profileKey='" + profileKey.getValue() + '}';
  }
}
