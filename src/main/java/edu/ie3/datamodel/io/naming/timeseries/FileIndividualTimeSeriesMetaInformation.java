/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming.timeseries;

import edu.ie3.datamodel.io.file.FileType;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

/** Enhancing the {@link IndividualTimeSeriesMetaInformation} with the full path to csv file */
public class FileIndividualTimeSeriesMetaInformation extends IndividualTimeSeriesMetaInformation {
  private final Path fullFilePath;
  private final FileType fileType;

  public FileIndividualTimeSeriesMetaInformation(
      UUID uuid, ColumnScheme columnScheme, Path fullFilePath, FileType fileType) {
    super(uuid, columnScheme);
    this.fullFilePath = fullFilePath;
    this.fileType = fileType;
  }

  public FileIndividualTimeSeriesMetaInformation(
      IndividualTimeSeriesMetaInformation metaInformation, Path fullFilePath, FileType fileType) {
    this(metaInformation.getUuid(), metaInformation.getColumnScheme(), fullFilePath, fileType);
  }

  public Path getFullFilePath() {
    return fullFilePath;
  }

  public FileType getFileType() {
    return fileType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FileIndividualTimeSeriesMetaInformation that)) return false;
    if (!super.equals(o)) return false;
    return fullFilePath.equals(that.fullFilePath) && fileType.equals(that.fileType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fullFilePath, fileType);
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
        + ", fileType="
        + fileType
        + '}';
  }
}
