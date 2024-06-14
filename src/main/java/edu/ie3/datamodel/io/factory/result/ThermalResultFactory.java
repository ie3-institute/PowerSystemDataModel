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
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult;
import edu.ie3.datamodel.models.result.thermal.ThermalHouseResult;
import edu.ie3.datamodel.models.result.thermal.ThermalUnitResult;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;

public class ThermalResultFactory extends ModelResultFactory<ThermalUnitResult> {
  private static final String Q_DOT = "qDot";
  private static final String INDOOR_TEMPERATURE = "indoorTemperature";
  private static final String ENERGY = "energy";
  private static final String FILL_LEVEL = "fillLevel";

  public ThermalResultFactory() {
    super(ThermalHouseResult.class, CylindricalStorageResult.class);
  }

  /**
   * Create a new factory to build {@link ThermalResultFactory}s and utilize the given date time
   * formatter pattern to parse date time strings
   *
   * @param dateTimeFormatter parse date time strings
   */
  public ThermalResultFactory(DateTimeFormatter dateTimeFormatter) {
    super(dateTimeFormatter, ThermalHouseResult.class, CylindricalStorageResult.class);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    Set<String> minConstructorParams = newSet(TIME, INPUT_MODEL, Q_DOT);

    if (entityClass.equals(ThermalHouseResult.class)) {
      minConstructorParams = newSet(TIME, INPUT_MODEL, Q_DOT, INDOOR_TEMPERATURE);
    } else if (entityClass.equals(CylindricalStorageResult.class)) {
      minConstructorParams = newSet(TIME, INPUT_MODEL, Q_DOT, ENERGY, FILL_LEVEL);
    }

    return List.of(minConstructorParams);
  }

  @Override
  protected ThermalUnitResult buildModel(EntityData data) {
    Class<? extends Entity> clazz = data.getTargetClass();

    ZonedDateTime zdtTime = timeUtil.toZonedDateTime(data.getField(TIME));
    UUID inputModelUuid = data.getUUID(INPUT_MODEL);
    ComparableQuantity<Power> qDotQuantity = data.getQuantity(Q_DOT, StandardUnits.HEAT_DEMAND);

    if (clazz.equals(ThermalHouseResult.class)) {
      ComparableQuantity<Temperature> indoorTemperature =
          data.getQuantity(INDOOR_TEMPERATURE, StandardUnits.TEMPERATURE);

      return new ThermalHouseResult(zdtTime, inputModelUuid, qDotQuantity, indoorTemperature);
    } else if (clazz.equals(CylindricalStorageResult.class)) {
      ComparableQuantity<Energy> energyQuantity =
          data.getQuantity(ENERGY, StandardUnits.ENERGY_RESULT);
      ComparableQuantity<Dimensionless> fillLevelQuantity =
          data.getQuantity(FILL_LEVEL, StandardUnits.FILL_LEVEL);

      return new CylindricalStorageResult(
          zdtTime, inputModelUuid, energyQuantity, qDotQuantity, fillLevelQuantity);
    } else {
      throw new FactoryException("Cannot process " + clazz.getSimpleName() + ".class.");
    }
  }
}
