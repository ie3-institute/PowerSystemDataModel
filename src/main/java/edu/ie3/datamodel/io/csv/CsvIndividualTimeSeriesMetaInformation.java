/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import java.util.Objects;
import java.util.UUID;

/** Enhancing the {@link IndividualTimeSeriesMetaInformation} with the full path to csv file */
public class CsvIndividualTimeSeriesMetaInformation extends IndividualTimeSeriesMetaInformation {
  private final String fullFilePath;

  public CsvIndividualTimeSeriesMetaInformation(
      UUID uuid, ColumnScheme columnScheme, String fullFilePath) {
    super(uuid, columnScheme);
    this.fullFilePath = fullFilePath;
  }

  public CsvIndividualTimeSeriesMetaInformation(
      IndividualTimeSeriesMetaInformation metaInformation, String fullFilePath) {
    this(metaInformation.getUuid(), metaInformation.getColumnScheme(), fullFilePath);
  }

  public String getFullFilePath() {
    return fullFilePath;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CsvIndividualTimeSeriesMetaInformation)) return false;
    if (!super.equals(o)) return false;
    CsvIndividualTimeSeriesMetaInformation that = (CsvIndividualTimeSeriesMetaInformation) o;
    return fullFilePath.equals(that.fullFilePath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fullFilePath);
  }

  @Override
  public String toString() {
    return "CsvIndividualTimeSeriesMetaInformation{"
        + "uuid="
        + getUuid()
        + ", columnScheme="
        + getColumnScheme()
        + ", fullFilePath='"
        + fullFilePath
        + '\''
        + '}';
  }
}
