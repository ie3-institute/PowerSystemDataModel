/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import static edu.ie3.util.quantities.PowerSystemUnits.PU;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.result.CongestionResult;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import javax.measure.quantity.Dimensionless;
import tech.units.indriya.ComparableQuantity;

public class CongestionResultFactory extends ResultEntityFactory<CongestionResult> {
  private static final String SUBNET = "subnet";
  private static final String VMIN = "vMin";
  private static final String VMAX = "vMax";
  private static final String VOLTAGE = "voltage";
  private static final String LINE = "line";
  private static final String TRANSFORMER = "transformer";

  public CongestionResultFactory() {
    super(CongestionResult.class);
  }

  public CongestionResultFactory(DateTimeFormatter dateTimeFormatter) {
    super(dateTimeFormatter, CongestionResult.class);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    return List.of(newSet(TIME, SUBNET, VMIN, VMAX, VOLTAGE, LINE, TRANSFORMER));
  }

  @Override
  protected CongestionResult buildModel(EntityData data) {
    ZonedDateTime zdtTime = timeUtil.toZonedDateTime(data.getField(TIME));
    int subnet = data.getInt(SUBNET);
    ComparableQuantity<Dimensionless> vMin = data.getQuantity(VMIN, PU);
    ComparableQuantity<Dimensionless> vMax = data.getQuantity(VMAX, PU);
    boolean voltage = data.getBoolean(VOLTAGE);
    boolean line = data.getBoolean(LINE);
    boolean transformer = data.getBoolean(TRANSFORMER);

    return new CongestionResult(zdtTime, subnet, vMin, vMax, voltage, line, transformer);
  }
}
