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
   * Gives empty sub directory.
   *
   * @param cls Class to define the sub directory for
   * @return An Option to the regarding sub directory as a string
   */
  @Override
  public Optional<Path> getSubDirectory(Class<? extends Entity> cls) {
    return Optional.empty();
  }

  /**
   * Gives the baseDirectory, which is Empty.
   *
   * @return An Option to the base directory as a string
   * @deprecated Use {@link edu.ie3.datamodel.io.connectors.CsvFileConnector} instead
   */
  @Deprecated(since = "3.0", forRemoval = true)
  @Override
  public Optional<Path> getBaseDirectory() {
    return Optional.empty();
  }
}
