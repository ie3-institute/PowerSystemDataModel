/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.Failure;
import java.util.Set;

public interface SourceValidator<C> {

  /**
   * Method for validating a data source.
   *
   * @param actualFields fields that were found in the source data
   * @param entityClass that should be buildable from the source data
   * @return either an exception wrapped by a {@link Failure} or an empty success
   */
  Try<Void, FactoryException> validate(Set<String> actualFields, Class<? extends C> entityClass);
}
