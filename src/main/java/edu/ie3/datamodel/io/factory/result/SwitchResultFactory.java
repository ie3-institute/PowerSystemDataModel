/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.result.connector.SwitchResult;
import java.time.ZonedDateTime;
import java.util.*;

public class SwitchResultFactory extends ResultEntityFactory<SwitchResult> {

  private static final String CLOSED = "closed";

  public SwitchResultFactory() {
    super(SwitchResult.class);
  }

  /**
   * Create a new factory to build {@link SwitchResult}s and utilize the given date time formatter
   * pattern to parse date time strings
   *
   * @param dtfPattern Pattern to parse date time strings
   */
  public SwitchResultFactory(String dtfPattern) {
    super(dtfPattern, SwitchResult.class);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {

    Set<String> minConstructorParams = newSet(TIME, INPUT_MODEL, CLOSED);
    Set<String> optionalFields = expandSet(minConstructorParams, ENTITY_UUID);

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected SwitchResult buildModel(EntityData data) {
    ZonedDateTime time = timeUtil.toZonedDateTime(data.getField(TIME));
    UUID inputModel = data.getUUID(INPUT_MODEL);

    final boolean closed = data.getBoolean(CLOSED);

    return new SwitchResult(time, inputModel, closed);
  }
}
