/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sql;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import java.util.Objects;
import java.util.UUID;

/**
 * Enhancing the {@link IndividualTimeSeriesMetaInformation} with the name of the table containing
 * the time series
 */
public class SqlIndividualTimeSeriesMetaInformation extends IndividualTimeSeriesMetaInformation {
  private final String tableName;

  public SqlIndividualTimeSeriesMetaInformation(
      UUID uuid, ColumnScheme columnScheme, String tableName) {
    super(uuid, columnScheme);
    this.tableName = tableName;
  }

  public SqlIndividualTimeSeriesMetaInformation(
      IndividualTimeSeriesMetaInformation metaInformation, String fullFilePath) {
    this(metaInformation.getUuid(), metaInformation.getColumnScheme(), fullFilePath);
  }

  public String getTableName() {
    return tableName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SqlIndividualTimeSeriesMetaInformation that)) return false;
    if (!super.equals(o)) return false;
    return tableName.equals(that.tableName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), tableName);
  }

  @Override
  public String toString() {
    return "SqlIndividualTimeSeriesMetaInformation{"
        + "uuid="
        + getUuid()
        + ", columnScheme="
        + getColumnScheme()
        + ", tableName='"
        + tableName
        + '\''
        + '}';
  }
}
