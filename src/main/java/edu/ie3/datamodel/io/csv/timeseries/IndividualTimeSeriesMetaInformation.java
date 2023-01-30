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
 * Specific meta information, that can be derived from a individual time series file
 *
 * @deprecated since 3.0. Use {@link
 *     edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation} instead
 */
@Deprecated(since = "3.0", forRemoval = true)
public class IndividualTimeSeriesMetaInformation extends FileNameMetaInformation {
  private final ColumnScheme columnScheme;

  public IndividualTimeSeriesMetaInformation(UUID uuid, ColumnScheme columnScheme) {
    super(uuid);
    this.columnScheme = columnScheme;
  }

  public IndividualTimeSeriesMetaInformation(
      edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation
          newMetaInformation) {
    super(newMetaInformation.getUuid());
    this.columnScheme =
        ColumnScheme.parse(newMetaInformation.getColumnScheme().toString())
            .orElseThrow(
                () ->
                    new RuntimeException(
                        "Cannot convert new column scheme "
                            + newMetaInformation.getColumnScheme().getScheme()
                            + " to deprecated column scheme!"));
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
