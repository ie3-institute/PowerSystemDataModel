/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.connector.ConnectorResult;
import edu.ie3.datamodel.models.result.connector.LineResult;
import edu.ie3.datamodel.models.result.connector.Transformer2WResult;
import edu.ie3.datamodel.models.result.connector.Transformer3WResult;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;
import tech.units.indriya.ComparableQuantity;

public class ConnectorResultFactory<R extends ConnectorResult>
    extends ResultEntityFactory<ConnectorResult, R> {

  private final Class<R> targetClass;

  public ConnectorResultFactory(Class<R> targetClass) {
    super(LineResult.class, Transformer2WResult.class, Transformer3WResult.class);
    this.targetClass = targetClass;

    isSupportedClass(targetClass);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected R buildModel(Map<String, String> data, ZonedDateTime time, UUID inputModel) {
    ComparableQuantity<ElectricCurrent> iAMag =
        getQuantity(data, IAMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);
    ComparableQuantity<Angle> iAAng =
        getQuantity(data, IAANG, StandardUnits.ELECTRIC_CURRENT_ANGLE);
    ComparableQuantity<ElectricCurrent> iBMag =
        getQuantity(data, IBMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);
    ComparableQuantity<Angle> iBAng =
        getQuantity(data, IBANG, StandardUnits.ELECTRIC_CURRENT_ANGLE);

    if (targetClass.equals(LineResult.class))
      return (R) new LineResult(time, inputModel, iAMag, iAAng, iBMag, iBAng);
    else if (targetClass.equals(Transformer2WResult.class)) {
      int tapPos = getInt(data, TAP_POS);

      return (R) new Transformer2WResult(time, inputModel, iAMag, iAAng, iBMag, iBAng, tapPos);
    } else if (targetClass.equals(Transformer3WResult.class)) {
      ComparableQuantity<ElectricCurrent> iCMag =
          getQuantity(data, ICMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);
      ComparableQuantity<Angle> iCAng =
          getQuantity(data, ICANG, StandardUnits.ELECTRIC_CURRENT_ANGLE);
      int tapPos = getInt(data, TAP_POS);

      return (R)
          new Transformer3WResult(
              time, inputModel, iAMag, iAAng, iBMag, iBAng, iCMag, iCAng, tapPos);
    } else throw new FactoryException("Cannot process " + targetClass.getSimpleName() + ".class.");
  }
}
