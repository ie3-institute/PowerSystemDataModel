/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming.timeseries;

import edu.ie3.datamodel.io.file.FileType;
import java.nio.file.Path;
import java.util.Objects;

public class FileLoadProfileMetaInformation extends LoadProfileMetaInformation {
  private final Path fullFilePath;
  private final FileType fileType;

  public FileLoadProfileMetaInformation(String profile, Path fullFilePath, FileType fileType) {
    super(profile);
    this.fullFilePath = fullFilePath;
    this.fileType = fileType;
  }

  public Path getFullFilePath() {
    return fullFilePath;
  }

  public FileType getFileType() {
    return fileType;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    FileLoadProfileMetaInformation that = (FileLoadProfileMetaInformation) o;
    return Objects.equals(fullFilePath, that.fullFilePath) && fileType == that.fileType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fullFilePath, fileType);
  }

  @Override
  public String toString() {
    return "FileLoadProfileMetaInformation{"
        + ", fullFilePath='"
        + fullFilePath
        + '\''
        + ", fileType="
        + fileType
        + '}';
  }
}
