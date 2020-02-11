/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.result;

import edu.ie3.io.factory.SimpleEntityData;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.result.ThermalSinkResult;
import edu.ie3.util.TimeTools;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.Energy;
import tec.uom.se.quantity.Quantities;

public class ThermalSinkResultFactory extends ResultEntityFactory<ThermalSinkResult> {

  private static final String Q_DEMAND = "qDemand";

  public ThermalSinkResultFactory() {
    super(ThermalSinkResult.class);
  }

  @Override
  protected List<Set<String>> getFields(SimpleEntityData simpleEntityData) {
    Set<String> minConstructorParams = newSet(TIMESTAMP, INPUT_MODEL, Q_DEMAND);
    Set<String> optionalFields = expandSet(minConstructorParams, ENTITY_UUID);

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected ThermalSinkResult buildModel(SimpleEntityData simpleEntityData) {
    Map<String, String> fieldsToValues = simpleEntityData.getFieldsToValues();

    ZonedDateTime zdtTimestamp = TimeTools.toZonedDateTime(fieldsToValues.get(TIMESTAMP));
    UUID inputModelUuid = UUID.fromString(fieldsToValues.get(INPUT_MODEL));
    Quantity<Energy> qDemandQuantity =
        Quantities.getQuantity(
            Double.parseDouble(fieldsToValues.get(Q_DEMAND)), StandardUnits.HEAT_DEMAND);
    Optional<UUID> uuidOpt =
        fieldsToValues.containsKey(ENTITY_UUID)
            ? Optional.of(UUID.fromString(fieldsToValues.get(ENTITY_UUID)))
            : Optional.empty();

    return uuidOpt
        .map(uuid -> new ThermalSinkResult(uuid, zdtTimestamp, inputModelUuid, qDemandQuantity))
        .orElseGet(() -> new ThermalSinkResult(zdtTimestamp, inputModelUuid, qDemandQuantity));
  }
}
