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
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import tech.units.indriya.ComparableQuantity;

public class NodeResultFactory extends ResultEntityFactory<NodeResult> {
  private static final String VMAG = "vMag";
  private static final String VANG = "vAng";

  public NodeResultFactory() {
    super(NodeResult.class);
  }

  /**
   * Create a new factory to build {@link NodeResult}s and utilize the given date time formatter
   * pattern to parse date time strings
   *
   * @param dateTimeFormatter to parse date time strings
   */
  public NodeResultFactory(DateTimeFormatter dateTimeFormatter) {
    super(dateTimeFormatter, NodeResult.class);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    Set<String> minConstructorParams = newSet(TIME, INPUT_MODEL, VMAG, VANG);
    return List.of(minConstructorParams);
  }

  @Override
  protected NodeResult buildModel(EntityData data) {
    ZonedDateTime zdtTime = timeUtil.toZonedDateTime(data.getField(TIME));
    UUID inputModelUuid = data.getUUID(INPUT_MODEL);
    ComparableQuantity<Dimensionless> vMagValue =
        data.getQuantity(VMAG, StandardUnits.VOLTAGE_MAGNITUDE);
    ComparableQuantity<Angle> vAngValue = data.getQuantity(VANG, StandardUnits.VOLTAGE_ANGLE);

    return new NodeResult(zdtTime, inputModelUuid, vMagValue, vAngValue);
  }
}
