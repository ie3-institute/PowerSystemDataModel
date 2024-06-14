/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.util.TimeUtil;
import java.time.format.DateTimeFormatter;

/**
 * Internal API for building {@link ResultEntity}s. This additional abstraction layer is necessary
 * to create generic reader for {@link ResultEntity}s only and furthermore removes code duplicates.
 *
 * @version 0.1
 * @since 11.02.20
 */
public abstract class ResultEntityFactory<T extends ResultEntity>
    extends EntityFactory<T, EntityData> {

  protected static final String TIME = "time";

  protected final TimeUtil timeUtil;

  @SafeVarargs
  protected ResultEntityFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
    timeUtil = TimeUtil.withDefaults;
  }

  @SafeVarargs
  protected ResultEntityFactory(
      DateTimeFormatter dateTimeFormatter, Class<? extends T>... allowedClasses) {
    super(allowedClasses);
    timeUtil = new TimeUtil(dateTimeFormatter);
  }
}
