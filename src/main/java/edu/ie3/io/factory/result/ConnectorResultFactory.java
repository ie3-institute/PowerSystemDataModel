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
import edu.ie3.models.result.connector.*;
import edu.ie3.util.TimeTools;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;

public class ConnectorResultFactory extends ResultEntityFactory<ConnectorResult> {

  private static final String IAMAG = "iamag";
  private static final String IAANG = "iaang";
  private static final String IBMAG = "ibmag";
  private static final String IBANG = "ibang";
  private static final String ICMAG = "icmag";
  private static final String ICANG = "icang";
  private static final String CLOSED = "closed";
  private static final String TAPPOS = "tappos";

  public ConnectorResultFactory() {
    super(
        LineResult.class, SwitchResult.class, Transformer2WResult.class, Transformer3WResult.class);
  }

  @Override
  protected List<Set<String>> getFields(SimpleEntityData simpleEntityData) {
    /// all result models have the same constructor except StorageResult
    Set<String> minConstructorParams = newSet(TIMESTAMP, INPUT_MODEL, IAMAG, IAANG, IBMAG, IBANG);
    Set<String> optionalFields = expandSet(minConstructorParams, ENTITY_UUID);

    final Class<? extends UniqueEntity> entityClass = simpleEntityData.getEntityClass();
    if (entityClass.equals(SwitchResult.class)) {
      minConstructorParams = newSet(TIMESTAMP, INPUT_MODEL, IAMAG, IAANG, IBMAG, IBANG, CLOSED);
      optionalFields = expandSet(minConstructorParams, ENTITY_UUID);
    } else if (entityClass.equals(Transformer2WResult.class)) {
      minConstructorParams = newSet(TIMESTAMP, INPUT_MODEL, IAMAG, IAANG, IBMAG, IBANG, TAPPOS);
      optionalFields = expandSet(minConstructorParams, ENTITY_UUID);
    } else if (entityClass.equals(Transformer3WResult.class)) {
      minConstructorParams =
          newSet(TIMESTAMP, INPUT_MODEL, IAMAG, IAANG, IBMAG, IBANG, ICMAG, ICANG, TAPPOS);
      optionalFields = expandSet(minConstructorParams, ENTITY_UUID);
    }

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected ConnectorResult buildModel(SimpleEntityData data) {
    final Class<? extends UniqueEntity> entityClass = data.getEntityClass();

    ZonedDateTime zdtTimestamp = TimeTools.toZonedDateTime(data.get(TIMESTAMP));
    UUID inputModelUuid = data.getUUID(INPUT_MODEL);
    Quantity<ElectricCurrent> iAMagVal = data.get(IAMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);
    Quantity<Angle> iAAngVal = data.get(IAANG, StandardUnits.ELECTRIC_CURRENT_ANGLE);
    Quantity<ElectricCurrent> iBMagVal = data.get(IBMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);
    Quantity<Angle> iBAngVal = data.get(IBANG, StandardUnits.ELECTRIC_CURRENT_ANGLE);

    Optional<UUID> uuidOpt =
        data.containsKey(ENTITY_UUID) ? Optional.of(data.getUUID(ENTITY_UUID)) : Optional.empty();

    if (entityClass.equals(LineResult.class))
      return uuidOpt
          .map(
              uuid ->
                  new LineResult(
                      uuid, zdtTimestamp, inputModelUuid, iAMagVal, iAAngVal, iBMagVal, iBAngVal))
          .orElseGet(
              () ->
                  new LineResult(
                      zdtTimestamp, inputModelUuid, iAMagVal, iAAngVal, iBMagVal, iBAngVal));
    else if (entityClass.equals(SwitchResult.class)) {
      final boolean closedVal =
          data.get(CLOSED).trim().equals("1") || data.get(CLOSED).trim().equals("true");

      return uuidOpt
          .map(
              uuid ->
                  new SwitchResult(
                      uuid,
                      zdtTimestamp,
                      inputModelUuid,
                      iAMagVal,
                      iAAngVal,
                      iBMagVal,
                      iBAngVal,
                      closedVal))
          .orElseGet(
              () ->
                  new SwitchResult(
                      zdtTimestamp,
                      inputModelUuid,
                      iAMagVal,
                      iAAngVal,
                      iBMagVal,
                      iBAngVal,
                      closedVal));
    } else if (entityClass.equals(Transformer2WResult.class)) {
      final int tapPosValue = Integer.parseInt(data.get(TAPPOS).trim());

      return uuidOpt
          .map(
              uuid ->
                  new Transformer2WResult(
                      uuid,
                      zdtTimestamp,
                      inputModelUuid,
                      iAMagVal,
                      iAAngVal,
                      iBMagVal,
                      iBAngVal,
                      tapPosValue))
          .orElseGet(
              () ->
                  new Transformer2WResult(
                      zdtTimestamp,
                      inputModelUuid,
                      iAMagVal,
                      iAAngVal,
                      iBMagVal,
                      iBAngVal,
                      tapPosValue));
    } else if (entityClass.equals(Transformer3WResult.class)) {
      Quantity<ElectricCurrent> iCMagVal =
          data.get(ICMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);
      Quantity<Angle> iCAngVal = data.get(ICANG, StandardUnits.ELECTRIC_CURRENT_ANGLE);
      final int tapPosValue = Integer.parseInt(data.get(TAPPOS).trim());

      return uuidOpt
          .map(
              uuid ->
                  new Transformer3WResult(
                      uuid,
                      zdtTimestamp,
                      inputModelUuid,
                      iAMagVal,
                      iAAngVal,
                      iBMagVal,
                      iBAngVal,
                      iCMagVal,
                      iCAngVal,
                      tapPosValue))
          .orElseGet(
              () ->
                  new Transformer3WResult(
                      zdtTimestamp,
                      inputModelUuid,
                      iAMagVal,
                      iAAngVal,
                      iBMagVal,
                      iBAngVal,
                      iCMagVal,
                      iCAngVal,
                      tapPosValue));
    } else throw new FactoryException("Cannot process " + entityClass.getSimpleName() + ".class.");
  }
}
