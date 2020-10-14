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
import edu.ie3.datamodel.models.result.connector.ConnectorResult;
import edu.ie3.datamodel.models.result.connector.LineResult;
import edu.ie3.datamodel.models.result.connector.Transformer2WResult;
import edu.ie3.datamodel.models.result.connector.Transformer3WResult;
import edu.ie3.util.TimeTools;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;
import tech.units.indriya.ComparableQuantity;

public class ConnectorResultFactory extends ResultEntityFactory<ConnectorResult> {

  private static final String IAMAG = "iamag";
  private static final String IAANG = "iaang";
  private static final String IBMAG = "ibmag";
  private static final String IBANG = "ibang";
  private static final String ICMAG = "icmag";
  private static final String ICANG = "icang";
  private static final String TAPPOS = "tappos";

  public ConnectorResultFactory() {
    super(LineResult.class, Transformer2WResult.class, Transformer3WResult.class);
  }

  @Override
  protected List<Set<String>> getFields(SimpleEntityData simpleEntityData) {
    /// all result models have the same constructor except StorageResult
    Set<String> minConstructorParams = newSet(TIMESTAMP, INPUT_MODEL, IAMAG, IAANG, IBMAG, IBANG);
    Set<String> optionalFields = expandSet(minConstructorParams, ENTITY_UUID);

    final Class<? extends UniqueEntity> entityClass = simpleEntityData.getEntityClass();
    if (entityClass.equals(Transformer2WResult.class)) {
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

    ZonedDateTime timestamp = TimeTools.toZonedDateTime(data.getField(TIMESTAMP));
    UUID inputModel = data.getUUID(INPUT_MODEL);
    ComparableQuantity<ElectricCurrent> iAMag =
        data.getQuantity(IAMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);
    ComparableQuantity<Angle> iAAng = data.getQuantity(IAANG, StandardUnits.ELECTRIC_CURRENT_ANGLE);
    ComparableQuantity<ElectricCurrent> iBMag =
        data.getQuantity(IBMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);
    ComparableQuantity<Angle> iBAng = data.getQuantity(IBANG, StandardUnits.ELECTRIC_CURRENT_ANGLE);

    Optional<UUID> uuidOpt =
        data.containsKey(ENTITY_UUID) ? Optional.of(data.getUUID(ENTITY_UUID)) : Optional.empty();

    if (entityClass.equals(LineResult.class))
      return uuidOpt
          .map(uuid -> new LineResult(uuid, timestamp, inputModel, iAMag, iAAng, iBMag, iBAng))
          .orElseGet(() -> new LineResult(timestamp, inputModel, iAMag, iAAng, iBMag, iBAng));
    else if (entityClass.equals(Transformer2WResult.class)) {
      final int tapPos = data.getInt(TAPPOS);

      return uuidOpt
          .map(
              uuid ->
                  new Transformer2WResult(
                      uuid, timestamp, inputModel, iAMag, iAAng, iBMag, iBAng, tapPos))
          .orElseGet(
              () ->
                  new Transformer2WResult(
                      timestamp, inputModel, iAMag, iAAng, iBMag, iBAng, tapPos));
    } else if (entityClass.equals(Transformer3WResult.class)) {
      ComparableQuantity<ElectricCurrent> iCMag =
          data.getQuantity(ICMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);
      ComparableQuantity<Angle> iCAng =
          data.getQuantity(ICANG, StandardUnits.ELECTRIC_CURRENT_ANGLE);
      final int tapPos = data.getInt(TAPPOS);

      return uuidOpt
          .map(
              uuid ->
                  new Transformer3WResult(
                      uuid,
                      timestamp,
                      inputModel,
                      iAMag,
                      iAAng,
                      iBMag,
                      iBAng,
                      iCMag,
                      iCAng,
                      tapPos))
          .orElseGet(
              () ->
                  new Transformer3WResult(
                      timestamp, inputModel, iAMag, iAAng, iBMag, iBAng, iCMag, iCAng, tapPos));
    } else throw new FactoryException("Cannot process " + entityClass.getSimpleName() + ".class.");
  }
}
