/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.factory.FieldMap;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.DimensionlessRate;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import edu.ie3.util.quantities.interfaces.SpecificEnergy;
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
  protected T buildModel(FieldMap data) {
    UUID uuid = data.getUUID(UUID_FIELD);
    String id = data.getField(ID);
    ComparableQuantity<Currency> capEx = data.getQuantity(CAP_EX, StandardUnits.CAPEX);
    ComparableQuantity<EnergyPrice> opEx = data.getQuantity(OP_EX, StandardUnits.ENERGY_PRICE);
    ComparableQuantity<Power> sRated = data.getQuantity(S_RATED, StandardUnits.S_RATED);
    double cosPhi = data.getDouble(COS_PHI_RATED);

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
      FieldMap data,
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capEx,
      ComparableQuantity<EnergyPrice> opEx,
      ComparableQuantity<Power> sRated,
      double cosPhi) {
    ComparableQuantity<Energy> eStorage = data.getQuantity(E_STORAGE, StandardUnits.ENERGY_IN);
    ComparableQuantity<SpecificEnergy> eCons =
        data.getQuantity(E_CONS, StandardUnits.ENERGY_PER_DISTANCE);
    ComparableQuantity<Power> sRatedDC =
        data.getQuantity(S_RATED_DC, StandardUnits.ACTIVE_POWER_IN);

    return new EvTypeInput(
        uuid,
        id,
        capEx,
        opEx,
        eStorage,
        eCons,
        sRated,
        cosPhi,
        sRatedDC,
        data.additionalInformation());
  }

  private HpTypeInput buildHpTypeInput(
      FieldMap data,
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capEx,
      ComparableQuantity<EnergyPrice> opEx,
      ComparableQuantity<Power> sRated,
      double cosPhi) {
    ComparableQuantity<Power> pThermal = data.getQuantity(P_THERMAL, StandardUnits.ACTIVE_POWER_IN);

    return new HpTypeInput(
        uuid, id, capEx, opEx, sRated, cosPhi, pThermal, data.additionalInformation());
  }

  private AcTypeInput buildAcTypeInput(
      FieldMap data,
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capEx,
      ComparableQuantity<EnergyPrice> opEx,
      ComparableQuantity<Power> sRated,
      double cosPhi) {
    ComparableQuantity<Power> pThermal = data.getQuantity(P_THERMAL, StandardUnits.ACTIVE_POWER_IN);

    return new AcTypeInput(
        uuid, id, capEx, opEx, sRated, cosPhi, pThermal, data.additionalInformation());
  }

  private BmTypeInput buildBmTypeInput(
      FieldMap data,
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

    return new BmTypeInput(
        uuid, id, capEx, opEx, loadGradient, sRated, cosPhi, etaConv, data.additionalInformation());
  }

  private WecTypeInput buildWecTypeInput(
      FieldMap data,
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

    String cpCharacteristicValue = data.getField(CP_CHARACTERISTIC);

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
        data.additionalInformation());
  }

  private ChpTypeInput buildChpTypeInput(
      FieldMap data,
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
        uuid,
        id,
        capEx,
        opEx,
        etaEl,
        etaThermal,
        sRated,
        cosPhi,
        pThermal,
        pOwn,
        data.additionalInformation());
  }

  private StorageTypeInput buildStorageTypeInput(
      FieldMap data,
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
        data.additionalInformation());
  }
}
