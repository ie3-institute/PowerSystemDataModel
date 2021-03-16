/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointType;
import javax.measure.Quantity;

public class SystemParticipantValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private SystemParticipantValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Validates a system participant if: <br>
   * - it is not null <br>
   * - its qCharacteristics are not null
   *
   * <p>A "distribution" method, that forwards the check request to specific implementations to
   * fulfill the checking task, based on the class of the given object. If an unknown class is
   * handed in, a {@link ValidationException} is thrown.
   *
   * @param systemParticipant systemParticipant to validate
   */
  public static void check(SystemParticipantInput systemParticipant) {
    checkNonNull(systemParticipant, "a system participant");
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
    else if (EvcsInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkEvcs((EvcsInput) systemParticipant);
    else throw new ValidationException(notImplementedString(systemParticipant));
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
    checkNonNull(systemParticipantTypeInput, "a system participant type");
    if ((systemParticipantTypeInput.getCapex() == null)
        || (systemParticipantTypeInput.getOpex() == null)
        || (systemParticipantTypeInput.getsRated() == null))
      throw new InvalidEntityException(
          "At least one of capex, opex, or sRated is null", systemParticipantTypeInput);
    detectNegativeQuantities(
        new Quantity<?>[] {
          systemParticipantTypeInput.getCapex(),
          systemParticipantTypeInput.getOpex(),
          systemParticipantTypeInput.getsRated()
        },
        systemParticipantTypeInput);
    if (systemParticipantTypeInput.getCosPhiRated() < 0d
        || systemParticipantTypeInput.getCosPhiRated() > 1d)
      throw new InvalidEntityException(
          "Rated power factor must be between 0 and 1", systemParticipantTypeInput);
  }

  /**
   * Validates a bmInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkBmType(BmTypeInput)} confirms a valid type
   * properties <br>
   *
   * @param bmInput BmInput to validate
   */
  public static void checkBm(BmInput bmInput) {
    checkBmType(bmInput.getType());
  }

  /**
   * Validates a bmTypeInput if: <br>
   * - common system participants values (capex, opex, sRated, cosphiRated) are valid <br>
   * - its active power gradient is not negative <br>
   * - its efficiency of assets inverter is between 0% and 100%
   *
   * @param bmTypeInput BmTypeInput to validate
   */
  public static void checkBmType(BmTypeInput bmTypeInput) {
    checkType(bmTypeInput);
    detectNegativeQuantities(new Quantity<?>[] {bmTypeInput.getActivePowerGradient()}, bmTypeInput);
    if (bmTypeInput.getEtaConv().getValue().doubleValue() < 0d
        || bmTypeInput.getEtaConv().getValue().doubleValue() > 100d)
      throw new InvalidEntityException(
          "Efficiency of inverter of biomass unit type must be between 0% and 100%", bmTypeInput);
  }

  /**
   * Validates a chpInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkChpType(ChpTypeInput)} confirms a valid type
   * properties
   *
   * @param chpInput ChpInput to validate
   */
  public static void checkChp(ChpInput chpInput) {
    checkChpType(chpInput.getType());
  }

  /**
   * Validates a chpTypeInput if: <br>
   * - common system participants values (capex, opex, sRated, cosphiRated) are valid <br>
   * - its efficiency of the electrical inverter is between 0% and 100% <br>
   * - its thermal efficiency of the system is between 0% and 100% <br>
   * - its rated thermal power is positive <br>
   * - its needed self-consumption is not negative
   *
   * @param chpTypeInput ChpTypeInput to validate
   */
  public static void checkChpType(ChpTypeInput chpTypeInput) {
    checkType(chpTypeInput);
    detectNegativeQuantities(new Quantity<?>[] {chpTypeInput.getpOwn()}, chpTypeInput);
    detectZeroOrNegativeQuantities(new Quantity<?>[] {chpTypeInput.getpThermal()}, chpTypeInput);
    if (chpTypeInput.getEtaEl().getValue().doubleValue() < 0d
        || chpTypeInput.getEtaEl().getValue().doubleValue() > 100d)
      throw new InvalidEntityException(
          "Efficiency of electrical inverter of CHP unit type must be between 0% and 100%",
          chpTypeInput);
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
    checkEvType(evInput.getType());
  }

  /**
   * Validates a EvTypeInput if: <br>
   * - common system participants values (capex, opex, sRated, cosphiRated) are valid <br>
   * - its available battery capacity is positive <br>
   * - its energy consumption per driven kilometre is positive
   *
   * @param evTypeInput EvTypeInput to validate
   */
  public static void checkEvType(EvTypeInput evTypeInput) {
    checkType(evTypeInput);
    detectZeroOrNegativeQuantities(
        new Quantity<?>[] {evTypeInput.geteStorage(), evTypeInput.geteCons()}, evTypeInput);
  }

  /**
   * Validates a FixedFeedInInput if: <br>
   * - its rated apparent power is not negative <br>
   * - its rated power factor is between 0 and 1
   *
   * @param fixedFeedInInput FixedFeedInInput to validate
   */
  public static void checkFixedFeedIn(FixedFeedInInput fixedFeedInInput) {
    detectNegativeQuantities(new Quantity<?>[] {fixedFeedInInput.getsRated()}, fixedFeedInInput);
    if (fixedFeedInInput.getCosPhiRated() < 0d || fixedFeedInInput.getCosPhiRated() > 1d)
      throw new InvalidEntityException(
          "Rated power factor of fixed feed-in unit must be between 0 and 1", fixedFeedInInput);
  }

  /**
   * Validates a HpInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkHpType(HpTypeInput)} confirms a valid type
   * properties
   *
   * @param hpInput HpInput to validate
   */
  public static void checkHp(HpInput hpInput) {
    checkHpType(hpInput.getType());
  }

  /**
   * Validates a HpTypeInput if: <br>
   * - common system participants values (capex, opex, sRated, cosphiRated) are valid <br>
   * - its rated thermal power is positive
   *
   * @param hpTypeInput HpTypeInput to validate
   */
  public static void checkHpType(HpTypeInput hpTypeInput) {
    checkType(hpTypeInput);
    detectZeroOrNegativeQuantities(new Quantity<?>[] {hpTypeInput.getpThermal()}, hpTypeInput);
  }

  /**
   * Validates a LoadInput if: <br>
   * - its standard load profile is not null <br>
   * - its rated apparent power is not negative <br>
   * - its annual energy consumption is not negative <br>
   * - its rated power factor is between 0 and 1
   *
   * @param loadInput LoadInput to validate
   */
  public static void checkLoad(LoadInput loadInput) {
    if (loadInput.getStandardLoadProfile() == null)
      throw new InvalidEntityException("No standard load profile defined for load", loadInput);
    detectNegativeQuantities(
        new Quantity<?>[] {loadInput.getsRated(), loadInput.geteConsAnnual()}, loadInput);
    if (loadInput.getCosPhiRated() < 0d || loadInput.getCosPhiRated() > 1d)
      throw new InvalidEntityException(
          "Rated power factor of load must be between 0 and 1", loadInput);
  }

  /**
   * Validates a PvInput if: <br>
   * - its rated apparent power is not negative <br>
   * - its albedo value of the plant's surrounding is between 0 and 1 <br>
   * - its inclination in a compass direction (azimuth) is is between -90° and 90° <br>
   * - its efficiency of the asset's inverter (etaConv) is is between 0% and 100% <br>
   * - its tilted inclination from horizontal (height) is is between 0° and 90° <br>
   * - its rated power factor is between 0 and 1
   *
   * @param pvInput PvInput to validate
   */
  public static void checkPv(PvInput pvInput) {
    detectNegativeQuantities(new Quantity<?>[] {pvInput.getsRated()}, pvInput);
    checkAlbedo(pvInput);
    checkAzimuth(pvInput);
    checkEtaConv(pvInput);
    checkHeight(pvInput);
    if (pvInput.getCosPhiRated() < 0d || pvInput.getCosPhiRated() > 1d)
      throw new InvalidEntityException(
          "Rated power factor of the PV unit must be between 0 and 1", pvInput);
  }

  /**
   * Check if albedo of pvInput is between 0 and 1
   *
   * @param pvInput PvInput to validate
   */
  private static void checkAlbedo(PvInput pvInput) {
    if (pvInput.getAlbedo() < 0d || pvInput.getAlbedo() > 1d)
      throw new InvalidEntityException(
          "Albedo of the plant's surrounding of "
              + pvInput.getClass().getSimpleName()
              + " must be between 0 and 1",
          pvInput);
  }

  /**
   * Check if azimuth angle of pvInput is between -90° and 90°
   *
   * @param pvInput PvInput to validate
   */
  private static void checkAzimuth(PvInput pvInput) {
    if (pvInput.getAzimuth().getValue().doubleValue() < -90d
        || pvInput.getAzimuth().getValue().doubleValue() > 90d)
      throw new InvalidEntityException(
          "Azimuth angle of "
              + pvInput.getClass().getSimpleName()
              + " must be between -90° (east) and 90° (west)",
          pvInput);
  }

  /**
   * Check if efficiency of the assets converter (etaConv) of pvInput is between 0% and 100%
   *
   * @param pvInput PvInput to validate
   */
  private static void checkEtaConv(PvInput pvInput) {
    if (pvInput.getEtaConv().getValue().doubleValue() < 0d
        || pvInput.getEtaConv().getValue().doubleValue() > 100d)
      throw new InvalidEntityException(
          "Efficiency of the converter of "
              + pvInput.getClass().getSimpleName()
              + " must be between 0% and 100%",
          pvInput);
  }

  /**
   * Check if tilted inclination from horizontal of pvInput is between 0° and 90°
   *
   * @param pvInput PvInput to validate
   */
  private static void checkHeight(PvInput pvInput) {
    if (pvInput.getHeight().getValue().doubleValue() < 0d
        || pvInput.getHeight().getValue().doubleValue() > 90d)
      throw new InvalidEntityException(
          "Tilted inclination from horizontal of "
              + pvInput.getClass().getSimpleName()
              + " must be between 0° and 90°",
          pvInput);
  }

  /**
   * Validates a StorageInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkStorageType(StorageTypeInput)} confirms a valid
   * type properties
   *
   * @param storageInput StorageInput to validate
   */
  public static void checkStorage(StorageInput storageInput) {
    checkStorageType(storageInput.getType());
  }

  /**
   * Validates a StorageTypeInput if: <br>
   * - common system participants values (capex, opex, sRated, cosphiRated) are valid <br>
   * - its permissible amount of full cycles is not negative <br>
   * - its efficiency of the electrical converter is between 0% and 100% <br>
   * - its maximum permissible depth of discharge is between 0% and 100% <br>
   * - its active power gradient is not negative <br>
   * - its battery capacity is positive <br>
   * - its maximum permissible active power (in-feed or consumption) is not negative <br>
   * - its permissible hours of full use is not negative
   *
   * @param storageTypeInput StorageTypeInput to validate
   */
  public static void checkStorageType(StorageTypeInput storageTypeInput) {
    checkType(storageTypeInput);
    if (storageTypeInput.getLifeCycle() < 0)
      throw new InvalidEntityException(
          "Permissible amount of life cycles of the storage type must be zero or positive",
          storageTypeInput);
    if (storageTypeInput.getEta().getValue().doubleValue() < 0d
        || storageTypeInput.getEta().getValue().doubleValue() > 100d)
      throw new InvalidEntityException(
          "Efficiency of the electrical converter of the storage type must be between 0% and 100%",
          storageTypeInput);
    if (storageTypeInput.getDod().getValue().doubleValue() < 0d
        || storageTypeInput.getDod().getValue().doubleValue() > 100d)
      throw new InvalidEntityException(
          "Maximum permissible depth of discharge of the storage type must be between 0% and 100%",
          storageTypeInput);
    detectNegativeQuantities(
        new Quantity<?>[] {
          storageTypeInput.getpMax(),
          storageTypeInput.getActivePowerGradient(),
          storageTypeInput.getLifeTime()
        },
        storageTypeInput);
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
    checkWecType(wecInput.getType());
  }

  /**
   * Validates a WecTypeInput if: <br>
   * - common system participants values (capex, opex, sRated, cosphiRated) are valid <br>
   * - its efficiency of the assets converter is between 0% and 100% <br>
   * - its rotor area is not negative <br>
   * - its height of the rotor hub is not negative
   *
   * @param wecTypeInput WecTypeInput to validate
   */
  public static void checkWecType(WecTypeInput wecTypeInput) {
    checkType(wecTypeInput);
    if (wecTypeInput.getEtaConv().getValue().doubleValue() < 0d
        || wecTypeInput.getEtaConv().getValue().doubleValue() > 100d)
      throw new InvalidEntityException(
          "Efficiency of the assets converter of the wind energy converter type must be between 0% and 100%",
          wecTypeInput);
    detectNegativeQuantities(
        new Quantity<?>[] {wecTypeInput.getRotorArea(), wecTypeInput.getHubHeight()}, wecTypeInput);
  }

  /**
   * Validates a EvcsInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkEvcsType(ChargingPointType)} confirms a valid
   * type properties
   *
   * @param evcsInput EvcsInput to validate
   */
  public static void checkEvcs(EvcsInput evcsInput) {
    checkEvcsType(evcsInput.getType());
    // TODO: Implement when class is finished
  }

  /**
   * Validates a ChargingPointType if: <br>
   * - ...
   *
   * @param evcsTypeInput ChargingPointType to validate
   */
  public static void checkEvcsType(ChargingPointType evcsTypeInput) {
    // TODO: Implement when class is finished
  }
}
