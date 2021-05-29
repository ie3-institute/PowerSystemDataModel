/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.models.UniqueEntity;
import java.util.Optional;

public interface DirectoryHierarchy extends FileHierarchy {

  @Override
  Optional<String> getSubDirectory(Class<? extends UniqueEntity> cls, String fileSeparator);
}
