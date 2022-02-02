/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming.timeseries;

import edu.ie3.datamodel.io.naming.DataSourceMetaInformation;
import java.util.Objects;
import java.util.UUID;

/** Specific meta information, that can be derived from an individual time series file */
public class IndividualTimeSeriesMetaInformation extends DataSourceMetaInformation {
  private final ColumnScheme columnScheme;

  public IndividualTimeSeriesMetaInformation(UUID uuid, ColumnScheme columnScheme) {
    super(uuid);
    this.columnScheme = columnScheme;
  }

  public ColumnScheme getColumnScheme() {
    return columnScheme;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IndividualTimeSeriesMetaInformation that)) return false;
    if (!super.equals(o)) return false;
    return columnScheme == that.columnScheme;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), columnScheme);
  }

  @Override
  public String toString() {
    return "IndividualTimeSeriesMetaInformation{"
        + "uuid="
        + getUuid()
        + ", columnScheme="
        + columnScheme
        + '}';
  }
}
