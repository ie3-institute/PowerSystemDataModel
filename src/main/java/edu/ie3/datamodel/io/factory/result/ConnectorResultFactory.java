/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.connector.ConnectorResult;
import edu.ie3.datamodel.models.result.connector.LineResult;
import edu.ie3.datamodel.models.result.connector.Transformer2WResult;
import edu.ie3.datamodel.models.result.connector.Transformer3WResult;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;
import tech.units.indriya.ComparableQuantity;

public class ConnectorResultFactory extends ResultEntityFactory<ConnectorResult> {

  private static final String IAMAG = "iAMag";
  private static final String IAANG = "iAAng";
  private static final String IBMAG = "iBMag";
  private static final String IBANG = "iBAng";
  private static final String ICMAG = "iCMag";
  private static final String ICANG = "iCAng";
  private static final String TAPPOS = "tapPos";

  public ConnectorResultFactory() {
    super(LineResult.class, Transformer2WResult.class, Transformer3WResult.class);
  }

  /**
   * Create a new factory to build {@link ConnectorResult}s and utilize the given date time
   * formatter pattern to parse date time strings
   *
   * @param dtfPattern Pattern to parse date time strings
   */
  public ConnectorResultFactory(String dtfPattern) {
    super(dtfPattern, LineResult.class, Transformer2WResult.class, Transformer3WResult.class);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    /// all result models have the same constructor except StorageResult
    Set<String> minConstructorParams = newSet(TIME, INPUT_MODEL, IAMAG, IAANG, IBMAG, IBANG);

    if (entityClass.equals(Transformer2WResult.class)) {
      minConstructorParams = newSet(TIME, INPUT_MODEL, IAMAG, IAANG, IBMAG, IBANG, TAPPOS);
    } else if (entityClass.equals(Transformer3WResult.class)) {
      minConstructorParams =
          newSet(TIME, INPUT_MODEL, IAMAG, IAANG, IBMAG, IBANG, ICMAG, ICANG, TAPPOS);
    }

    return List.of(minConstructorParams);
  }

  @Override
  protected ConnectorResult buildModel(EntityData data) {
    final Class<? extends Entity> entityClass = data.getTargetClass();
    ZonedDateTime time = timeUtil.toZonedDateTime(data.getField(TIME));

    UUID inputModel = data.getUUID(INPUT_MODEL);
    ComparableQuantity<ElectricCurrent> iAMag =
        data.getQuantity(IAMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);
    ComparableQuantity<Angle> iAAng = data.getQuantity(IAANG, StandardUnits.ELECTRIC_CURRENT_ANGLE);
    ComparableQuantity<ElectricCurrent> iBMag =
        data.getQuantity(IBMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);
    ComparableQuantity<Angle> iBAng = data.getQuantity(IBANG, StandardUnits.ELECTRIC_CURRENT_ANGLE);

    if (entityClass.equals(LineResult.class))
      return new LineResult(time, inputModel, iAMag, iAAng, iBMag, iBAng);
    else if (entityClass.equals(Transformer2WResult.class)) {
      final int tapPos = data.getInt(TAPPOS);

      return new Transformer2WResult(time, inputModel, iAMag, iAAng, iBMag, iBAng, tapPos);
    } else if (entityClass.equals(Transformer3WResult.class)) {
      ComparableQuantity<ElectricCurrent> iCMag =
          data.getQuantity(ICMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);
      ComparableQuantity<Angle> iCAng =
          data.getQuantity(ICANG, StandardUnits.ELECTRIC_CURRENT_ANGLE);
      final int tapPos = data.getInt(TAPPOS);

      return new Transformer3WResult(
          time, inputModel, iAMag, iAAng, iBMag, iBAng, iCMag, iCAng, tapPos);
    } else throw new FactoryException("Cannot process " + entityClass.getSimpleName() + ".class.");
  }
}
