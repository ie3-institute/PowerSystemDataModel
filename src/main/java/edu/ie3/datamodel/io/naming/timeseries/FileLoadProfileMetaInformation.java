/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming.timeseries;

import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

public abstract class FileLoadProfileMetaInformation extends LoadProfileMetaInformation {
  private final Path fullFilePath;

  protected FileLoadProfileMetaInformation(String profile, Path fullFilePath) {
    super(profile);
    this.fullFilePath = Objects.requireNonNull(fullFilePath, "fullFilePath");
  }

  protected FileLoadProfileMetaInformation(UUID uuid, String profile, Path fullFilePath) {
    super(uuid, profile);
    this.fullFilePath = Objects.requireNonNull(fullFilePath, "fullFilePath");
  }

  protected FileLoadProfileMetaInformation(
      LoadProfileMetaInformation metaInformation, Path fullFilePath) {
    this(metaInformation.getUuid(), metaInformation.getProfile(), fullFilePath);
  }

  public Path getFullFilePath() {
    return fullFilePath;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FileLoadProfileMetaInformation that)) return false;
    if (!super.equals(o)) return false;
    return fullFilePath.equals(that.fullFilePath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fullFilePath);
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
        + "fullFilePath="
        + fullFilePath
        + '}';
  }
}
