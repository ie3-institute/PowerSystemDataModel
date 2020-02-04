/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.io.factory.SimpleEntityData;
import edu.ie3.io.factory.SimpleEntityFactory;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.system.type.*;
import edu.ie3.util.quantities.PowerSystemUnits;
import edu.ie3.util.quantities.interfaces.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.*;

public class SystemParticipantTypeInputFactory
    extends SimpleEntityFactory<SystemParticipantTypeInput> {
  // SystemParticipantTypeInput parameters
  private static final String entityUuid = "uuid";
  private static final String entityId = "id";
  private static final String capEx = "capex";
  private static final String opEx = "opex";
  private static final String cosPhi = "cosphi";

  // required in multiple types
  private static final String sRated = "srated";
  private static final String etaConv = "etaconv";
  private static final String pThermal = "pthermal";
  private static final String pEl = "pel";
  private static final String eStorage = "estorage";
  private static final String pRated = "prated";

  // EvTypeInput
  private static final String eCons = "econs";

  // BmTypeInput
  private static final String loadGradient = "loadgradient";

  // WecTypeInput
  private static final String rotorArea = "rotorarea";
  private static final String hubHeight = "hubheight";

  // ChpTypeInput
  private static final String etaEl = "etael";
  private static final String etaThermal = "etathermal";
  private static final String pOwn = "pown";
  private static final String storageVolumeLvl = "storagevolumelvl";
  private static final String storageVolumeLvlMin = "storagevolumelvlmin";
  private static final String inletTemp = "inlettemp";
  private static final String returnTemp = "returntemp";
  private static final String c = "c";

  // StorageTypeInput
  private static final String pMin = "pmin";
  private static final String pMax = "pmax";
  private static final String eta = "eta";
  private static final String dod = "dod";
  private static final String lifeTime = "lifetime";
  private static final String lifeCycle = "lifecycle";

  public SystemParticipantTypeInputFactory() {
    super(
        EvTypeInput.class,
        HpTypeInput.class,
        BmTypeInput.class,
        WecTypeInput.class,
        ChpTypeInput.class,
        StorageTypeInput.class);
  }

  @Override
  protected List<Set<String>> getFields(SimpleEntityData data) {
    Set<String> standardConstructorParams = newSet(entityUuid, entityId, capEx, opEx, cosPhi);

    Set<String> constructorParameters = null;
    if (data.getEntityClass().equals(EvTypeInput.class)) {
      constructorParameters = expandSet(standardConstructorParams, eStorage, eCons, sRated);
    } else if (data.getEntityClass().equals(HpTypeInput.class)) {
      constructorParameters = expandSet(standardConstructorParams, pRated, pThermal, pEl);
    } else if (data.getEntityClass().equals(BmTypeInput.class)) {
      constructorParameters = expandSet(standardConstructorParams, loadGradient, sRated, etaConv);
    } else if (data.getEntityClass().equals(WecTypeInput.class)) {
      constructorParameters =
          expandSet(standardConstructorParams, etaConv, sRated, rotorArea, hubHeight);
    } else if (data.getEntityClass().equals(ChpTypeInput.class)) {
      constructorParameters =
          expandSet(
              standardConstructorParams,
              etaEl,
              etaThermal,
              pEl,
              pThermal,
              pOwn,
              storageVolumeLvl,
              storageVolumeLvlMin,
              inletTemp,
              returnTemp,
              c);
    } else if (data.getEntityClass().equals(StorageTypeInput.class)) {
      constructorParameters =
          expandSet(
              standardConstructorParams,
              eStorage,
              pRated,
              pMin,
              pMax,
              eta,
              dod,
              lifeTime,
              lifeCycle);
    }

    return Collections.singletonList(constructorParameters);
  }

  @Override
  protected SystemParticipantTypeInput buildModel(SimpleEntityData data) {
    UUID uuid = data.getUUID(entityUuid);
    String id = data.get(entityId);
    Quantity<Currency> capExVal = data.get(capEx, PowerSystemUnits.EURO); // TODO StandardUnit
    Quantity<EnergyPrice> opExVal = data.get(opEx, StandardUnits.ENERGY_PRICE);
    double cosPhiVal = Double.parseDouble(data.get(cosPhi));

    if (data.getEntityClass().equals(EvTypeInput.class)) {
      Quantity<Energy> eStorageVal = data.get(eStorage, StandardUnits.ENERGY); // TODO StandardUnit
      Quantity<SpecificEnergy> eConsVal =
          data.get(eCons, PowerSystemUnits.WATTHOUR_PER_METRE); // TODO StandardUnit
      Quantity<Power> sRatedVal = data.get(sRated, StandardUnits.S_RATED);

      return new EvTypeInput(
          uuid, id, capExVal, opExVal, cosPhiVal, eStorageVal, eConsVal, sRatedVal);
    } else if (data.getEntityClass().equals(HpTypeInput.class)) {
      Quantity<Power> pRatedVal = data.get(pRated, StandardUnits.ACTIVE_POWER_IN);
      Quantity<Power> pThermalVal = data.get(pThermal, StandardUnits.ACTIVE_POWER_IN);
      Quantity<Power> pElVal = data.get(pEl, StandardUnits.ACTIVE_POWER_IN);

      return new HpTypeInput(
          uuid, id, capExVal, opExVal, cosPhiVal, pRatedVal, pThermalVal, pElVal);
    } else if (data.getEntityClass().equals(BmTypeInput.class)) {
      Quantity<DimensionlessRate> loadGradientVal =
          data.get(loadGradient, StandardUnits.LOAD_GRADIENT);
      Quantity<Power> sRatedVal = data.get(sRated, StandardUnits.S_RATED);
      Quantity<Dimensionless> etaConvVal = data.get(etaConv, StandardUnits.EFFICIENCY);

      return new BmTypeInput(
          uuid, id, capExVal, opExVal, cosPhiVal, loadGradientVal, sRatedVal, etaConvVal);
    } else if (data.getEntityClass().equals(WecTypeInput.class)) {
      Quantity<Dimensionless> etaConvVal = data.get(etaConv, StandardUnits.EFFICIENCY);
      Quantity<Power> sRatedVal = data.get(sRated, StandardUnits.S_RATED);
      Quantity<Area> rotorAreaVal = data.get(rotorArea, StandardUnits.ROTOR_AREA);
      Quantity<Length> hubHeightVal = data.get(hubHeight, StandardUnits.HUB_HEIGHT);

      return new WecTypeInput(
          uuid,
          id,
          capExVal,
          opExVal,
          cosPhiVal,
          etaConvVal,
          sRatedVal,
          rotorAreaVal,
          hubHeightVal);
    } else if (data.getEntityClass().equals(ChpTypeInput.class)) {
      Quantity<Dimensionless> etaElVal = data.get(etaEl, StandardUnits.EFFICIENCY);
      Quantity<Dimensionless> etaThermalVal = data.get(etaThermal, StandardUnits.EFFICIENCY);
      Quantity<Power> pElVal = data.get(pEl, StandardUnits.ACTIVE_POWER_IN);
      Quantity<Power> pThermalVal = data.get(pThermal, StandardUnits.ACTIVE_POWER_IN);
      Quantity<Power> pOwnVal = data.get(pOwn, StandardUnits.ACTIVE_POWER_IN);
      Quantity<Volume> storageVolumeLvlVal = data.get(storageVolumeLvl, StandardUnits.VOLUME);
      Quantity<Volume> storageVolumeLvlMinVal = data.get(storageVolumeLvlMin, StandardUnits.VOLUME);
      Quantity<Temperature> inletTempVal = data.get(inletTemp, StandardUnits.TEMPERATURE);
      Quantity<Temperature> returnTempVal = data.get(returnTemp, StandardUnits.TEMPERATURE);
      Quantity<SpecificHeatCapacity> cVal = data.get(c, StandardUnits.SPECIFIC_HEAT_CAPACITY);

      return new ChpTypeInput(
          uuid,
          id,
          capExVal,
          opExVal,
          cosPhiVal,
          etaElVal,
          etaThermalVal,
          pElVal,
          pThermalVal,
          pOwnVal,
          storageVolumeLvlVal,
          storageVolumeLvlMinVal,
          inletTempVal,
          returnTempVal,
          cVal);
    } else if (data.getEntityClass().equals(StorageTypeInput.class)) {
      Quantity<Energy> eStorageVal = data.get(eStorage, StandardUnits.ENERGY); // TODO StandardUnit
      Quantity<Power> pRatedVal = data.get(pRated, StandardUnits.ACTIVE_POWER_IN);
      Quantity<Power> pMinVal = data.get(pMin, StandardUnits.ACTIVE_POWER_IN);
      Quantity<Power> pMaxVal = data.get(pMax, StandardUnits.ACTIVE_POWER_IN);
      Quantity<Dimensionless> etaVal = data.get(eta, StandardUnits.EFFICIENCY);
      Quantity<Dimensionless> dodVal = data.get(dod, StandardUnits.DOD);
      Quantity<Time> lifeTimeVal = data.get(lifeTime, StandardUnits.LIFE_TIME);
      int lifeCycleVal = Integer.parseInt(data.get(lifeCycle));

      return new StorageTypeInput(
          uuid,
          id,
          capExVal,
          opExVal,
          cosPhiVal,
          eStorageVal,
          pRatedVal,
          pMinVal,
          pMaxVal,
          etaVal,
          dodVal,
          lifeTimeVal,
          lifeCycleVal);
    } else
      throw new FactoryException(
          "SystemParticipantTypeInputFactory does not know how to build a "
              + data.getEntityClass().getName());
  }
}
