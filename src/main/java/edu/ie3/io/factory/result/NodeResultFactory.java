/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.result;

import edu.ie3.io.factory.SimpleEntityData;
import edu.ie3.io.factory.SimpleEntityFactory;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.result.NodeResult;
import edu.ie3.util.TimeTools;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import tec.uom.se.quantity.Quantities;

public class NodeResultFactory extends SimpleEntityFactory<NodeResult> {
  private static final String ENTITY_UUID = "uuid";
  private static final String TIMESTAMP = "timestamp";
  private static final String INPUT_MODEL = "inputModel";
  private static final String VMAG = "vmag";
  private static final String VANG = "vang";

  public NodeResultFactory() {
    super(NodeResult.class);
  }

  @Override
  protected List<Set<String>> getFields(SimpleEntityData entityData) {
    Set<String> minConstructorParams = newSet(TIMESTAMP, INPUT_MODEL, VMAG, VANG);
    Set<String> optionalFields = expandSet(minConstructorParams, ENTITY_UUID);

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected NodeResult buildModel(SimpleEntityData simpleEntityData) {
    Map<String, String> fieldsToValues = simpleEntityData.getFieldsToValues();

    ZonedDateTime zdtTimestamp = TimeTools.toZonedDateTime(fieldsToValues.get(TIMESTAMP));
    UUID inputModelUuid = UUID.fromString(fieldsToValues.get(INPUT_MODEL));
    Quantity<Dimensionless> vMagValue =
        Quantities.getQuantity(
            Double.parseDouble(fieldsToValues.get(VMAG)), StandardUnits.VOLTAGE_MAGNITUDE);
    Quantity<Angle> vAngValue =
        Quantities.getQuantity(
            Double.parseDouble(fieldsToValues.get(VANG)), StandardUnits.VOLTAGE_ANGLE);
    Optional<UUID> uuidOpt =
        fieldsToValues.containsKey(ENTITY_UUID)
            ? Optional.of(UUID.fromString(fieldsToValues.get(ENTITY_UUID)))
            : Optional.empty();

    return uuidOpt
        .map(uuid -> new NodeResult(uuid, zdtTimestamp, inputModelUuid, vMagValue, vAngValue))
        .orElseGet(() -> new NodeResult(zdtTimestamp, inputModelUuid, vMagValue, vAngValue));
  }
}
