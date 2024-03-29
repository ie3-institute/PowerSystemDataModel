/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

/** Enhancing the {@link IndividualTimeSeriesMetaInformation} with the full path to csv file */
public class CsvIndividualTimeSeriesMetaInformation extends IndividualTimeSeriesMetaInformation {
  private final Path fullFilePath;

  public CsvIndividualTimeSeriesMetaInformation(
      UUID uuid, ColumnScheme columnScheme, Path fullFilePath) {
    super(uuid, columnScheme);
    this.fullFilePath = fullFilePath;
  }

  public CsvIndividualTimeSeriesMetaInformation(
      IndividualTimeSeriesMetaInformation metaInformation, Path fullFilePath) {
    this(metaInformation.getUuid(), metaInformation.getColumnScheme(), fullFilePath);
  }

  public Path getFullFilePath() {
    return fullFilePath;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CsvIndividualTimeSeriesMetaInformation that)) return false;
    if (!super.equals(o)) return false;
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
