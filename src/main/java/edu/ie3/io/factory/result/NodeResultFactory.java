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

public class NodeResultFactory extends SimpleEntityFactory<NodeResult> {
  private static final String entityUuid = "uuid";
  private static final String timestamp = "timestamp";
  private static final String inputModel = "inputModel";
  private static final String vMag = "vmag";
  private static final String vAng = "vang";

  public NodeResultFactory() {
    super(NodeResult.class);
  }

  @Override
  protected List<Set<String>> getFields(SimpleEntityData entityData) {
    Set<String> minConstructorParams = newSet(timestamp, inputModel, vMag, vAng);
    Set<String> optionalFields = expandSet(minConstructorParams, entityUuid);

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected NodeResult buildModel(SimpleEntityData data) {
    ZonedDateTime zdtTimestamp = TimeTools.toZonedDateTime(data.get(timestamp));
    UUID inputModelUuid = data.getUUID(inputModel);
    Quantity<Dimensionless> vMagValue = data.get(vMag, StandardUnits.TARGET_VOLTAGE); // TODO
    Quantity<Angle> vAngValue = data.get(vAng, StandardUnits.DPHI_TAP); // TODO
    Optional<UUID> uuidOpt =
        data.containsKey(entityUuid)
            ? Optional.of(data.getUUID(entityUuid))
            : Optional.empty();

    return uuidOpt
        .map(uuid -> new NodeResult(uuid, zdtTimestamp, inputModelUuid, vMagValue, vAngValue))
        .orElseGet(() -> new NodeResult(zdtTimestamp, inputModelUuid, vMagValue, vAngValue));
  }
}
