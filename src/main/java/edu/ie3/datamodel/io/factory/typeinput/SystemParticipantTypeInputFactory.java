/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.util.quantities.dep.interfaces.Currency;
import edu.ie3.util.quantities.dep.interfaces.DimensionlessRate;
import edu.ie3.util.quantities.dep.interfaces.EnergyPrice;
import edu.ie3.util.quantities.dep.interfaces.SpecificEnergy;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.measure.quantity.*;
import tec.uom.se.ComparableQuantity;

public class SystemParticipantTypeInputFactory
    extends AssetTypeInputEntityFactory<SystemParticipantTypeInput> {
  // SystemParticipantTypeInput parameters
  private static final String CAP_EX = "capex";
  private static final String OP_EX = "opex";
  private static final String S_RATED = "srated";
  private static final String COS_PHI_RATED = "cosphirated";

  // required in multiple types
  private static final String ETA_CONV = "etaconv";
  private static final String P_THERMAL = "pthermal";
  private static final String E_STORAGE = "estorage";

  // EvTypeInput
  private static final String E_CONS = "econs";

  // BmTypeInput
  private static final String ACTIVE_POWER_GRADIENT = "activepowergradient";

  // WecTypeInput
  private static final String ROTOR_AREA = "rotorarea";
  private static final String HUB_HEIGHT = "hubheight";

  // ChpTypeInput
  private static final String ETA_EL = "etael";
  private static final String ETA_THERMAL = "etathermal";
  private static final String P_OWN = "pown";

  // StorageTypeInput
  private static final String P_MAX = "pmax";
  private static final String ETA = "eta";
  private static final String DOD = "dod";
  private static final String LIFETIME = "lifetime";
  private static final String LIFECYCLE = "lifecycle";

  // WecTypeInput
  private static final String CP_CHARACTERISTIC = "cpCharacteristic";

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
        newSet(ENTITY_UUID, ENTITY_ID, CAP_EX, OP_EX, S_RATED, COS_PHI_RATED);

    Set<String> constructorParameters = null;
    if (data.getEntityClass().equals(EvTypeInput.class)) {
      constructorParameters = expandSet(standardConstructorParams, E_STORAGE, E_CONS);
    } else if (data.getEntityClass().equals(HpTypeInput.class)) {
      constructorParameters = expandSet(standardConstructorParams, P_THERMAL);
    } else if (data.getEntityClass().equals(BmTypeInput.class)) {
      constructorParameters = expandSet(standardConstructorParams, ACTIVE_POWER_GRADIENT, ETA_CONV);
    } else if (data.getEntityClass().equals(WecTypeInput.class)) {
      constructorParameters =
          expandSet(standardConstructorParams, CP_CHARACTERISTIC, ETA_CONV, ROTOR_AREA, HUB_HEIGHT);
    } else if (data.getEntityClass().equals(ChpTypeInput.class)) { // into new file
      constructorParameters =
          expandSet(standardConstructorParams, ETA_EL, ETA_THERMAL, P_THERMAL, P_OWN);
    } else if (data.getEntityClass().equals(StorageTypeInput.class)) {
      constructorParameters =
          expandSet(
              standardConstructorParams,
              E_STORAGE,
              P_MAX,
              ACTIVE_POWER_GRADIENT,
              ETA,
              DOD,
              LIFETIME,
              LIFECYCLE);
    }

    return Collections.singletonList(constructorParameters);
  }

  @Override
  protected SystemParticipantTypeInput buildModel(SimpleEntityData data) {
    UUID uuid = data.getUUID(ENTITY_UUID);
    String id = data.getField(ENTITY_ID);
    ComparableQuantity<Currency> capEx = data.getQuantity(CAP_EX, StandardUnits.CAPEX);
    ComparableQuantity<EnergyPrice> opEx = data.getQuantity(OP_EX, StandardUnits.ENERGY_PRICE);
    ComparableQuantity<Power> sRated = data.getQuantity(S_RATED, StandardUnits.S_RATED);
    double cosPhi = data.getDouble(COS_PHI_RATED);

    if (data.getEntityClass().equals(EvTypeInput.class))
      return buildEvTypeInput(data, uuid, id, capEx, opEx, sRated, cosPhi);
    else if (data.getEntityClass().equals(HpTypeInput.class))
      return buildHpTypeInput(data, uuid, id, capEx, opEx, sRated, cosPhi);
    else if (data.getEntityClass().equals(BmTypeInput.class))
      return buildBmTypeInput(data, uuid, id, capEx, opEx, sRated, cosPhi);
    else if (data.getEntityClass().equals(WecTypeInput.class))
      return buildWecTypeInput(data, uuid, id, capEx, opEx, sRated, cosPhi);
    else if (data.getEntityClass().equals(ChpTypeInput.class))
      return buildChpTypeInput(data, uuid, id, capEx, opEx, sRated, cosPhi);
    else if (data.getEntityClass().equals(StorageTypeInput.class))
      return buildStorageTypeInput(data, uuid, id, capEx, opEx, sRated, cosPhi);
    else
      throw new FactoryException(
          "SystemParticipantTypeInputFactory does not know how to build a "
              + data.getEntityClass().getName());
  }

  private SystemParticipantTypeInput buildEvTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capEx,
      ComparableQuantity<EnergyPrice> opEx,
      ComparableQuantity<Power> sRated,
      double cosPhi) {
    ComparableQuantity<Energy> eStorage = data.getQuantity(E_STORAGE, StandardUnits.ENERGY_IN);

    ComparableQuantity<SpecificEnergy> eCons =
        data.getQuantity(E_CONS, StandardUnits.ENERGY_PER_DISTANCE);

    return new EvTypeInput(uuid, id, capEx, opEx, eStorage, eCons, sRated, cosPhi);
  }

  private SystemParticipantTypeInput buildHpTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capEx,
      ComparableQuantity<EnergyPrice> opEx,
      ComparableQuantity<Power> sRated,
      double cosPhi) {
    ComparableQuantity<Power> pThermal = data.getQuantity(P_THERMAL, StandardUnits.ACTIVE_POWER_IN);

    return new HpTypeInput(uuid, id, capEx, opEx, sRated, cosPhi, pThermal);
  }

  private SystemParticipantTypeInput buildBmTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capEx,
      ComparableQuantity<EnergyPrice> opEx,
      ComparableQuantity<Power> sRated,
      double cosPhi) {
    ComparableQuantity<DimensionlessRate> loadGradient =
        data.getQuantity(ACTIVE_POWER_GRADIENT, StandardUnits.ACTIVE_POWER_GRADIENT);
    ComparableQuantity<Dimensionless> etaConv =
        data.getQuantity(ETA_CONV, StandardUnits.EFFICIENCY);

    return new BmTypeInput(uuid, id, capEx, opEx, loadGradient, sRated, cosPhi, etaConv);
  }

  private SystemParticipantTypeInput buildWecTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capEx,
      ComparableQuantity<EnergyPrice> opEx,
      ComparableQuantity<Power> sRated,
      double cosPhi) {
    ComparableQuantity<Dimensionless> etaConv =
        data.getQuantity(ETA_CONV, StandardUnits.EFFICIENCY);

    ComparableQuantity<Area> rotorArea = data.getQuantity(ROTOR_AREA, StandardUnits.ROTOR_AREA);

    ComparableQuantity<Length> hubHeight = data.getQuantity(HUB_HEIGHT, StandardUnits.HUB_HEIGHT);

    WecCharacteristicInput cpCharacteristic;
    try {
      cpCharacteristic = new WecCharacteristicInput(data.getField(CP_CHARACTERISTIC));
    } catch (ParsingException e) {
      throw new FactoryException(
          "Cannot parse the following Betz characteristic: '"
              + data.getField(CP_CHARACTERISTIC)
              + "'",
          e);
    }

    return new WecTypeInput(
        uuid, id, capEx, opEx, sRated, cosPhi, cpCharacteristic, etaConv, rotorArea, hubHeight);
  }

  private SystemParticipantTypeInput buildChpTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capEx,
      ComparableQuantity<EnergyPrice> opEx,
      ComparableQuantity<Power> sRated,
      double cosPhi) {
    ComparableQuantity<Dimensionless> etaEl = data.getQuantity(ETA_EL, StandardUnits.EFFICIENCY);

    ComparableQuantity<Dimensionless> etaThermal =
        data.getQuantity(ETA_THERMAL, StandardUnits.EFFICIENCY);

    ComparableQuantity<Power> pThermal = data.getQuantity(P_THERMAL, StandardUnits.ACTIVE_POWER_IN);

    ComparableQuantity<Power> pOwn = data.getQuantity(P_OWN, StandardUnits.ACTIVE_POWER_IN);

    return new ChpTypeInput(
        uuid, id, capEx, opEx, etaEl, etaThermal, sRated, cosPhi, pThermal, pOwn);
  }

  private SystemParticipantTypeInput buildStorageTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capEx,
      ComparableQuantity<EnergyPrice> opEx,
      ComparableQuantity<Power> sRated,
      double cosPhi) {
    ComparableQuantity<Energy> eStorage = data.getQuantity(E_STORAGE, StandardUnits.ENERGY_IN);
    ComparableQuantity<Power> pMax = data.getQuantity(P_MAX, StandardUnits.ACTIVE_POWER_IN);
    ComparableQuantity<DimensionlessRate> activePowerGradient =
        data.getQuantity(ACTIVE_POWER_GRADIENT, StandardUnits.ACTIVE_POWER_GRADIENT);
    ComparableQuantity<Dimensionless> eta = data.getQuantity(ETA, StandardUnits.EFFICIENCY);
    ComparableQuantity<Dimensionless> dod = data.getQuantity(DOD, StandardUnits.DOD);
    ComparableQuantity<Time> lifeTime = data.getQuantity(LIFETIME, StandardUnits.LIFE_TIME);
    int lifeCycle = data.getInt(LIFECYCLE);

    return new StorageTypeInput(
        uuid,
        id,
        capEx,
        opEx,
        eStorage,
        sRated,
        cosPhi,
        pMax,
        activePowerGradient,
        eta,
        dod,
        lifeTime,
        lifeCycle);
  }
}
