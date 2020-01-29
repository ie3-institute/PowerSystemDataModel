/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.result;

import edu.ie3.io.factory.EntityData;
import edu.ie3.io.factory.EntityFactoryImpl;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.result.ThermalSinkResult;
import edu.ie3.util.TimeTools;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.Power;
import tec.uom.se.quantity.Quantities;

public class ThermalSinkResultFactory extends EntityFactoryImpl<ThermalSinkResult> {
  private static final String entityUuid = "uuid";
  private static final String timestamp = "timestamp";
  private static final String inputModel = "inputModel";
  private static final String qDemand = "qDemand";

  public ThermalSinkResultFactory() {
    super(ThermalSinkResult.class);
  }

  @Override
  protected List<Set<String>> getFields(EntityData entityData) {
    Set<String> minConstructorParams = newSet(timestamp, inputModel, qDemand);
    Set<String> optionalFields = enhanceSet(minConstructorParams, entityUuid);

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected ThermalSinkResult buildModel(EntityData simpleEntityData) {
    Map<String, String> fieldsToValues = simpleEntityData.getFieldsToValues();

    ZonedDateTime zdtTimestamp = TimeTools.toZonedDateTime(fieldsToValues.get(timestamp));
    UUID inputModelUuid = UUID.fromString(fieldsToValues.get(inputModel));
    Quantity<Power> q =
        Quantities.getQuantity(
            Double.parseDouble(fieldsToValues.get(qDemand)), StandardUnits.REACTIVE_POWER_OUT);
    Optional<UUID> uuidOpt =
        fieldsToValues.containsKey(entityUuid)
            ? Optional.of(UUID.fromString(fieldsToValues.get(entityUuid)))
            : Optional.empty();

    return uuidOpt
        .map(uuid -> new ThermalSinkResult(uuid, zdtTimestamp, inputModelUuid, q))
        .orElseGet(() -> new ThermalSinkResult(zdtTimestamp, inputModelUuid, q));
  }
}
