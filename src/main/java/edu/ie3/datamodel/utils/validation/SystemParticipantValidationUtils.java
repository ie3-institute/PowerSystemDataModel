/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import static edu.ie3.datamodel.models.StandardUnits.*;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.NotImplementedException;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.type.*;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

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
   * fulfill the checking task, based on the class of the given object.
   *
   * @param systemParticipant systemParticipant to validate
   * @throws edu.ie3.datamodel.exceptions.NotImplementedException if an unknown class is handed in
   */
  protected static void check(SystemParticipantInput systemParticipant) {
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
    else if (EvcsInput.class.isAssignableFrom(systemParticipant.getClass())) checkEvcs();
    else throw checkNotImplementedException(systemParticipant);
  }

  /**
   * Validates a system participant type if: <br>
   * - it is not null <br>
   * - capex is not null and not negative <br>
   * - opex is not null and not negative <br>
   * - sRated is not null and not negative <br>
   * - cosphiRated is between zero and one
   *
   * <p>A "distribution" method, that forwards the check request to specific implementations to
   * fulfill the checking task, based on the class of the given object.
   *
   * @param systemParticipantTypeInput systemParticipant Type to validate
   * @throws edu.ie3.datamodel.exceptions.NotImplementedException if an unknown class is handed in
   */
  protected static void checkType(SystemParticipantTypeInput systemParticipantTypeInput) {
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
    checkRatedPowerFactor(systemParticipantTypeInput, systemParticipantTypeInput.getCosPhiRated());

    if (BmTypeInput.class.isAssignableFrom(systemParticipantTypeInput.getClass()))
      checkBmType((BmTypeInput) systemParticipantTypeInput);
    else if (ChpTypeInput.class.isAssignableFrom(systemParticipantTypeInput.getClass()))
      checkChpType((ChpTypeInput) systemParticipantTypeInput);
    else if (EvTypeInput.class.isAssignableFrom(systemParticipantTypeInput.getClass()))
      checkEvType((EvTypeInput) systemParticipantTypeInput);
    else if (HpTypeInput.class.isAssignableFrom(systemParticipantTypeInput.getClass()))
      checkHpType((HpTypeInput) systemParticipantTypeInput);
    else if (StorageTypeInput.class.isAssignableFrom(systemParticipantTypeInput.getClass()))
      checkStorageType((StorageTypeInput) systemParticipantTypeInput);
    else if (WecTypeInput.class.isAssignableFrom(systemParticipantTypeInput.getClass()))
      checkWecType((WecTypeInput) systemParticipantTypeInput);
    else throw checkNotImplementedException(systemParticipantTypeInput);
  }

  /**
   * Validates a bmInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkBmType(BmTypeInput)} confirms a valid type
   * properties <br>
   *
   * @param bmInput BmInput to validate
   */
  private static void checkBm(BmInput bmInput) {
    checkType(bmInput.getType());
  }

  /**
   * Validates a bmTypeInput if: <br>
   * - its active power gradient is not negative <br>
   * - its efficiency of assets inverter is between 0% and 100%
   *
   * @param bmTypeInput BmTypeInput to validate
   */
  private static void checkBmType(BmTypeInput bmTypeInput) {
    detectNegativeQuantities(new Quantity<?>[] {bmTypeInput.getActivePowerGradient()}, bmTypeInput);
    isBetweenZeroAndHundredPercent(bmTypeInput, bmTypeInput.getEtaConv(), "Efficiency of inverter");
  }

  /**
   * Validates a chpInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkChpType(ChpTypeInput)} confirms a valid type
   * properties
   *
   * @param chpInput ChpInput to validate
   */
  private static void checkChp(ChpInput chpInput) {
    checkType(chpInput.getType());
  }

  /**
   * Validates a chpTypeInput if: <br>
   * - its efficiency of the electrical inverter is between 0% and 100% <br>
   * - its thermal efficiency of the system is between 0% and 100% <br>
   * - its rated thermal power is positive <br>
   * - its needed self-consumption is not negative
   *
   * @param chpTypeInput ChpTypeInput to validate
   */
  private static void checkChpType(ChpTypeInput chpTypeInput) {
    detectNegativeQuantities(new Quantity<?>[] {chpTypeInput.getpOwn()}, chpTypeInput);
    detectZeroOrNegativeQuantities(new Quantity<?>[] {chpTypeInput.getpThermal()}, chpTypeInput);
    isBetweenZeroAndHundredPercent(chpTypeInput, chpTypeInput.getEtaEl(), "Electrical efficiency");
    isBetweenZeroAndHundredPercent(
        chpTypeInput, chpTypeInput.getEtaThermal(), "Thermal efficiency");
  }

  /**
   * Validates a EvInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkEvType(EvTypeInput)} confirms a valid type
   * properties
   *
   * @param evInput EvInput to validate
   */
  private static void checkEv(EvInput evInput) {
    checkType(evInput.getType());
  }

  /**
   * Validates a EvTypeInput if: <br>
   * - its available battery capacity is positive <br>
   * - its energy consumption per driven kilometre is positive
   *
   * @param evTypeInput EvTypeInput to validate
   */
  private static void checkEvType(EvTypeInput evTypeInput) {
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
  private static void checkFixedFeedIn(FixedFeedInInput fixedFeedInInput) {
    detectNegativeQuantities(new Quantity<?>[] {fixedFeedInInput.getsRated()}, fixedFeedInInput);
    checkRatedPowerFactor(fixedFeedInInput, fixedFeedInInput.getCosPhiRated());
  }

  /**
   * Validates a HpInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkHpType(HpTypeInput)} confirms a valid type
   * properties
   *
   * @param hpInput HpInput to validate
   */
  private static void checkHp(HpInput hpInput) {
    checkType(hpInput.getType());
  }

  /**
   * Validates a HpTypeInput if: <br>
   * - its rated thermal power is positive
   *
   * @param hpTypeInput HpTypeInput to validate
   */
  private static void checkHpType(HpTypeInput hpTypeInput) {
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
  private static void checkLoad(LoadInput loadInput) {
    if (loadInput.getStandardLoadProfile() == null)
      throw new InvalidEntityException("No standard load profile defined for load", loadInput);
    detectNegativeQuantities(
        new Quantity<?>[] {loadInput.getsRated(), loadInput.geteConsAnnual()}, loadInput);
    checkRatedPowerFactor(loadInput, loadInput.getCosPhiRated());
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
  private static void checkPv(PvInput pvInput) {
    detectNegativeQuantities(new Quantity<?>[] {pvInput.getsRated()}, pvInput);
    checkAlbedo(pvInput);
    checkAzimuth(pvInput);
    isBetweenZeroAndHundredPercent(pvInput, pvInput.getEtaConv(), "Efficiency of the converter");
    checkHeight(pvInput);
    checkRatedPowerFactor(pvInput, pvInput.getCosPhiRated());
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
    if (pvInput.getAzimuth().isLessThan(Quantities.getQuantity(-90d, AZIMUTH))
        || pvInput.getAzimuth().isGreaterThan(Quantities.getQuantity(90d, AZIMUTH)))
      throw new InvalidEntityException(
          "Azimuth angle of "
              + pvInput.getClass().getSimpleName()
              + " must be between -90° (east) and 90° (west)",
          pvInput);
  }

  /**
   * Check if tilted inclination from horizontal of pvInput is between 0° and 90°
   *
   * @param pvInput PvInput to validate
   */
  private static void checkHeight(PvInput pvInput) {
    if (pvInput.getElevationAngle().isLessThan(Quantities.getQuantity(0d, SOLAR_ELEVATION_ANGLE))
        || pvInput
            .getElevationAngle()
            .isGreaterThan(Quantities.getQuantity(90d, SOLAR_ELEVATION_ANGLE)))
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
  private static void checkStorage(StorageInput storageInput) {
    checkType(storageInput.getType());
  }

  /**
   * Validates a StorageTypeInput if: <br>
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
  private static void checkStorageType(StorageTypeInput storageTypeInput) {
    if (storageTypeInput.getLifeCycle() < 0)
      throw new InvalidEntityException(
          "Permissible amount of life cycles of the storage type must be zero or positive",
          storageTypeInput);
    isBetweenZeroAndHundredPercent(
        storageTypeInput, storageTypeInput.getEta(), "Efficiency of the electrical converter");
    isBetweenZeroAndHundredPercent(
        storageTypeInput, storageTypeInput.getDod(), "Maximum permissible depth of discharge");
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
  private static void checkWec(WecInput wecInput) {
    checkType(wecInput.getType());
  }

  /**
   * Validates a WecTypeInput if: <br>
   * - its efficiency of the assets converter is between 0% and 100% <br>
   * - its rotor area is not negative <br>
   * - its height of the rotor hub is not negative
   *
   * @param wecTypeInput WecTypeInput to validate
   */
  private static void checkWecType(WecTypeInput wecTypeInput) {
    isBetweenZeroAndHundredPercent(
        wecTypeInput, wecTypeInput.getEtaConv(), "Efficiency of the converter");
    detectNegativeQuantities(
        new Quantity<?>[] {wecTypeInput.getRotorArea(), wecTypeInput.getHubHeight()}, wecTypeInput);
  }

  /** Validates a EvcsInput */
  private static void checkEvcs() {
    throw new NotImplementedException(
        String.format(
            "Validation of '%s' is currently not supported.", EvcsInput.class.getSimpleName()));
  }

  /**
   * Validates if the rated power factor is between 0 and 1, otherwise throws an {@link
   * InvalidEntityException}
   *
   * @param input entity to validate
   * @param cosPhiRated rated power factor to check
   */
  private static void checkRatedPowerFactor(InputEntity input, double cosPhiRated) {
    if (cosPhiRated < 0d || cosPhiRated > 1d)
      throw new InvalidEntityException(
          "Rated power factor of " + input.getClass().getSimpleName() + " must be between 0 and 1",
          input);
  }

  /**
   * Validates if a value (e.g. an efficiency) is between 0% and 100%, otherwise throws an {@link
   * InvalidEntityException}
   *
   * @param input entity to validate
   * @param value value of entity to check
   */
  private static void isBetweenZeroAndHundredPercent(
      InputEntity input, ComparableQuantity<Dimensionless> value, String string) {
    if (value.isLessThan(Quantities.getQuantity(0d, Units.PERCENT))
        || value.isGreaterThan(Quantities.getQuantity(100d, Units.PERCENT)))
      throw new InvalidEntityException(
          string + " of " + input.getClass().getSimpleName() + " must be between 0% and 100%",
          input);
  }
}
