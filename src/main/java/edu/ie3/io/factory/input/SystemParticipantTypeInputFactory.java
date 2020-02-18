/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.io.factory.SimpleEntityData;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.system.type.*;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.DimensionlessRate;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import edu.ie3.util.quantities.interfaces.SpecificEnergy;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.*;

public class SystemParticipantTypeInputFactory
    extends AssetTypeInputEntityFactory<SystemParticipantTypeInput> {
  // SystemParticipantTypeInput parameters
  private static final String CAP_EX = "capex";
  private static final String OP_EX = "opex";
  private static final String S_RATED = "srated";
  private static final String COS_PHI = "cosphi";

  // required in multiple types
  private static final String ETA_CONV = "etaconv";
  private static final String P_THERMAL = "pthermal";
  private static final String E_STORAGE = "estorage";

  // EvTypeInput
  private static final String E_CONS = "econs";

  // BmTypeInput
  private static final String LOAD_GRADIENT = "loadgradient";

  // WecTypeInput
  private static final String ROTOR_AREA = "rotorarea";
  private static final String HUB_HEIGHT = "hubheight";

  // ChpTypeInput
  private static final String ETA_EL = "etael";
  private static final String ETA_THERMAL = "etathermal";
  private static final String P_OWN = "pown";

  // StorageTypeInput
  private static final String P_MIN = "pmin";
  private static final String P_MAX = "pmax";
  private static final String ETA = "eta";
  private static final String DOD = "dod";
  private static final String LIFETIME = "lifetime";
  private static final String LIFECYCLE = "lifecycle";

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
        newSet(ENTITY_UUID, ENTITY_ID, CAP_EX, OP_EX, S_RATED, COS_PHI);

    Set<String> constructorParameters = null;
    if (data.getEntityClass().equals(EvTypeInput.class)) {
      constructorParameters = expandSet(standardConstructorParams, E_STORAGE, E_CONS);
    } else if (data.getEntityClass().equals(HpTypeInput.class)) {
      constructorParameters = expandSet(standardConstructorParams, P_THERMAL);
    } else if (data.getEntityClass().equals(BmTypeInput.class)) {
      constructorParameters = expandSet(standardConstructorParams, LOAD_GRADIENT, ETA_CONV);
    } else if (data.getEntityClass().equals(WecTypeInput.class)) {
      constructorParameters =
          expandSet(standardConstructorParams, ETA_CONV, ROTOR_AREA, HUB_HEIGHT);
    } else if (data.getEntityClass().equals(ChpTypeInput.class)) { // into new file
      constructorParameters =
          expandSet(standardConstructorParams, ETA_EL, ETA_THERMAL, P_THERMAL, P_OWN);
    } else if (data.getEntityClass().equals(StorageTypeInput.class)) {
      constructorParameters =
          expandSet(
              standardConstructorParams, E_STORAGE, P_MIN, P_MAX, ETA, DOD, LIFETIME, LIFECYCLE);
    }

    return Collections.singletonList(constructorParameters);
  }

  @Override
  protected SystemParticipantTypeInput buildModel(SimpleEntityData data) {
    UUID uuid = data.getUUID(ENTITY_UUID);
    String id = data.getField(ENTITY_ID);
    Quantity<Currency> capEx = data.getQuantity(CAP_EX, StandardUnits.CAPEX);
    Quantity<EnergyPrice> opEx = data.getQuantity(OP_EX, StandardUnits.ENERGY_PRICE);
    Quantity<Power> sRated = data.getQuantity(S_RATED, StandardUnits.S_RATED);
    double cosPhi = Double.parseDouble(data.getField(COS_PHI));

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
      Quantity<Currency> capEx,
      Quantity<EnergyPrice> opEx,
      Quantity<Power> sRated,
      double cosPhi) {
    Quantity<Energy> eStorage = data.getQuantity(E_STORAGE, StandardUnits.ENERGY_IN);
    Quantity<SpecificEnergy> eCons = data.getQuantity(E_CONS, StandardUnits.ENERGY_PER_DISTANCE);

    return new EvTypeInput(uuid, id, capEx, opEx, eStorage, eCons, sRated, cosPhi);
  }

  private SystemParticipantTypeInput buildHpTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      Quantity<Currency> capEx,
      Quantity<EnergyPrice> opEx,
      Quantity<Power> sRated,
      double cosPhi) {
    Quantity<Power> pThermal = data.getQuantity(P_THERMAL, StandardUnits.ACTIVE_POWER_IN);

    return new HpTypeInput(uuid, id, capEx, opEx, sRated, cosPhi, pThermal);
  }

  private SystemParticipantTypeInput buildBmTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      Quantity<Currency> capEx,
      Quantity<EnergyPrice> opEx,
      Quantity<Power> sRated,
      double cosPhi) {
    Quantity<DimensionlessRate> loadGradient =
        data.getQuantity(LOAD_GRADIENT, StandardUnits.LOAD_GRADIENT);
    Quantity<Dimensionless> etaConv = data.getQuantity(ETA_CONV, StandardUnits.EFFICIENCY);

    return new BmTypeInput(uuid, id, capEx, opEx, loadGradient, sRated, cosPhi, etaConv);
  }

  private SystemParticipantTypeInput buildWecTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      Quantity<Currency> capEx,
      Quantity<EnergyPrice> opEx,
      Quantity<Power> sRated,
      double cosPhi) {
    Quantity<Dimensionless> etaConv = data.getQuantity(ETA_CONV, StandardUnits.EFFICIENCY);
    Quantity<Area> rotorArea = data.getQuantity(ROTOR_AREA, StandardUnits.ROTOR_AREA);
    Quantity<Length> hubHeight = data.getQuantity(HUB_HEIGHT, StandardUnits.HUB_HEIGHT);

    return new WecTypeInput(uuid, id, capEx, opEx, cosPhi, etaConv, sRated, rotorArea, hubHeight);
  }

  private SystemParticipantTypeInput buildChpTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      Quantity<Currency> capEx,
      Quantity<EnergyPrice> opEx,
      Quantity<Power> sRated,
      double cosPhi) {
    Quantity<Dimensionless> etaEl = data.getQuantity(ETA_EL, StandardUnits.EFFICIENCY);
    Quantity<Dimensionless> etaThermal = data.getQuantity(ETA_THERMAL, StandardUnits.EFFICIENCY);
    Quantity<Power> pThermal = data.getQuantity(P_THERMAL, StandardUnits.ACTIVE_POWER_IN);
    Quantity<Power> pOwn = data.getQuantity(P_OWN, StandardUnits.ACTIVE_POWER_IN);

    return new ChpTypeInput(
        uuid, id, capEx, opEx, etaEl, etaThermal, sRated, cosPhi, pThermal, pOwn);
  }

  private SystemParticipantTypeInput buildStorageTypeInput(
      SimpleEntityData data,
      UUID uuid,
      String id,
      Quantity<Currency> capEx,
      Quantity<EnergyPrice> opEx,
      Quantity<Power> sRated,
      double cosPhi) {
    Quantity<Energy> eStorage = data.getQuantity(E_STORAGE, StandardUnits.ENERGY_IN);
    Quantity<Power> pMin = data.getQuantity(P_MIN, StandardUnits.ACTIVE_POWER_IN);
    Quantity<Power> pMax = data.getQuantity(P_MAX, StandardUnits.ACTIVE_POWER_IN);
    Quantity<Dimensionless> eta = data.getQuantity(ETA, StandardUnits.EFFICIENCY);
    Quantity<Dimensionless> dod = data.getQuantity(DOD, StandardUnits.DOD);
    Quantity<Time> lifeTime = data.getQuantity(LIFETIME, StandardUnits.LIFE_TIME);
    int lifeCycle = Integer.parseInt(data.getField(LIFECYCLE));

    return new StorageTypeInput(
        uuid, id, capEx, opEx, eStorage, sRated, cosPhi, pMin, pMax, eta, dod, lifeTime, lifeCycle);
  }
}
