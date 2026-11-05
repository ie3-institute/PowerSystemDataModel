/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.factory.FieldMap;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult;
import edu.ie3.datamodel.models.result.thermal.DomesticHotWaterStorageResult;
import edu.ie3.datamodel.models.result.thermal.ThermalHouseResult;
import edu.ie3.datamodel.models.result.thermal.ThermalUnitResult;
import java.time.ZonedDateTime;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;

public class ThermalResultFactory<R extends ThermalUnitResult>
    extends ResultEntityFactory<ThermalUnitResult, R> {

  private final Class<R> targetClass;

  public ThermalResultFactory(Class<R> targetClass) {
    super(
        ThermalHouseResult.class,
        CylindricalStorageResult.class,
        DomesticHotWaterStorageResult.class);
    this.targetClass = targetClass;

    isSupportedClass(targetClass);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected R buildModel(FieldMap data, ZonedDateTime time, UUID inputModel) {
    ComparableQuantity<Power> qDotQuantity = data.getQuantity(Q_DOT, StandardUnits.HEAT_DEMAND);

    if (targetClass.equals(ThermalHouseResult.class)) {
      ComparableQuantity<Temperature> indoorTemperature =
          data.getQuantity(INDOOR_TEMPERATURE, StandardUnits.TEMPERATURE);

      return (R) new ThermalHouseResult(time, inputModel, qDotQuantity, indoorTemperature);
    } else if (targetClass.equals(CylindricalStorageResult.class)) {
      ComparableQuantity<Energy> energyQuantity =
          data.getQuantity(ENERGY, StandardUnits.ENERGY_RESULT);
      ComparableQuantity<Dimensionless> fillLevelQuantity =
          data.getQuantity(FILL_LEVEL, StandardUnits.FILL_LEVEL);

      return (R)
          new CylindricalStorageResult(
              time, inputModel, energyQuantity, qDotQuantity, fillLevelQuantity);
    } else if (targetClass.equals(DomesticHotWaterStorageResult.class)) {
      ComparableQuantity<Energy> energyQuantity =
          data.getQuantity(ENERGY, StandardUnits.ENERGY_RESULT);
      ComparableQuantity<Dimensionless> fillLevelQuantity =
          data.getQuantity(FILL_LEVEL, StandardUnits.FILL_LEVEL);

      return (R)
          new DomesticHotWaterStorageResult(
              time, inputModel, energyQuantity, qDotQuantity, fillLevelQuantity);
    } else {
      throw new FactoryException("Cannot process " + targetClass.getSimpleName() + ".class.");
    }
  }
}
