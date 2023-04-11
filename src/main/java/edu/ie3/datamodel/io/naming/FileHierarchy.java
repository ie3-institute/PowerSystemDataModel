/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.models.UniqueEntity;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Abstract definition of a file hierarchy (a piece of software, that knows where to find / place a
 * file reflecting a certain class) inside of a nested sub directory structure
 */
public interface FileHierarchy {
  /**
   * Determines the correct subdirectory (w.r.t. an arbitrary base directory) for a certain given
   * class using the file separator provided by {@link Path} for delimiting between directories and
   * files.
   *
   * @param cls Class to define the sub directory for
   * @return An Option to the regarding sub directory as a string
   */
  Optional<Path> getSubDirectory(Class<? extends UniqueEntity> cls);

  /**
   * Determines the base directory.
   *
   * @return An option to the base directory
   */
  Optional<Path> getBaseDirectory();
}
