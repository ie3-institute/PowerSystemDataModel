/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.NodeResult;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import tech.units.indriya.ComparableQuantity;

public class NodeResultFactory extends ResultEntityFactory<NodeResult> {
  private static final String VMAG = "vmag";
  private static final String VANG = "vang";

  public NodeResultFactory() {
    super(NodeResult.class);
  }

  /**
   * Create a new factory to build {@link NodeResult}s and utilize the given date time formatter
   * pattern to parse date time strings
   *
   * @param dtfPattern Pattern to parse date time strings
   */
  public NodeResultFactory(String dtfPattern) {
    super(dtfPattern, NodeResult.class);
  }

  @Override
  protected List<Set<String>> getFields(EntityData entityData) {
    Set<String> minConstructorParams = newSet(TIME, INPUT_MODEL, VMAG, VANG);
    Set<String> optionalFields = expandSet(minConstructorParams, ENTITY_UUID);

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected NodeResult buildModel(EntityData data) {
    ZonedDateTime zdtTime = timeUtil.toZonedDateTime(data.getField(TIME));
    UUID inputModelUuid = data.getUUID(INPUT_MODEL);
    ComparableQuantity<Dimensionless> vMagValue =
        data.getQuantity(VMAG, StandardUnits.VOLTAGE_MAGNITUDE);
    ComparableQuantity<Angle> vAngValue = data.getQuantity(VANG, StandardUnits.VOLTAGE_ANGLE);
    Optional<UUID> uuidOpt =
        data.containsKey(ENTITY_UUID) ? Optional.of(data.getUUID(ENTITY_UUID)) : Optional.empty();

    return uuidOpt
        .map(uuid -> new NodeResult(uuid, zdtTime, inputModelUuid, vMagValue, vAngValue))
        .orElseGet(() -> new NodeResult(zdtTime, inputModelUuid, vMagValue, vAngValue));
  }
}
