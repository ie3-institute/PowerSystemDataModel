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
   * @param dtfPattern Pattern to parse date time strings
   */
  public FlexOptionsResultFactory(String dtfPattern) {
    super(dtfPattern, FlexOptionsResult.class);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    Set<String> minConstructorParams = newSet(TIME, INPUT_MODEL, P_REF, P_MIN, P_MAX);
    Set<String> optionalFields = expandSet(minConstructorParams, ENTITY_UUID);

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected FlexOptionsResult buildModel(EntityData data) {
    ZonedDateTime zdtTime = timeUtil.toZonedDateTime(data.getField(TIME));
    UUID inputModelUuid = data.getUUID(INPUT_MODEL);
    ComparableQuantity<Power> pRef = data.getQuantity(P_REF, StandardUnits.ACTIVE_POWER_RESULT);
    ComparableQuantity<Power> pMin = data.getQuantity(P_MIN, StandardUnits.ACTIVE_POWER_RESULT);
    ComparableQuantity<Power> pMax = data.getQuantity(P_MAX, StandardUnits.ACTIVE_POWER_RESULT);

    Optional<UUID> uuidOpt =
        data.containsKey(ENTITY_UUID) ? Optional.of(data.getUUID(ENTITY_UUID)) : Optional.empty();

    return uuidOpt
        .map(uuid -> new FlexOptionsResult(uuid, zdtTime, inputModelUuid, pRef, pMin, pMax))
        .orElseGet(() -> new FlexOptionsResult(zdtTime, inputModelUuid, pRef, pMin, pMax));
  }
}
