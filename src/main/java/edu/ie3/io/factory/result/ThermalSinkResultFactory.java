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
  protected ThermalSinkResult buildModel(SimpleEntityData data) {
    ZonedDateTime zdtTimestamp = TimeTools.toZonedDateTime(data.get(TIMESTAMP));
    UUID inputModelUuid = data.getUUID(INPUT_MODEL);
    Quantity<Energy> qDemandQuantity = data.get(Q_DEMAND, StandardUnits.HEAT_DEMAND);
    Optional<UUID> uuidOpt =
        data.containsKey(ENTITY_UUID) ? Optional.of(data.getUUID(ENTITY_UUID)) : Optional.empty();

    return uuidOpt
        .map(uuid -> new ThermalSinkResult(uuid, zdtTimestamp, inputModelUuid, qDemandQuantity))
        .orElseGet(() -> new ThermalSinkResult(zdtTimestamp, inputModelUuid, qDemandQuantity));
  }
}
