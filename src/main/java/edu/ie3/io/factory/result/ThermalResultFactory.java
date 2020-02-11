/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.result;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.io.factory.SimpleEntityData;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.result.thermal.CylindricalStorageResult;
import edu.ie3.models.result.thermal.ThermalHouseResult;
import edu.ie3.models.result.thermal.ThermalUnitResult;
import edu.ie3.util.TimeTools;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import javax.measure.quantity.Temperature;
import tec.uom.se.quantity.Quantities;

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

    if (simpleEntityData.getEntityClass().equals(ThermalHouseResult.class)) {
      minConstructorParams = newSet(TIMESTAMP, INPUT_MODEL, Q_DOT, INDOOR_TEMPERATURE);
    } else if (simpleEntityData.getEntityClass().equals(CylindricalStorageResult.class)) {
      minConstructorParams = newSet(TIMESTAMP, INPUT_MODEL, Q_DOT, ENERGY, FILL_LEVEL);
    }

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected ThermalUnitResult buildModel(SimpleEntityData simpleEntityData) {
    Map<String, String> fieldsToValues = simpleEntityData.getFieldsToValues();
    Class<? extends UniqueEntity> clazz = simpleEntityData.getEntityClass();

    ZonedDateTime zdtTimestamp = TimeTools.toZonedDateTime(fieldsToValues.get(TIMESTAMP));
    UUID inputModelUuid = UUID.fromString(fieldsToValues.get(INPUT_MODEL));
    Quantity<Power> qDotQuantity =
        Quantities.getQuantity(
            Double.parseDouble(fieldsToValues.get(Q_DOT)), StandardUnits.HEAT_DEMAND);
    Optional<UUID> uuidOpt =
        fieldsToValues.containsKey(ENTITY_UUID)
            ? Optional.of(UUID.fromString(fieldsToValues.get(ENTITY_UUID)))
            : Optional.empty();

    if (clazz.equals(ThermalHouseResult.class)) {
      Quantity<Temperature> indoorTemperature =
          Quantities.getQuantity(
              Double.parseDouble(fieldsToValues.get(INDOOR_TEMPERATURE)),
              StandardUnits.TEMPERATURE);

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
      Quantity<Energy> energyQuantity =
          Quantities.getQuantity(
              Double.parseDouble(fieldsToValues.get(ENERGY)), StandardUnits.ENERGY_RESULT);
      Quantity<Dimensionless> fillLevelQuantity =
          Quantities.getQuantity(
              Double.parseDouble(fieldsToValues.get(FILL_LEVEL)), StandardUnits.FILL_LEVEL);

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
