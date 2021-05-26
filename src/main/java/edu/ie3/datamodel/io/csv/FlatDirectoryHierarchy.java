/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.exceptions.FileException;
import edu.ie3.datamodel.models.UniqueEntity;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.apache.commons.io.FilenameUtils;

/** Default directory hierarchy for input models */
public class FlatDirectoryHierarchy implements DirectoryHierarchy {

  /** Use the unix file separator here. */
  protected static final String FILE_SEPARATOR = File.separator;

  /** The project's directory beneath the {@code baseDirectory} */
  private final Path projectDirectory;

  public FlatDirectoryHierarchy(String baseDirectory, String gridName) {
    /* Prepare the base path */
    String baseDirectoryNormalized =
        FilenameUtils.normalizeNoEndSeparator(baseDirectory, true) + FILE_SEPARATOR;
    this.projectDirectory =
        Paths.get(
                baseDirectoryNormalized
                    + FilenameUtils.normalizeNoEndSeparator(gridName, true)
                    + FILE_SEPARATOR)
            .toAbsolutePath();
  }

  /**
   * Checks, if the project directory beneath the base directory is okay.
   *
   * @throws FileException if not
   */
  public void validate() throws FileException {
    if (!Files.exists(projectDirectory))
      throw new FileException("The path '" + projectDirectory + "' does not exist.");
    if (!Files.isDirectory(projectDirectory))
      throw new FileException("The path '" + projectDirectory + "' has to be a directory.");
  }

  /**
   * Creates project directory of this flat directory hierarchy.
   *
   * @throws IOException If the creation of the project directory is not possible
   */
  public void createDirs() throws IOException {
    Files.createDirectories(projectDirectory);
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
}
