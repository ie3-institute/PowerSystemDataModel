/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.file;

import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

public class FileLoadProfileMetaInformation extends LoadProfileMetaInformation {
  private final Path path;
  private final FileType fileType;

  public FileLoadProfileMetaInformation(String profile, Path path, FileType fileType) {
    super(profile);
    this.path = Objects.requireNonNull(path, "path must not be null");
    this.fileType = Objects.requireNonNull(fileType, "fileType must not be null");
  }

  public FileLoadProfileMetaInformation(UUID uuid, String profile, Path path, FileType fileType) {
    super(uuid, profile);
    this.path = Objects.requireNonNull(path, "path must not be null");
    this.fileType = Objects.requireNonNull(fileType, "fileType must not be null");
  }

  public FileLoadProfileMetaInformation(
      LoadProfileMetaInformation metaInformation, Path path, FileType fileType) {
    this(metaInformation.getUuid(), metaInformation.getProfile(), path, fileType);
  }

  public Path getPath() {
    return path;
  }

  public FileType getFileType() {
    return fileType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FileLoadProfileMetaInformation that)) return false;
    if (!super.equals(o)) return false;
    return path.equals(that.path) && fileType == that.fileType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), path, fileType);
  }

  @Override
  public String toString() {
    return "FileLoadProfileMetaInformation{"
        + "uuid='"
        + getUuid()
        + '\''
        + ", profile='"
        + getProfile()
        + '\''
        + ", path="
        + path
        + ", fileType="
        + fileType
        + '}';
  }
}
