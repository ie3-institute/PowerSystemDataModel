/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.result;

import edu.ie3.io.factory.SimpleEntityFactory;
import edu.ie3.models.result.ResultEntity;

/**
 * Internal API for building {@link ResultEntity}s. This additional abstraction layer is necessary
 * to create generic reader for {@link ResultEntity}s only and furthermore removes code duplicates.
 *
 * @version 0.1
 * @since 11.02.20
 */
abstract class ResultEntityFactory<T extends ResultEntity> extends SimpleEntityFactory<T> {

  protected static final String ENTITY_UUID = "uuid";
  protected static final String TIMESTAMP = "timestamp";
  protected static final String INPUT_MODEL = "inputModel";

  public ResultEntityFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
  }
}
