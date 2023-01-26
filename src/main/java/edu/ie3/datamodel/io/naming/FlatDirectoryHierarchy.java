/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.models.UniqueEntity;
import java.nio.file.Path;
import java.util.Optional;

/** Default directory hierarchy for input models */
public class FlatDirectoryHierarchy implements FileHierarchy {

  /**
   * Gives empty sub directory.
   *
   * @param cls Class to define the sub directory for
   * @param fileSeparator The file separator to use
   * @return An Option to the regarding sub directory as a string
   */
  @Override
  public Optional<Path> getSubDirectory(Class<? extends UniqueEntity> cls, String fileSeparator) {
    return Optional.empty();
  }

  /**
   * Gives the baseDirectory, which is Empty.
   *
   * @return An Option to the base directory as a string
   */
  @Override
  public Optional<Path> getBaseDirectory() {
    return Optional.empty();
  }
}
