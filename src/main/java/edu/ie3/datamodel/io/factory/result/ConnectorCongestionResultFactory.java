/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.connector.ConnectorCongestionResult;
import edu.ie3.datamodel.models.result.connector.LineCongestionResult;
import edu.ie3.datamodel.models.result.connector.Transformer2WCongestionResult;
import edu.ie3.datamodel.models.result.connector.Transformer3WCongestionResult;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.measure.quantity.ElectricCurrent;
import tech.units.indriya.ComparableQuantity;

public class ConnectorCongestionResultFactory
    extends ResultEntityFactory<ConnectorCongestionResult> {
  private static final String IMIN = "iMin";
  private static final String IBMIN = "iBMin";
  private static final String ICMIN = "iCMin";

  /** Create a new factory to build {@link ConnectorCongestionResult}s. */
  public ConnectorCongestionResultFactory() {
    super(
        LineCongestionResult.class,
        Transformer2WCongestionResult.class,
        Transformer3WCongestionResult.class);
  }

  /**
   * Create a new factory to build {@link ConnectorCongestionResult}s and utilize the given date
   * time formatter pattern to parse date time strings
   *
   * @param dateTimeFormatter to parse date time strings
   */
  public ConnectorCongestionResultFactory(DateTimeFormatter dateTimeFormatter) {
    super(
        dateTimeFormatter,
        LineCongestionResult.class,
        Transformer2WCongestionResult.class,
        Transformer3WCongestionResult.class);
  }

  @Override
  protected ConnectorCongestionResult buildModel(EntityData data) {
    final Class<? extends Entity> entityClass = data.getTargetClass();
    ZonedDateTime time = timeUtil.toZonedDateTime(data.getField(TIME));
    UUID inputModel = data.getUUID(INPUT_MODEL);

    ComparableQuantity<ElectricCurrent> iMin =
        data.getQuantity(IMIN, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);

    if (entityClass.equals(LineCongestionResult.class)) {
      return new LineCongestionResult(time, inputModel, iMin);
    } else if (entityClass.equals(Transformer2WCongestionResult.class)) {
      return new Transformer2WCongestionResult(time, inputModel, iMin);
    } else if (entityClass.equals(Transformer3WCongestionResult.class)) {
      ComparableQuantity<ElectricCurrent> iBMin =
          data.getQuantity(IBMIN, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);
      ComparableQuantity<ElectricCurrent> iCMin =
          data.getQuantity(ICMIN, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);

      return new Transformer3WCongestionResult(time, inputModel, iMin, iBMin, iCMin);
    } else throw new FactoryException("Cannot process " + entityClass.getSimpleName() + ".class.");
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    Set<String> minConstructorParams = newSet(TIME, INPUT_MODEL, IMIN);

    if (entityClass.equals(Transformer3WCongestionResult.class)) {
      minConstructorParams = newSet(TIME, INPUT_MODEL, IMIN, IBMIN, ICMIN);
    }

    return List.of(minConstructorParams);
  }
}
