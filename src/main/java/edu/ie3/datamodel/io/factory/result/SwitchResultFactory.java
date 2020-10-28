/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.models.result.connector.SwitchResult;
import java.time.ZonedDateTime;
import java.util.*;

public class SwitchResultFactory extends ResultEntityFactory<SwitchResult> {

  private static final String CLOSED = "closed";

  public SwitchResultFactory() {
    super(SwitchResult.class);
  }

  @Override
  protected List<Set<String>> getFields(SimpleEntityData data) {

    Set<String> minConstructorParams = newSet(TIMESTAMP, INPUT_MODEL, CLOSED);
    Set<String> optionalFields = expandSet(minConstructorParams, ENTITY_UUID);

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected SwitchResult buildModel(SimpleEntityData data) {
    Optional<UUID> uuidOpt =
        data.containsKey(ENTITY_UUID) ? Optional.of(data.getUUID(ENTITY_UUID)) : Optional.empty();
    ZonedDateTime timestamp = TIME_UTIL.toZonedDateTime(data.getField(TIMESTAMP));
    UUID inputModel = data.getUUID(INPUT_MODEL);

    final boolean closed = data.getBoolean(CLOSED);

    return uuidOpt
        .map(uuid -> new SwitchResult(uuid, timestamp, inputModel, closed))
        .orElseGet(() -> new SwitchResult(timestamp, inputModel, closed));
  }
}
