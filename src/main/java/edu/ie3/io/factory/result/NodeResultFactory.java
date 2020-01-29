/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.result;

import edu.ie3.io.factory.EntityData;
import edu.ie3.io.factory.EntityFactoryImpl;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.result.NodeResult;
import edu.ie3.util.TimeTools;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import tec.uom.se.quantity.Quantities;

public class NodeResultFactory extends EntityFactoryImpl<NodeResult> {
  private static final String entityUuid = "uuid";
  private static final String timestamp = "timestamp";
  private static final String inputModel = "inputModel";
  private static final String vMag = "vmag";
  private static final String vAng = "vang";

  public NodeResultFactory() {
    super(NodeResult.class);
  }

  @Override
  protected List<Set<String>> getFields(EntityData entityData) {
    Set<String> minConstructorParams = newSet(timestamp, inputModel, vMag, vAng);
    Set<String> optionalFields = enhanceSet(minConstructorParams, entityUuid);

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected NodeResult buildModel(EntityData simpleEntityData) {
    Map<String, String> fieldsToValues = simpleEntityData.getFieldsToValues();

    ZonedDateTime zdtTimestamp = TimeTools.toZonedDateTime(fieldsToValues.get(timestamp));
    UUID inputModelUuid = UUID.fromString(fieldsToValues.get(inputModel));
    Quantity<Dimensionless> vMagValue =
        Quantities.getQuantity(
            Double.parseDouble(fieldsToValues.get(vMag)), StandardUnits.TARGET_VOLTAGE); // TODO
    Quantity<Angle> vAngValue =
        Quantities.getQuantity(
            Double.parseDouble(fieldsToValues.get(vAng)), StandardUnits.DPHI_TAP); // TODO
    Optional<UUID> uuidOpt =
        fieldsToValues.containsKey(entityUuid)
            ? Optional.of(UUID.fromString(fieldsToValues.get(entityUuid)))
            : Optional.empty();

    return uuidOpt
        .map(uuid -> new NodeResult(uuid, zdtTimestamp, inputModelUuid, vMagValue, vAngValue))
        .orElseGet(() -> new NodeResult(zdtTimestamp, inputModelUuid, vMagValue, vAngValue));
  }
}
