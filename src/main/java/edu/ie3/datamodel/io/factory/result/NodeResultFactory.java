/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.NodeResult;
import edu.ie3.util.TimeTools;
import tec.uom.se.ComparableQuantity;

import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;

public class NodeResultFactory extends ResultEntityFactory<NodeResult> {
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
  protected NodeResult buildModel(SimpleEntityData data) {
    ZonedDateTime zdtTimestamp = TimeTools.toZonedDateTime(data.getField(TIMESTAMP));
    UUID inputModelUuid = data.getUUID(INPUT_MODEL);
    ComparableQuantity<Dimensionless> vMagValue = data.getQuantity(VMAG, StandardUnits.VOLTAGE_MAGNITUDE); // TODO doublecheck
    ComparableQuantity<Angle> vAngValue = data.getQuantity(VANG, StandardUnits.VOLTAGE_ANGLE); // TODO doublecheck
    Optional<UUID> uuidOpt =
        data.containsKey(ENTITY_UUID) ? Optional.of(data.getUUID(ENTITY_UUID)) : Optional.empty();

    return uuidOpt
        .map(uuid -> new NodeResult(uuid, zdtTimestamp, inputModelUuid, vMagValue, vAngValue))
        .orElseGet(() -> new NodeResult(zdtTimestamp, inputModelUuid, vMagValue, vAngValue));
  }
}
