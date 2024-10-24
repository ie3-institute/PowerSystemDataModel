/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.io.naming.timeseries.LoadProfileTimeSeriesMetaInformation;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

public class CsvLoadProfileMetaInformation extends LoadProfileTimeSeriesMetaInformation {
  private final Path fullFilePath;

  public CsvLoadProfileMetaInformation(UUID uuid, String profile, Path fullFilePath) {
    super(uuid, profile);
    this.fullFilePath = fullFilePath;
  }

  public CsvLoadProfileMetaInformation(
      LoadProfileTimeSeriesMetaInformation metaInformation, Path fullFilePath) {
    this(metaInformation.getUuid(), metaInformation.getProfile(), fullFilePath);
  }

  public Path getFullFilePath() {
    return fullFilePath;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CsvLoadProfileMetaInformation that)) return false;
    if (!super.equals(o)) return false;
    return fullFilePath.equals(that.fullFilePath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fullFilePath);
  }

  @Override
  public String toString() {
    return "CsvLoadProfileMetaInformation{"
        + "uuid='"
        + getUuid()
        + '\''
        + ", profile='"
        + getProfile()
        + '\''
        + "fullFilePath="
        + fullFilePath
        + '}';
  }
}
