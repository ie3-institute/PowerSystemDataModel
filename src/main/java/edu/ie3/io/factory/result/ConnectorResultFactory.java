/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.result;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.io.factory.EntityData;
import edu.ie3.io.factory.SimpleEntityFactory;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.result.connector.*;
import edu.ie3.util.TimeTools;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;
import java.time.ZonedDateTime;
import java.util.*;

public class ConnectorResultFactory extends SimpleEntityFactory<ConnectorResult> {
  private static final String entityUuid = "uuid";
  private static final String timestamp = "timestamp";
  private static final String inputModel = "inputModel";
  private static final String iAMag = "iamag";
  private static final String iAAng = "iaang";
  private static final String iBMag = "ibmag";
  private static final String iBAng = "ibang";
  private static final String iCMag = "icmag";
  private static final String iCAng = "icang";
  private static final String closed = "closed";
  private static final String tapPos = "tappos";

  public ConnectorResultFactory() {
    super(
        LineResult.class, SwitchResult.class, Transformer2WResult.class, Transformer3WResult.class);
  }

  @Override
  protected List<Set<String>> getFields(EntityData entityData) {
    /// all result models have the same constructor except StorageResult
    Set<String> minConstructorParams = newSet(timestamp, inputModel, iAMag, iAAng, iBMag, iBAng);
    Set<String> optionalFields = expandSet(minConstructorParams, entityUuid);

    if (entityData.getEntityClass().equals(SwitchResult.class)) {
      minConstructorParams = newSet(timestamp, inputModel, iAMag, iAAng, iBMag, iBAng, closed);
      optionalFields = expandSet(minConstructorParams, entityUuid);
    } else if (entityData.getEntityClass().equals(Transformer2WResult.class)) {
      minConstructorParams = newSet(timestamp, inputModel, iAMag, iAAng, iBMag, iBAng, tapPos);
      optionalFields = expandSet(minConstructorParams, entityUuid);
    } else if (entityData.getEntityClass().equals(Transformer3WResult.class)) {
      minConstructorParams =
          newSet(timestamp, inputModel, iAMag, iAAng, iBMag, iBAng, iCMag, iCAng, tapPos);
      optionalFields = expandSet(minConstructorParams, entityUuid);
    }

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected ConnectorResult buildModel(EntityData simpleEntityData) {
    Map<String, String> fieldsToValues = simpleEntityData.getFieldsToValues();
    Class<? extends UniqueEntity> clazz = simpleEntityData.getEntityClass();

    ZonedDateTime zdtTimestamp = TimeTools.toZonedDateTime(fieldsToValues.get(timestamp));
    UUID inputModelUuid = UUID.fromString(fieldsToValues.get(inputModel));
    Quantity<ElectricCurrent> iAMagVal =
        Quantities.getQuantity(
            Double.parseDouble(fieldsToValues.get(iAMag)), StandardUnits.CURRENT);
    Quantity<Angle> iAAngVal =
        Quantities.getQuantity(
            Double.parseDouble(fieldsToValues.get(iAAng)), StandardUnits.DPHI_TAP); // TODO
    Quantity<ElectricCurrent> iBMagVal =
        Quantities.getQuantity(
            Double.parseDouble(fieldsToValues.get(iBMag)), StandardUnits.CURRENT);
    Quantity<Angle> iBAngVal =
        Quantities.getQuantity(
            Double.parseDouble(fieldsToValues.get(iBAng)), StandardUnits.DPHI_TAP); // TODO
    Optional<UUID> uuidOpt =
        fieldsToValues.containsKey(entityUuid)
            ? Optional.of(UUID.fromString(fieldsToValues.get(entityUuid)))
            : Optional.empty();

    if (clazz.equals(LineResult.class)) {
      return uuidOpt
          .map(
              uuid ->
                  new LineResult(
                      uuid, zdtTimestamp, inputModelUuid, iAMagVal, iAAngVal, iBMagVal, iBAngVal))
          .orElseGet(
              () ->
                  new LineResult(
                      zdtTimestamp, inputModelUuid, iAMagVal, iAAngVal, iBMagVal, iBAngVal));
    } else if (clazz.equals(SwitchResult.class)) {
      final boolean closedVal = fieldsToValues.get(closed).trim().equals("1");

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
    } else if (clazz.equals(Transformer2WResult.class)) {
      final int tapPosValue = Integer.parseInt(fieldsToValues.get(tapPos).trim());

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
    } else if (clazz.equals(Transformer3WResult.class)) {
      Quantity<ElectricCurrent> iCMagVal =
          Quantities.getQuantity(
              Double.parseDouble(fieldsToValues.get(iCMag)), StandardUnits.CURRENT);
      Quantity<Angle> iCAngVal =
          Quantities.getQuantity(
              Double.parseDouble(fieldsToValues.get(iCAng)), StandardUnits.DPHI_TAP); // TODO
      final int tapPosValue = Integer.parseInt(fieldsToValues.get(tapPos).trim());

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
    } else {
      throw new FactoryException("Cannot process " + clazz.getSimpleName() + ".class.");
    }
  }
}
