/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult;
import edu.ie3.datamodel.models.result.thermal.ThermalHouseResult;
import edu.ie3.datamodel.models.result.thermal.ThermalUnitResult;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;

public class ThermalResultFactory extends ResultEntityFactory<ThermalUnitResult> {
  private static final String Q_DOT = "qDot";
  private static final String INDOOR_TEMPERATURE = "indoorTemperature";
  private static final String ENERGY = "energy";
  private static final String FILL_LEVEL = "fillLevel";

  public ThermalResultFactory() {
    super(ThermalHouseResult.class, CylindricalStorageResult.class);
  }

  @Override
  protected List<Set<String>> getFields(SimpleEntityData simpleEntityData) {
    Set<String> minConstructorParams = newSet(TIMESTAMP, INPUT_MODEL, Q_DOT);
    Set<String> optionalFields = expandSet(minConstructorParams, ENTITY_UUID);

    if (simpleEntityData.getTargetClass().equals(ThermalHouseResult.class)) {
      minConstructorParams = newSet(TIMESTAMP, INPUT_MODEL, Q_DOT, INDOOR_TEMPERATURE);
    } else if (simpleEntityData.getTargetClass().equals(CylindricalStorageResult.class)) {
      minConstructorParams = newSet(TIMESTAMP, INPUT_MODEL, Q_DOT, ENERGY, FILL_LEVEL);
    }

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected ThermalUnitResult buildModel(SimpleEntityData data) {
    Class<? extends UniqueEntity> clazz = data.getTargetClass();

    ZonedDateTime zdtTimestamp = TIME_UTIL.toZonedDateTime(data.getField(TIMESTAMP));
    UUID inputModelUuid = data.getUUID(INPUT_MODEL);
    ComparableQuantity<Power> qDotQuantity = data.getQuantity(Q_DOT, StandardUnits.HEAT_DEMAND);
    Optional<UUID> uuidOpt =
        data.containsKey(ENTITY_UUID) ? Optional.of(data.getUUID(ENTITY_UUID)) : Optional.empty();

    if (clazz.equals(ThermalHouseResult.class)) {
      ComparableQuantity<Temperature> indoorTemperature =
          data.getQuantity(INDOOR_TEMPERATURE, StandardUnits.TEMPERATURE);

      return uuidOpt
          .map(
              uuid ->
                  new ThermalHouseResult(
                      uuid, zdtTimestamp, inputModelUuid, qDotQuantity, indoorTemperature))
          .orElseGet(
              () ->
                  new ThermalHouseResult(
                      zdtTimestamp, inputModelUuid, qDotQuantity, indoorTemperature));
    } else if (clazz.equals(CylindricalStorageResult.class)) {
      ComparableQuantity<Energy> energyQuantity =
          data.getQuantity(ENERGY, StandardUnits.ENERGY_RESULT);
      ComparableQuantity<Dimensionless> fillLevelQuantity =
          data.getQuantity(FILL_LEVEL, StandardUnits.FILL_LEVEL);

      return uuidOpt
          .map(
              uuid ->
                  new CylindricalStorageResult(
                      uuid,
                      zdtTimestamp,
                      inputModelUuid,
                      energyQuantity,
                      qDotQuantity,
                      fillLevelQuantity))
          .orElseGet(
              () ->
                  new CylindricalStorageResult(
                      zdtTimestamp,
                      inputModelUuid,
                      energyQuantity,
                      qDotQuantity,
                      fillLevelQuantity));
    } else {
      throw new FactoryException("Cannot process " + clazz.getSimpleName() + ".class.");
    }
  }
}
