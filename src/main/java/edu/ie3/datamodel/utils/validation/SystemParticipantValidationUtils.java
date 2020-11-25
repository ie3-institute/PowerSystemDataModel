/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.type.*;
import javax.measure.Quantity;

public class SystemParticipantValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private SystemParticipantValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Validates a system participant if: <br>
   * - it is not null <br>
   * - its node is not null <br>
   * - its qCharacteristics are not null
   *
   * <p>A "distribution" method, that forwards the check request to specific implementations to
   * fulfill the checking task, based on the class of the given object. If an unknown class is
   * handed in, a {@link ValidationException} is thrown.
   *
   * @param systemParticipant systemParticipant to validate
   */
  public static void check(SystemParticipantInput systemParticipant) {
    // Check if null
    checkNonNull(systemParticipant, "a system participant");
    // Check if node is null
    if (systemParticipant.getNode() == null)
      throw new InvalidEntityException("Node of system participant is null", systemParticipant);
    // Check if qCharacteristics is null
    if (systemParticipant.getqCharacteristics() == null)
      throw new InvalidEntityException(
          "Reactive power characteristics of system participant is not defined", systemParticipant);

    // Further checks for subclasses
    if (BmInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkBm((BmInput) systemParticipant);
    else if (ChpInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkChp((ChpInput) systemParticipant);
    else if (EvInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkEv((EvInput) systemParticipant);
    else if (FixedFeedInInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkFixedFeedIn((FixedFeedInInput) systemParticipant);
    else if (HpInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkHp((HpInput) systemParticipant);
    else if (LoadInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkLoad((LoadInput) systemParticipant);
    else if (PvInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkPv((PvInput) systemParticipant);
    else if (StorageInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkStorage((StorageInput) systemParticipant);
    else if (WecInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkWec((WecInput) systemParticipant);
    else
      throw new ValidationException(
          "Cannot validate object of class '"
              + systemParticipant.getClass().getSimpleName()
              + "', as no routine is implemented.");
  }

  /**
   * Validates a system participant type if: <br>
   * - it is not null <br>
   * - capex is not null and not negative <br>
   * - opex is not null and not negative <br>
   * - sRated is not null and not negative <br>
   * - cosphiRated is between zero and one
   *
   * @param systemParticipantTypeInput systemParticipant Type to validate
   */
  public static void checkType(SystemParticipantTypeInput systemParticipantTypeInput) {
    // Check if null
    checkNonNull(systemParticipantTypeInput, "a system participant type");
    // Check if any quantities are null
    if ((systemParticipantTypeInput.getCapex() == null)
        || (systemParticipantTypeInput.getOpex() == null)
        || (systemParticipantTypeInput.getsRated() == null))
      throw new InvalidEntityException(
          "At least one of capex, opex, or sRated is null", systemParticipantTypeInput);
    // Check for negative quantities
    detectNegativeQuantities(
        new Quantity<?>[] {
          systemParticipantTypeInput.getCapex(),
          systemParticipantTypeInput.getOpex(),
          systemParticipantTypeInput.getsRated()
        },
        systemParticipantTypeInput);
    // Check if rated power factor is between zero and one
    if (systemParticipantTypeInput.getCosPhiRated() < 0d
        || systemParticipantTypeInput.getCosPhiRated() > 1d)
      throw new InvalidEntityException(
          "Rated power factor must be between 0 and 1", systemParticipantTypeInput);
  }

  /**
   * Validates a bmInput if: <br>
   * - feed in tariff is not null <br>
   * - {@link SystemParticipantValidationUtils#checkBmType(BmTypeInput)} confirms a valid type
   * properties <br>
   *
   * @param bmInput BmInput to validate
   */
  public static void checkBm(BmInput bmInput) {
    // Check if feed in tariff is null
    if (bmInput.getFeedInTariff() == null)
      throw new InvalidEntityException("Feed in tariff of biomass unit is null", bmInput);
    // Check BmType
    checkBmType(bmInput.getType());
  }

  /**
   * Validates a bmTypeInput if: <br>
   * - common system participants values (capex, opex, sRated, cosphiRated) are valid <br>
   * - its active power gradient is not null and not negative <br>
   * - its efficiency of assets inverter is not null and between 0% and 100%
   *
   * @param bmTypeInput BmTypeInput to validate
   */
  public static void checkBmType(BmTypeInput bmTypeInput) {
    // Check common values of system participants
    checkType(bmTypeInput);
    // Check if active power gradient is null
    if (bmTypeInput.getActivePowerGradient() == null)
      throw new InvalidEntityException(
          "Active power gradient of biomass unit type is null", bmTypeInput);
    // Check if efficiency of assets inverter is null
    if (bmTypeInput.getEtaConv() == null)
      throw new InvalidEntityException(
          "Efficiency of inverter of biomass unit type is null", bmTypeInput);
    // Check if active power gradient is negative
    detectNegativeQuantities(new Quantity<?>[] {bmTypeInput.getActivePowerGradient()}, bmTypeInput);
    // Check if efficiency of assets inverter is between 0% and 100%
    if (bmTypeInput.getEtaConv().getValue().doubleValue() < 0d
        || bmTypeInput.getEtaConv().getValue().doubleValue() > 100d)
      throw new InvalidEntityException(
          "Efficiency of inverter of biomass unit type must be between 0% and 100%", bmTypeInput);
  }

  /**
   * Validates a chpInput if: <br>
   * - thermal bus is not null <br>
   * - thermal storage is not null <br>
   * - {@link SystemParticipantValidationUtils#checkChpType(ChpTypeInput)} confirms a valid type
   * properties
   *
   * @param chpInput ChpInput to validate
   */
  public static void checkChp(ChpInput chpInput) {
    // Check if thermal bus is null
    if (chpInput.getThermalBus() == null)
      throw new InvalidEntityException("Thermal bus of CHP unit is null", chpInput);
    // Check if thermal storage is null
    if (chpInput.getThermalStorage() == null)
      throw new InvalidEntityException("Thermal storage of CHP unit is null", chpInput);
    // Check ChpType
    checkChpType(chpInput.getType());
  }

  /**
   * Validates a chpTypeInput if: <br>
   * - common system participants values (capex, opex, sRated, cosphiRated) are valid <br>
   * - its efficiency of the electrical inverter is not null and between 0% and 100% <br>
   * - its thermal efficiency of the system is not null and between 0% and 100% <br>
   * - its rated thermal power is not null and positive <br>
   * - its needed self-consumption is not null and not negative
   *
   * @param chpTypeInput ChpTypeInput to validate
   */
  public static void checkChpType(ChpTypeInput chpTypeInput) {
    // Check common values of system participants
    checkType(chpTypeInput);
    // Check if any values are null
    if ((chpTypeInput.getEtaEl() == null)
        || (chpTypeInput.getEtaThermal() == null)
        || (chpTypeInput.getpThermal() == null)
        || (chpTypeInput.getpOwn() == null))
      throw new InvalidEntityException(
          "At least one value of the CHP unit type is null", chpTypeInput);
    // Check if needed self-consumption is negative
    detectNegativeQuantities(new Quantity<?>[] {chpTypeInput.getpOwn()}, chpTypeInput);
    // Check if rated thermal power is zero or negative
    detectZeroOrNegativeQuantities(new Quantity<?>[] {chpTypeInput.getpThermal()}, chpTypeInput);
    // Check if efficiency of electrical inverter is between 0% and 100%
    if (chpTypeInput.getEtaEl().getValue().doubleValue() < 0d
        || chpTypeInput.getEtaEl().getValue().doubleValue() > 100d)
      throw new InvalidEntityException(
          "Efficiency of electrical inverter of CHP unit type must be between 0% and 100%",
          chpTypeInput);
    // Check if thermal efficiency of system is between 0% and 100%
    if (chpTypeInput.getEtaThermal().getValue().doubleValue() < 0d
        || chpTypeInput.getEtaThermal().getValue().doubleValue() > 100d)
      throw new InvalidEntityException(
          "Thermal efficiency of system of CHP unit type must be between 0% and 100%",
          chpTypeInput);
  }

  /**
   * Validates a EvInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkEvType(EvTypeInput)} confirms a valid type
   * properties
   *
   * @param evInput EvInput to validate
   */
  public static void checkEv(EvInput evInput) {
    // Check EvType
    checkEvType(evInput.getType());
  }

  /**
   * Validates a EvTypeInput if: <br>
   * - common system participants values (capex, opex, sRated, cosphiRated) are valid <br>
   * - its available battery capacity is not null and positive <br>
   * - its energy consumption per driven kilometre is not null and positive
   *
   * @param evTypeInput EvTypeInput to validate
   */
  public static void checkEvType(EvTypeInput evTypeInput) {
    // Check common values of system participants
    checkType(evTypeInput);
    // Check if available battery capacity is null
    if (evTypeInput.geteStorage() == null)
      throw new InvalidEntityException(
          "Available battery capacity of the EV type is null", evTypeInput);
    // Check if energy consumption per driven kilometre is null
    if (evTypeInput.geteCons() == null)
      throw new InvalidEntityException(
          "Energy consumption per driven kilometre of the EV type is null", evTypeInput);
    // Check for zero or negative quantities
    detectZeroOrNegativeQuantities(
        new Quantity<?>[] {evTypeInput.geteStorage(), evTypeInput.geteCons()}, evTypeInput);
  }

  /**
   * Validates a FixedFeedInInput if: <br>
   * - its rated apparent power is not null and not negative <br>
   * - its rated power factor is between 0 and 1
   *
   * @param fixedFeedInInput FixedFeedInInput to validate
   */
  public static void checkFixedFeedIn(FixedFeedInInput fixedFeedInInput) {
    // Check if rated apparent power is null
    if (fixedFeedInInput.getsRated() == null)
      throw new InvalidEntityException(
          "Rated apparent power of fixed feed-in unit is null", fixedFeedInInput);
    // Check if rated apparent power is negative
    detectNegativeQuantities(new Quantity<?>[] {fixedFeedInInput.getsRated()}, fixedFeedInInput);
    // Check if rated power factor is between 0 and 1
    if (fixedFeedInInput.getCosPhiRated() < 0d || fixedFeedInInput.getCosPhiRated() > 1d)
      throw new InvalidEntityException(
          "Rated power factor of fixed feed-in unit must be between 0 and 1", fixedFeedInInput);
  }

  /**
   * Validates a HpInput if: <br>
   * - its thermal bus is not null - {@link
   * SystemParticipantValidationUtils#checkHpType(HpTypeInput)} confirms a valid type properties
   *
   * @param hpInput HpInput to validate
   */
  public static void checkHp(HpInput hpInput) {
    // Check if thermal bus is null
    if (hpInput.getThermalBus() == null)
      throw new InvalidEntityException("Thermal bus of heat pump is null", hpInput);
    // Check HpType
    checkHpType(hpInput.getType());
  }

  /**
   * Validates a HpTypeInput if: <br>
   * - common system participants values (capex, opex, sRated, cosphiRated) are valid <br>
   * - its rated thermal power is not null and positive
   *
   * @param hpTypeInput HpTypeInput to validate
   */
  public static void checkHpType(HpTypeInput hpTypeInput) {
    // Check common values of system participants
    checkType(hpTypeInput);
    // Check if rated thermal power is null
    if (hpTypeInput.getpThermal() == null)
      throw new InvalidEntityException("Rated thermal power of heat pump is null", hpTypeInput);
    // Check if rated thermal power is positive
    detectZeroOrNegativeQuantities(new Quantity<?>[] {hpTypeInput.getpThermal()}, hpTypeInput);
  }

  /**
   * Validates a LoadInput if: <br>
   * - its standard load profile is not null <br>
   * - its rated apparent power is not null and not negative <br>
   * - its annual energy consumption is not null and not negative <br>
   * - its rated power factor is between 0 and 1
   *
   * @param loadInput LoadInput to validate
   */
  public static void checkLoad(LoadInput loadInput) {
    // Check if standard load profile is null
    if (loadInput.getStandardLoadProfile() == null)
      throw new InvalidEntityException("No standard load profile defined for load", loadInput);
    // Check if rated apparent power is null
    if (loadInput.getsRated() == null)
      throw new InvalidEntityException("Rated apparent power of load is null", loadInput);
    // Check if annual energy consumption is null
    if (loadInput.geteConsAnnual() == null)
      throw new InvalidEntityException("Annual energy consumption of load is null", loadInput);
    // Check for negative quantities
    detectNegativeQuantities(
        new Quantity<?>[] {loadInput.getsRated(), loadInput.geteConsAnnual()}, loadInput);
    // Check if rated power factor is between 0 and 1
    if (loadInput.getCosPhiRated() < 0d || loadInput.getCosPhiRated() > 1d)
      throw new InvalidEntityException(
          "Rated power factor of load must be between 0 and 1", loadInput);
  }

  /**
   * Validates a PvInput if: <br>
   * - its rated apparent power is not null and not negative <br>
   * - its albedo value of the plant's surrounding is between 0 and 1 <br>
   * - its inclination in a compass direction (azimuth) is not null and is between -90° and 90° <br>
   * - its efficiency of the asset's inverter (etaConv) is not null and is between 0% and 100% <br>
   * - its tilted inclination from horizontal (height) is not null and is between 0° and 90° <br>
   * - its rated power factor is between 0 and 1
   *
   * @param pvInput PvInput to validate
   */
  public static void checkPv(PvInput pvInput) {
    // Check if any quantities are null
    if ((pvInput.getsRated() == null)
        || (pvInput.getAzimuth() == null)
        || (pvInput.getEtaConv() == null)
        || (pvInput.getHeight() == null))
      throw new InvalidEntityException("At least one value of the PV unit is null", pvInput);
    // Check if rated apparent power is negative
    detectNegativeQuantities(new Quantity<?>[] {pvInput.getsRated()}, pvInput);
    // Check if albedo is between 0 and 1
    if (pvInput.getAlbedo() < 0d || pvInput.getAlbedo() > 1d)
      throw new InvalidEntityException(
          "Albedo of the plant's surrounding of the PV unit must be between 0 and 1", pvInput);
    // Check if azimuth angle is between -90° and 90°
    if (pvInput.getAzimuth().getValue().doubleValue() < -90d
        || pvInput.getAzimuth().getValue().doubleValue() > 90d)
      throw new InvalidEntityException(
          "Azimuth angle of the PV unit must be between -90° (east) and 90° (west)", pvInput);
    // Check if efficiency of the assets converter (etaConv) is between 0% and 100%
    if (pvInput.getEtaConv().getValue().doubleValue() < 0d
        || pvInput.getEtaConv().getValue().doubleValue() > 100d)
      throw new InvalidEntityException(
          "Efficiency of the converter of the PV unit must be between 0% and 100%", pvInput);
    // Check if tilted inclination from horizontal is between 0° and 90°
    if (pvInput.getHeight().getValue().doubleValue() < 0d
        || pvInput.getHeight().getValue().doubleValue() > 90d)
      throw new InvalidEntityException(
          "Tilted inclination from horizontal of the PV unit must be between 0° and 90°", pvInput);
    // Check if rated power factor is between 0 and 1
    if (pvInput.getCosPhiRated() < 0d || pvInput.getCosPhiRated() > 1d)
      throw new InvalidEntityException(
          "Rated power factor of the PV unit must be between 0 and 1", pvInput);
  }

  /**
   * Validates a StorageInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkStorageType(StorageTypeInput)} confirms a valid
   * type properties
   *
   * @param storageInput StorageInput to validate
   */
  public static void checkStorage(StorageInput storageInput) {
    // Check StorageType
    checkStorageType(storageInput.getType());
  }

  /**
   * Validates a StorageTypeInput if: <br>
   * - common system participants values (capex, opex, sRated, cosphiRated) are valid <br>
   * - its permissible amount of full cycles is not negative <br>
   * - its efficiency of the electrical converter is not null and between 0% and 100% <br>
   * - its maximum permissible depth of discharge is not null and between 0% and 100% <br>
   * - its active power gradient is not null and not negative <br>
   * - its battery capacity is not null and positive <br>
   * - its maximum permissible active power (in-feed or consumption) is not null and not negative
   * <br>
   * - its permissible hours of full use is not null and not negative
   *
   * @param storageTypeInput StorageTypeInput to validate
   */
  public static void checkStorageType(StorageTypeInput storageTypeInput) {
    // Check common values of system participants
    checkType(storageTypeInput);
    // Check if any quantities are null
    if ((storageTypeInput.geteStorage() == null)
        || (storageTypeInput.getpMax() == null)
        || (storageTypeInput.getActivePowerGradient() == null)
        || (storageTypeInput.getEta() == null)
        || (storageTypeInput.getDod() == null)
        || (storageTypeInput.getLifeTime() == null))
      throw new InvalidEntityException(
          "At least one value of the storage type is null", storageTypeInput);
    // Check if permissible amount of full cycles is positive
    if (storageTypeInput.getLifeCycle() < 0)
      throw new InvalidEntityException(
          "Permissible amount of life cycles of the storage type must be positive",
          storageTypeInput);
    // Check if efficiency of the electrical converter is between 0% and 100%
    if (storageTypeInput.getEta().getValue().doubleValue() < 0d
        || storageTypeInput.getEta().getValue().doubleValue() > 100d)
      throw new InvalidEntityException(
          "Efficiency of the electrical converter of the storage type must be between 0% and 100%",
          storageTypeInput);
    // Check if maximum permissible depth of discharge is between 0% and 100%
    if (storageTypeInput.getDod().getValue().doubleValue() < 0d
        || storageTypeInput.getDod().getValue().doubleValue() > 100d)
      throw new InvalidEntityException(
          "Maximum permissible depth of discharge of the storage type must be between 0% and 100%",
          storageTypeInput);
    // Check if pMax, activePowerGradient or lifeTime are negative
    detectNegativeQuantities(
        new Quantity<?>[] {
          storageTypeInput.getpMax(),
          storageTypeInput.getActivePowerGradient(),
          storageTypeInput.getLifeTime()
        },
        storageTypeInput);
    // Check if eStorage is zero or negative
    detectZeroOrNegativeQuantities(
        new Quantity<?>[] {storageTypeInput.geteStorage()}, storageTypeInput);
  }

  /**
   * Validates a WecInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkWecType(WecTypeInput)} confirms a valid type
   * properties
   *
   * @param wecInput WecInput to validate
   */
  public static void checkWec(WecInput wecInput) {
    // Check WecType
    checkWecType(wecInput.getType());
  }

  /**
   * Validates a WecTypeInput if: <br>
   * - common system participants values (capex, opex, sRated, cosphiRated) are valid <br>
   * - its cpCharacteristic is not null <br>
   * - its efficiency of the assets converter is not null and between 0% and 100% <br>
   * - its rotor area is not null and not negative <br>
   * - its height of the rotor hub is not null and not negative
   *
   * @param wecTypeInput WecTypeInput to validate
   */
  public static void checkWecType(WecTypeInput wecTypeInput) {
    // Check common values of system participants
    checkType(wecTypeInput);
    // Check if any quantities are null
    if ((wecTypeInput.getCpCharacteristic() == null)
        || (wecTypeInput.getEtaConv() == null)
        || (wecTypeInput.getRotorArea() == null)
        || (wecTypeInput.getHubHeight() == null))
      throw new InvalidEntityException(
          "At least one value of he wind energy converter type is null", wecTypeInput);
    // Check if efficiency of the assets converter is between 0% and 100%
    if (wecTypeInput.getEtaConv().getValue().doubleValue() < 0d
        || wecTypeInput.getEtaConv().getValue().doubleValue() > 100d)
      throw new InvalidEntityException(
          "Efficiency of the assets converter of the wind energy converter type must be between 0% and 100%",
          wecTypeInput);
    // Check if rotorArea or hubHeight are negative
    detectNegativeQuantities(
        new Quantity<?>[] {wecTypeInput.getRotorArea(), wecTypeInput.getHubHeight()}, wecTypeInput);
  }
}
