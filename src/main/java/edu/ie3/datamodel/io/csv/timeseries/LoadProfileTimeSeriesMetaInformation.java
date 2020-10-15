/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv.timeseries;

import edu.ie3.datamodel.io.csv.FileNameMetaInformation;
import java.util.Objects;
import java.util.UUID;

/** Specific meta information, that can be derived from a load profile time series file */
public class LoadProfileTimeSeriesMetaInformation implements FileNameMetaInformation {
  private final UUID uuid;
  private final String profile;

  public LoadProfileTimeSeriesMetaInformation(UUID uuid, String profile) {
    this.uuid = uuid;
    this.profile = profile;
  }

  public UUID getUuid() {
    return uuid;
  }

  public String getProfile() {
    return profile;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LoadProfileTimeSeriesMetaInformation)) return false;
    LoadProfileTimeSeriesMetaInformation that = (LoadProfileTimeSeriesMetaInformation) o;
    return uuid.equals(that.uuid) && profile.equals(that.profile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, profile);
  }
}
