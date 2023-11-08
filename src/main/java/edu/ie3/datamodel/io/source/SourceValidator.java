/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import java.util.Set;

public interface SourceValidator {

  /**
   * Method for validating a data source.
   *
   * @param foundFields fields that were found in the source data
   * @param entityClass that should be buildable from the source data
   */
  void validate(Set<String> foundFields, Class<?> entityClass);
}
