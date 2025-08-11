/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation;
import java.nio.file.Path;
import java.util.Objects;

/** The type Csv load profile meta information. */
public class CsvLoadProfileMetaInformation extends LoadProfileMetaInformation {
  /** Represents the full file path for the load profile. */
  private final Path fullFilePath;

  /**
   * Instantiates a new Csv load profile meta information.
   *
   * @param profile the profile
   * @param fullFilePath the full file path
   */
  public CsvLoadProfileMetaInformation(String profile, Path fullFilePath) {
    super(profile);
    this.fullFilePath = fullFilePath;
  }

  /**
   * Instantiates a new Csv load profile meta information.
   *
   * @param metaInformation the meta information
   * @param fullFilePath the full file path
   */
  public CsvLoadProfileMetaInformation(
      LoadProfileMetaInformation metaInformation, Path fullFilePath) {
    this(metaInformation.getProfile(), fullFilePath);
  }

  /**
   * Gets full file path.
   *
   * @return the full file path
   */
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
