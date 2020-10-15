/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv.timeseries;

import edu.ie3.datamodel.io.csv.FileNameMetaInformation;
import java.util.Objects;
import java.util.UUID;

/** Specific meta information, that can be derived from a individual time series file */
public class IndividualTimeSeriesMetaInformation implements FileNameMetaInformation {
  private final UUID uuid;
  private final ColumnScheme columnScheme;

  public IndividualTimeSeriesMetaInformation(UUID uuid, ColumnScheme columnScheme) {
    this.uuid = uuid;
    this.columnScheme = columnScheme;
  }

  public UUID getUuid() {
    return uuid;
  }

  public ColumnScheme getColumnScheme() {
    return columnScheme;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IndividualTimeSeriesMetaInformation)) return false;
    IndividualTimeSeriesMetaInformation that = (IndividualTimeSeriesMetaInformation) o;
    return uuid.equals(that.uuid) && columnScheme == that.columnScheme;
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, columnScheme);
  }
}
