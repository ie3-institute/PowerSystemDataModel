/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.util.TimeUtil;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Internal API for building {@link ResultEntity}s. This additional abstraction layer is necessary
 * to create generic reader for {@link ResultEntity}s only and furthermore removes code duplicates.
 */
public abstract class ResultEntityFactory<T extends ResultEntity, R extends ResultEntity>
    extends EntityFactory<T, R> {

  private final TimeUtil timeUtil;

  @SafeVarargs
  protected ResultEntityFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
    timeUtil = TimeUtil.withDefaults;
  }

  @Override
  protected R buildModel(Map<String, String> data) {
    return buildModel(
        data, timeUtil.toZonedDateTime(getField(data, TIME)), getUUID(data, INPUT_MODEL));
  }

  protected abstract R buildModel(Map<String, String> data, ZonedDateTime time, UUID inputModel);
}
