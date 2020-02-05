/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.result;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.io.factory.SimpleEntityData;
import edu.ie3.io.factory.SimpleEntityFactory;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.result.connector.*;
import edu.ie3.util.TimeTools;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;

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
  protected List<Set<String>> getFields(SimpleEntityData simpleEntityData) {
    /// all result models have the same constructor except StorageResult
    Set<String> minConstructorParams = newSet(timestamp, inputModel, iAMag, iAAng, iBMag, iBAng);
    Set<String> optionalFields = expandSet(minConstructorParams, entityUuid);

    final Class<? extends UniqueEntity> entityClass = simpleEntityData.getEntityClass();
    if (entityClass.equals(SwitchResult.class)) {
      minConstructorParams = newSet(timestamp, inputModel, iAMag, iAAng, iBMag, iBAng, closed);
      optionalFields = expandSet(minConstructorParams, entityUuid);
    } else if (entityClass.equals(Transformer2WResult.class)) {
      minConstructorParams = newSet(timestamp, inputModel, iAMag, iAAng, iBMag, iBAng, tapPos);
      optionalFields = expandSet(minConstructorParams, entityUuid);
    } else if (entityClass.equals(Transformer3WResult.class)) {
      minConstructorParams =
          newSet(timestamp, inputModel, iAMag, iAAng, iBMag, iBAng, iCMag, iCAng, tapPos);
      optionalFields = expandSet(minConstructorParams, entityUuid);
    }

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected ConnectorResult buildModel(SimpleEntityData data) {
    final Class<? extends UniqueEntity> entityClass = data.getEntityClass();

    ZonedDateTime zdtTimestamp = TimeTools.toZonedDateTime(data.get(timestamp));
    UUID inputModelUuid = data.getUUID(inputModel);
    Quantity<ElectricCurrent> iAMagVal = data.get(iAMag, StandardUnits.CURRENT);
    Quantity<Angle> iAAngVal = data.get(iAAng, StandardUnits.DPHI_TAP); // TODO
    Quantity<ElectricCurrent> iBMagVal = data.get(iBMag, StandardUnits.CURRENT);
    Quantity<Angle> iBAngVal = data.get(iBAng, StandardUnits.DPHI_TAP); // TODO

    Optional<UUID> uuidOpt =
        data.containsKey(entityUuid) ? Optional.of(data.getUUID(entityUuid)) : Optional.empty();

    if (entityClass.equals(LineResult.class))
      return buildLineResult(
          zdtTimestamp, inputModelUuid, iAMagVal, iAAngVal, iBMagVal, iBAngVal, uuidOpt);
    else if (entityClass.equals(SwitchResult.class))
      return buildSwitchResult(
          data, zdtTimestamp, inputModelUuid, iAMagVal, iAAngVal, iBMagVal, iBAngVal, uuidOpt);
    else if (entityClass.equals(Transformer2WResult.class))
      return buildTransformer2WResult(
          data, zdtTimestamp, inputModelUuid, iAMagVal, iAAngVal, iBMagVal, iBAngVal, uuidOpt);
    else if (entityClass.equals(Transformer3WResult.class))
      return buildTransformer3WResult(
          data, zdtTimestamp, inputModelUuid, iAMagVal, iAAngVal, iBMagVal, iBAngVal, uuidOpt);
    else throw new FactoryException("Cannot process " + entityClass.getSimpleName() + ".class.");
  }

  private ConnectorResult buildLineResult(
      ZonedDateTime zdtTimestamp,
      UUID inputModelUuid,
      Quantity<ElectricCurrent> iAMagVal,
      Quantity<Angle> iAAngVal,
      Quantity<ElectricCurrent> iBMagVal,
      Quantity<Angle> iBAngVal,
      Optional<UUID> uuidOpt) {
    return uuidOpt
        .map(
            uuid ->
                new LineResult(
                    uuid, zdtTimestamp, inputModelUuid, iAMagVal, iAAngVal, iBMagVal, iBAngVal))
        .orElseGet(
            () ->
                new LineResult(
                    zdtTimestamp, inputModelUuid, iAMagVal, iAAngVal, iBMagVal, iBAngVal));
  }

  private ConnectorResult buildSwitchResult(
      SimpleEntityData data,
      ZonedDateTime zdtTimestamp,
      UUID inputModelUuid,
      Quantity<ElectricCurrent> iAMagVal,
      Quantity<Angle> iAAngVal,
      Quantity<ElectricCurrent> iBMagVal,
      Quantity<Angle> iBAngVal,
      Optional<UUID> uuidOpt) {
    final boolean closedVal =
        data.get(closed).trim().equals("1") || data.get(closed).trim().equals("true");

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
  }

  private ConnectorResult buildTransformer2WResult(
      SimpleEntityData data,
      ZonedDateTime zdtTimestamp,
      UUID inputModelUuid,
      Quantity<ElectricCurrent> iAMagVal,
      Quantity<Angle> iAAngVal,
      Quantity<ElectricCurrent> iBMagVal,
      Quantity<Angle> iBAngVal,
      Optional<UUID> uuidOpt) {
    final int tapPosValue = Integer.parseInt(data.get(tapPos).trim());

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
  }

  private ConnectorResult buildTransformer3WResult(
      SimpleEntityData data,
      ZonedDateTime zdtTimestamp,
      UUID inputModelUuid,
      Quantity<ElectricCurrent> iAMagVal,
      Quantity<Angle> iAAngVal,
      Quantity<ElectricCurrent> iBMagVal,
      Quantity<Angle> iBAngVal,
      Optional<UUID> uuidOpt) {
    Quantity<ElectricCurrent> iCMagVal = data.get(iCMag, StandardUnits.CURRENT);
    Quantity<Angle> iCAngVal = data.get(iCAng, StandardUnits.DPHI_TAP); // TODO
    final int tapPosValue = Integer.parseInt(data.get(tapPos).trim());

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
  }
}
