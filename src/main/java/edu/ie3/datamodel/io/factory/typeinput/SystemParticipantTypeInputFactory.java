/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.DimensionlessRate;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import edu.ie3.util.quantities.interfaces.SpecificEnergy;
import java.util.Map;
import java.util.UUID;
import javax.measure.quantity.*;
import tech.units.indriya.ComparableQuantity;

public class SystemParticipantTypeInputFactory<T extends SystemParticipantTypeInput>
    extends AssetTypeInputEntityFactory<SystemParticipantTypeInput, T> {

  private final Class<T> targetClass;

  public SystemParticipantTypeInputFactory(Class<T> targetClass) {
    super(
        AcTypeInput.class,
        EvTypeInput.class,
        HpTypeInput.class,
        BmTypeInput.class,
        WecTypeInput.class,
        ChpTypeInput.class,
        StorageTypeInput.class);
    this.targetClass = targetClass;

    isSupportedClass(targetClass);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected T buildModel(Map<String, String> data) {
    UUID uuid = getUUID(data, UUID);
    String id = getField(data, ID);
    ComparableQuantity<Currency> capEx = getQuantity(data, CAP_EX, StandardUnits.CAPEX);
    ComparableQuantity<EnergyPrice> opEx = getQuantity(data, OP_EX, StandardUnits.ENERGY_PRICE);
    ComparableQuantity<Power> sRated = getQuantity(data, S_RATED, StandardUnits.S_RATED);
    double cosPhi = getDouble(data, COS_PHI_RATED);

    if (targetClass.equals(EvTypeInput.class))
      return (T) buildEvTypeInput(data, uuid, id, capEx, opEx, sRated, cosPhi);
    else if (targetClass.equals(HpTypeInput.class))
      return (T) buildHpTypeInput(data, uuid, id, capEx, opEx, sRated, cosPhi);
    else if (targetClass.equals(AcTypeInput.class))
      return (T) buildAcTypeInput(data, uuid, id, capEx, opEx, sRated, cosPhi);
    else if (targetClass.equals(BmTypeInput.class))
      return (T) buildBmTypeInput(data, uuid, id, capEx, opEx, sRated, cosPhi);
    else if (targetClass.equals(WecTypeInput.class))
      return (T) buildWecTypeInput(data, uuid, id, capEx, opEx, sRated, cosPhi);
    else if (targetClass.equals(ChpTypeInput.class))
      return (T) buildChpTypeInput(data, uuid, id, capEx, opEx, sRated, cosPhi);
    else if (targetClass.equals(StorageTypeInput.class))
      return (T) buildStorageTypeInput(data, uuid, id, capEx, opEx, sRated, cosPhi);
    else
      throw new FactoryException(
          "SystemParticipantTypeInputFactory does not know how to build a "
              + targetClass.getName());
  }

  private EvTypeInput buildEvTypeInput(
      Map<String, String> data,
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capEx,
      ComparableQuantity<EnergyPrice> opEx,
      ComparableQuantity<Power> sRated,
      double cosPhi) {
    ComparableQuantity<Energy> eStorage = getQuantity(data, E_STORAGE, StandardUnits.ENERGY_IN);
    ComparableQuantity<SpecificEnergy> eCons =
        getQuantity(data, E_CONS, StandardUnits.ENERGY_PER_DISTANCE);
    ComparableQuantity<Power> sRatedDC =
        getQuantity(data, S_RATED_DC, StandardUnits.ACTIVE_POWER_IN);

    return new EvTypeInput(uuid, id, capEx, opEx, eStorage, eCons, sRated, cosPhi, sRatedDC, data);
  }

  private HpTypeInput buildHpTypeInput(
      Map<String, String> data,
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capEx,
      ComparableQuantity<EnergyPrice> opEx,
      ComparableQuantity<Power> sRated,
      double cosPhi) {
    ComparableQuantity<Power> pThermal =
        getQuantity(data, P_THERMAL, StandardUnits.ACTIVE_POWER_IN);

    return new HpTypeInput(uuid, id, capEx, opEx, sRated, cosPhi, pThermal, data);
  }

  private AcTypeInput buildAcTypeInput(
      Map<String, String> data,
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capEx,
      ComparableQuantity<EnergyPrice> opEx,
      ComparableQuantity<Power> sRated,
      double cosPhi) {
    ComparableQuantity<Power> pThermal =
        getQuantity(data, P_THERMAL, StandardUnits.ACTIVE_POWER_IN);

    return new AcTypeInput(uuid, id, capEx, opEx, sRated, cosPhi, pThermal, data);
  }

  private BmTypeInput buildBmTypeInput(
      Map<String, String> data,
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capEx,
      ComparableQuantity<EnergyPrice> opEx,
      ComparableQuantity<Power> sRated,
      double cosPhi) {
    ComparableQuantity<DimensionlessRate> loadGradient =
        getQuantity(data, ACTIVE_POWER_GRADIENT, StandardUnits.ACTIVE_POWER_GRADIENT);
    ComparableQuantity<Dimensionless> etaConv =
        getQuantity(data, ETA_CONV, StandardUnits.EFFICIENCY);

    return new BmTypeInput(uuid, id, capEx, opEx, loadGradient, sRated, cosPhi, etaConv, data);
  }

  private WecTypeInput buildWecTypeInput(
      Map<String, String> data,
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capEx,
      ComparableQuantity<EnergyPrice> opEx,
      ComparableQuantity<Power> sRated,
      double cosPhi) {
    ComparableQuantity<Dimensionless> etaConv =
        getQuantity(data, ETA_CONV, StandardUnits.EFFICIENCY);
    ComparableQuantity<Area> rotorArea = getQuantity(data, ROTOR_AREA, StandardUnits.ROTOR_AREA);
    ComparableQuantity<Length> hubHeight = getQuantity(data, HUB_HEIGHT, StandardUnits.HUB_HEIGHT);

    String cpCharacteristicValue = getField(data, CP_CHARACTERISTIC);

    WecCharacteristicInput cpCharacteristic;
    try {
      cpCharacteristic = new WecCharacteristicInput(cpCharacteristicValue);
    } catch (ParsingException e) {
      throw new FactoryException(
          "Cannot parse the following Betz characteristic: '" + cpCharacteristicValue + "'", e);
    }

    return new WecTypeInput(
        uuid,
        id,
        capEx,
        opEx,
        sRated,
        cosPhi,
        cpCharacteristic,
        etaConv,
        rotorArea,
        hubHeight,
        data);
  }

  private ChpTypeInput buildChpTypeInput(
      Map<String, String> data,
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capEx,
      ComparableQuantity<EnergyPrice> opEx,
      ComparableQuantity<Power> sRated,
      double cosPhi) {
    ComparableQuantity<Dimensionless> etaEl = getQuantity(data, ETA_EL, StandardUnits.EFFICIENCY);
    ComparableQuantity<Dimensionless> etaThermal =
        getQuantity(data, ETA_THERMAL, StandardUnits.EFFICIENCY);
    ComparableQuantity<Power> pThermal =
        getQuantity(data, P_THERMAL, StandardUnits.ACTIVE_POWER_IN);
    ComparableQuantity<Power> pOwn = getQuantity(data, P_OWN, StandardUnits.ACTIVE_POWER_IN);

    return new ChpTypeInput(
        uuid, id, capEx, opEx, etaEl, etaThermal, sRated, cosPhi, pThermal, pOwn, data);
  }

  private StorageTypeInput buildStorageTypeInput(
      Map<String, String> data,
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capEx,
      ComparableQuantity<EnergyPrice> opEx,
      ComparableQuantity<Power> sRated,
      double cosPhi) {
    ComparableQuantity<Energy> eStorage = getQuantity(data, E_STORAGE, StandardUnits.ENERGY_IN);
    ComparableQuantity<Power> pMax = getQuantity(data, P_MAX, StandardUnits.ACTIVE_POWER_IN);
    ComparableQuantity<DimensionlessRate> activePowerGradient =
        getQuantity(data, ACTIVE_POWER_GRADIENT, StandardUnits.ACTIVE_POWER_GRADIENT);
    ComparableQuantity<Dimensionless> eta = getQuantity(data, ETA, StandardUnits.EFFICIENCY);

    return new StorageTypeInput(
        uuid, id, capEx, opEx, eStorage, sRated, cosPhi, pMax, activePowerGradient, eta, data);
  }
}
