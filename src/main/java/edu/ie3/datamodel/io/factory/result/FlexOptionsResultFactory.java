/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.system.FlexOptionsResult;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

public class FlexOptionsResultFactory extends ResultEntityFactory<FlexOptionsResult> {

  private static final String P_REF = "pRef";
  private static final String P_MIN = "pMin";
  private static final String P_MAX = "pMax";

  public FlexOptionsResultFactory() {
    super(FlexOptionsResult.class);
  }

  /**
   * Create a new factory to build {@link FlexOptionsResult}s and utilize the given date time
   * formatter pattern to parse date time strings
   *
   * @param dateTimeFormatter to parse date time strings
   */
  public FlexOptionsResultFactory(DateTimeFormatter dateTimeFormatter) {
    super(dateTimeFormatter, FlexOptionsResult.class);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    Set<String> minConstructorParams = newSet(TIME, INPUT_MODEL, P_REF, P_MIN, P_MAX);
    return List.of(minConstructorParams);
  }

  @Override
  protected FlexOptionsResult buildModel(EntityData data) {
    ZonedDateTime zdtTime = timeUtil.toZonedDateTime(data.getField(TIME));
    UUID inputModelUuid = data.getUUID(INPUT_MODEL);
    ComparableQuantity<Power> pRef = data.getQuantity(P_REF, StandardUnits.ACTIVE_POWER_RESULT);
    ComparableQuantity<Power> pMin = data.getQuantity(P_MIN, StandardUnits.ACTIVE_POWER_RESULT);
    ComparableQuantity<Power> pMax = data.getQuantity(P_MAX, StandardUnits.ACTIVE_POWER_RESULT);

    return new FlexOptionsResult(zdtTime, inputModelUuid, pRef, pMin, pMax);
  }
}
