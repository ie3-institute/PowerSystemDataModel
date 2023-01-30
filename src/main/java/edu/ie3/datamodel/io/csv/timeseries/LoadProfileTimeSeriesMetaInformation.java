/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv.timeseries;

import edu.ie3.datamodel.io.csv.FileNameMetaInformation;
import java.util.Objects;
import java.util.UUID;

/**
 * Specific meta information, that can be derived from a load profile time series file
 *
 * @deprecated since 3.0. Use {@link
 *     edu.ie3.datamodel.io.naming.timeseries.LoadProfileTimeSeriesMetaInformation} instead
 */
@Deprecated(since = "3.0", forRemoval = true)
public class LoadProfileTimeSeriesMetaInformation extends FileNameMetaInformation {
  private final String profile;

  public LoadProfileTimeSeriesMetaInformation(UUID uuid, String profile) {
    super(uuid);
    this.profile = profile;
  }

  public LoadProfileTimeSeriesMetaInformation(
      edu.ie3.datamodel.io.naming.timeseries.LoadProfileTimeSeriesMetaInformation metaInformation) {
    super(metaInformation.getUuid());
    this.profile = metaInformation.getProfile();
  }

  public String getProfile() {
    return profile;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LoadProfileTimeSeriesMetaInformation that)) return false;
    if (!super.equals(o)) return false;
    return profile.equals(that.profile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), profile);
  }

  @Override
  public String toString() {
    return "LoadProfileTimeSeriesMetaInformation{"
        + "uuid='"
        + getUuid()
        + '\''
        + ", profile='"
        + profile
        + '\''
        + '}';
  }
}
