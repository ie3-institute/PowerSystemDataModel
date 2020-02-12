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
import edu.ie3.models.input.thermal.ThermalStorageInput;
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
  private static final String sRated = "srated";

  // required in multiple types
  private static final String etaConv = "etaconv";
  private static final String pThermal = "pthermal";
  private static final String eStorage = "estorage";

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
    Set<String> standardConstructorParams =
        newSet(entityUuid, entityId, capEx, opEx, sRated, cosPhi);

    Set<String> constructorParameters = null; // TODO adapt all below
    if (data.getEntityClass().equals(EvTypeInput.class)) {
      constructorParameters = expandSet(standardConstructorParams, eStorage, eCons);
    } else if (data.getEntityClass().equals(HpTypeInput.class)) {
      constructorParameters = expandSet(standardConstructorParams, pThermal);
    } else if (data.getEntityClass().equals(BmTypeInput.class)) {
      constructorParameters = expandSet(standardConstructorParams, loadGradient, etaConv);
    } else if (data.getEntityClass().equals(WecTypeInput.class)) {
      constructorParameters = expandSet(standardConstructorParams, etaConv, rotorArea, hubHeight);
    } else if (data.getEntityClass().equals(ChpTypeInput.class)) { // into new file
      constructorParameters =
          expandSet(standardConstructorParams, etaEl, etaThermal, pThermal, pOwn);
    } else if (data.getEntityClass().equals(StorageTypeInput.class)) {
      constructorParameters =
          expandSet(standardConstructorParams, eStorage, pMin, pMax, eta, dod, lifeTime, lifeCycle);
    }

    return Collections.singletonList(constructorParameters);
  }

  @Override
  protected SystemParticipantTypeInput buildModel(SimpleEntityData data) {
    UUID uuid = data.getUUID(entityUuid);
    String id = data.get(entityId);
    Quantity<Currency> capExVal = data.get(capEx, StandardUnits.CAPEX);
    Quantity<EnergyPrice> opExVal = data.get(opEx, StandardUnits.ENERGY_PRICE);
    Quantity<Power> sRatedVal = data.get(sRated, StandardUnits.S_RATED);
    double cosPhiVal = Double.parseDouble(data.get(cosPhi));

    if (data.getEntityClass().equals(EvTypeInput.class))
      return buildEvTypeInput(data, uuid, id, capExVal, opExVal, sRatedVal, cosPhiVal);
    else if (data.getEntityClass().equals(HpTypeInput.class))
      return buildHpTypeInput(data, uuid, id, capExVal, opExVal, sRatedVal, cosPhiVal);
    else if (data.getEntityClass().equals(BmTypeInput.class))
      return buildBmTypeInput(data, uuid, id, capExVal, opExVal, sRatedVal, cosPhiVal);
    else if (data.getEntityClass().equals(WecTypeInput.class))
      return buildWecTypeInput(data, uuid, id, capExVal, opExVal, sRatedVal, cosPhiVal);
    else if (data.getEntityClass().equals(ChpTypeInput.class))
      return buildChpTypeInput(data, uuid, id, capExVal, opExVal, sRatedVal, cosPhiVal);
    else if (data.getEntityClass().equals(StorageTypeInput.class))
      return buildStorageTypeInput(data, uuid, id, capExVal, opExVal, sRatedVal, cosPhiVal);
    else
      throw new FactoryException(
          "SystemParticipantTypeInputFactory does not know how to build a "
              + data.getEntityClass().getName());
  }

  private SystemParticipantTypeInput buildEvTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      Quantity<Currency> capExVal,
      Quantity<EnergyPrice> opExVal,
      Quantity<Power> sRatedVal,
      double cosPhiVal) {
    Quantity<Energy> eStorageVal = data.get(eStorage, StandardUnits.ENERGY_IN);
    Quantity<SpecificEnergy> eConsVal = data.get(eCons, StandardUnits.ENERGY_PER_DISTANCE);

    return new EvTypeInput(
        uuid, id, capExVal, opExVal, eStorageVal, eConsVal, sRatedVal, cosPhiVal);
  }

  private SystemParticipantTypeInput buildHpTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      Quantity<Currency> capExVal,
      Quantity<EnergyPrice> opExVal,
      Quantity<Power> sRatedVal,
      double cosPhiVal) {
    Quantity<Power> pThermalVal = data.get(pThermal, StandardUnits.ACTIVE_POWER_IN);

    return new HpTypeInput(uuid, id, capExVal, opExVal, sRatedVal, cosPhiVal, pThermalVal);
  }

  private SystemParticipantTypeInput buildBmTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      Quantity<Currency> capExVal,
      Quantity<EnergyPrice> opExVal,
      Quantity<Power> sRatedVal,
      double cosPhiVal) {
    Quantity<DimensionlessRate> loadGradientVal =
        data.get(loadGradient, StandardUnits.LOAD_GRADIENT);
    Quantity<Dimensionless> etaConvVal = data.get(etaConv, StandardUnits.EFFICIENCY);

    return new BmTypeInput(
        uuid, id, capExVal, opExVal, loadGradientVal, sRatedVal, cosPhiVal, etaConvVal);
  }

  private SystemParticipantTypeInput buildWecTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      Quantity<Currency> capExVal,
      Quantity<EnergyPrice> opExVal,
      Quantity<Power> sRatedVal,
      double cosPhiVal) {
    Quantity<Dimensionless> etaConvVal = data.get(etaConv, StandardUnits.EFFICIENCY);
    Quantity<Area> rotorAreaVal = data.get(rotorArea, StandardUnits.ROTOR_AREA);
    Quantity<Length> hubHeightVal = data.get(hubHeight, StandardUnits.HUB_HEIGHT);

    return new WecTypeInput(
        uuid, id, capExVal, opExVal, cosPhiVal, etaConvVal, sRatedVal, rotorAreaVal, hubHeightVal);
  }

  private SystemParticipantTypeInput buildChpTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      Quantity<Currency> capExVal,
      Quantity<EnergyPrice> opExVal,
      Quantity<Power> sRatedVal,
      double cosPhiVal) {
    Quantity<Dimensionless> etaElVal = data.get(etaEl, StandardUnits.EFFICIENCY);
    Quantity<Dimensionless> etaThermalVal = data.get(etaThermal, StandardUnits.EFFICIENCY);
    Quantity<Power> pThermalVal = data.get(pThermal, StandardUnits.ACTIVE_POWER_IN);
    Quantity<Power> pOwnVal = data.get(pOwn, StandardUnits.ACTIVE_POWER_IN);
    ThermalStorageInput thermalStorageInput = null; // TODO

    return new ChpTypeInput(
        uuid,
        id,
        capExVal,
        opExVal,
        etaElVal,
        etaThermalVal,
        sRatedVal,
        cosPhiVal,
        pThermalVal,
        pOwnVal,
        thermalStorageInput);
  }

  private SystemParticipantTypeInput buildStorageTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      Quantity<Currency> capExVal,
      Quantity<EnergyPrice> opExVal,
      Quantity<Power> sRatedVal,
      double cosPhiVal) {
    Quantity<Energy> eStorageVal = data.get(eStorage, StandardUnits.ENERGY_IN);
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
        eStorageVal,
        sRatedVal,
        cosPhiVal,
        pMinVal,
        pMaxVal,
        etaVal,
        dodVal,
        lifeTimeVal,
        lifeCycleVal);
  }
}
