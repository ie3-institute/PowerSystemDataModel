/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.models.Entity;
import java.nio.file.Path;
import java.util.Optional;

/** Default directory hierarchy for input models */
public class FlatDirectoryHierarchy implements FileHierarchy {

  /**
   * Gives empty subdirectory.
   *
   * @param cls Class to define the subdirectory for
   * @return An Option to the regarding subdirectory as a string
   */
  @Override
  public Optional<Path> getSubDirectory(Class<? extends Entity> cls) {
    return Optional.empty();
  }
}
