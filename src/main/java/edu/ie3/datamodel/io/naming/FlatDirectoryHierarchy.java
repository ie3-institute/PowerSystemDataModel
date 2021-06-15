/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.models.UniqueEntity;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/** Default directory hierarchy for input models */
public class FlatDirectoryHierarchy implements FileHierarchy {

  /** Use the unix file separator here. */
  protected static final String FILE_SEPARATOR = File.separator;

  /** Base directory for this specific grid model. The base path should be a directory. */
  private final Path baseDirectory;

  public FlatDirectoryHierarchy(String baseDirectory) {
    /* Prepare the base path */
    String baseDirectoryNormalized =
            FilenameUtils.normalizeNoEndSeparator(baseDirectory, true) + FILE_SEPARATOR;
    this.baseDirectory = Paths.get(baseDirectoryNormalized).toAbsolutePath();
  }

  /**
   * Gives empty sub directory.
   *
   * @param cls Class to define the sub directory for
   * @param fileSeparator The file separator to use
   * @return An Option to the regarding sub directory as a string
   */
  @Override
  public Optional<String> getSubDirectory(Class<? extends UniqueEntity> cls, String fileSeparator) {
    return Optional.empty();
  }


  /**
   * Gives the {@link #baseDirectory}).
   *
   * @return An Option to the base directory as a string
   */
  @Override
  public Optional<String> getBaseDirectory() {
    return Optional.of(this.baseDirectory.toString());
  }
}
