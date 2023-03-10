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
import edu.ie3.datamodel.utils.options.Failure;
import edu.ie3.datamodel.utils.options.Try;
import java.util.ArrayList;
import java.util.List;
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
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  protected static List<Try<Void, InvalidEntityException>> check(
      SystemParticipantInput systemParticipant) {
    try {
      checkNonNull(systemParticipant, "a system participant");
    } catch (InvalidEntityException e) {
      return List.of(
          new Failure<>(
              new InvalidEntityException(
                  "Validation not possible because received object {"
                      + systemParticipant
                      + "} was null",
                  e)));
    }

    List<Try<Void, InvalidEntityException>> exceptions = new ArrayList<>();

    if (systemParticipant.getqCharacteristics() == null) {
      exceptions.add(
          new Failure<>(
              new InvalidEntityException(
                  "Reactive power characteristics of system participant is not defined",
                  systemParticipant)));
    }

    // Further checks for subclasses
    if (BmInput.class.isAssignableFrom(systemParticipant.getClass())) {
      exceptions.addAll(checkBm((BmInput) systemParticipant));
    } else if (ChpInput.class.isAssignableFrom(systemParticipant.getClass())) {
      exceptions.addAll(checkChp((ChpInput) systemParticipant));
    } else if (EvInput.class.isAssignableFrom(systemParticipant.getClass())) {
      exceptions.addAll(checkEv((EvInput) systemParticipant));
    } else if (FixedFeedInInput.class.isAssignableFrom(systemParticipant.getClass())) {
      exceptions.addAll(checkFixedFeedIn((FixedFeedInInput) systemParticipant));
    } else if (HpInput.class.isAssignableFrom(systemParticipant.getClass())) {
      exceptions.addAll(checkHp((HpInput) systemParticipant));
    } else if (LoadInput.class.isAssignableFrom(systemParticipant.getClass())) {
      exceptions.addAll(checkLoad((LoadInput) systemParticipant));
    } else if (PvInput.class.isAssignableFrom(systemParticipant.getClass())) {
      exceptions.addAll(checkPv((PvInput) systemParticipant));
    } else if (StorageInput.class.isAssignableFrom(systemParticipant.getClass())) {
      exceptions.addAll(checkStorage((StorageInput) systemParticipant));
    } else if (WecInput.class.isAssignableFrom(systemParticipant.getClass())) {
      exceptions.addAll(checkWec((WecInput) systemParticipant));
    } else if (EvcsInput.class.isAssignableFrom(systemParticipant.getClass())) {
      exceptions.add(Try.apply(SystemParticipantValidationUtils::checkEvcs));
    } else {
      exceptions.add(
          new Failure<>(
              new InvalidEntityException(
                  "Validation failed due to: ", checkNotImplementedException(systemParticipant))));
    }

    return exceptions;
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
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  protected static List<Try<Void, InvalidEntityException>> checkType(
      SystemParticipantTypeInput systemParticipantTypeInput) {
    try {
      checkNonNull(systemParticipantTypeInput, "a system participant type");
    } catch (InvalidEntityException e) {
      return List.of(
          new Failure<>(
              new InvalidEntityException(
                  "Validation not possible because received object {"
                      + systemParticipantTypeInput
                      + "} was null",
                  e)));
    }

    List<Try<Void, InvalidEntityException>> exceptions = new ArrayList<>();

    if ((systemParticipantTypeInput.getCapex() == null)
        || (systemParticipantTypeInput.getOpex() == null)
        || (systemParticipantTypeInput.getsRated() == null)) {
      exceptions.add(
          new Failure<>(
              new InvalidEntityException(
                  "At least one of capex, opex, or sRated is null", systemParticipantTypeInput)));
    }

    exceptions.add(
        Try.apply(
            () ->
                detectNegativeQuantities(
                    new Quantity<?>[] {
                      systemParticipantTypeInput.getCapex(),
                      systemParticipantTypeInput.getOpex(),
                      systemParticipantTypeInput.getsRated()
                    },
                    systemParticipantTypeInput)));

    exceptions.add(
        Try.apply(
            () ->
                checkRatedPowerFactor(
                    systemParticipantTypeInput, systemParticipantTypeInput.getCosPhiRated())));

    if (BmTypeInput.class.isAssignableFrom(systemParticipantTypeInput.getClass())) {
      exceptions.addAll(checkBmType((BmTypeInput) systemParticipantTypeInput));
    } else if (ChpTypeInput.class.isAssignableFrom(systemParticipantTypeInput.getClass())) {
      exceptions.addAll(checkChpType((ChpTypeInput) systemParticipantTypeInput));
    } else if (EvTypeInput.class.isAssignableFrom(systemParticipantTypeInput.getClass())) {
      exceptions.add(checkEvType((EvTypeInput) systemParticipantTypeInput));
    } else if (HpTypeInput.class.isAssignableFrom(systemParticipantTypeInput.getClass())) {
      exceptions.add(checkHpType((HpTypeInput) systemParticipantTypeInput));
    } else if (StorageTypeInput.class.isAssignableFrom(systemParticipantTypeInput.getClass())) {
      exceptions.addAll(checkStorageType((StorageTypeInput) systemParticipantTypeInput));
    } else if (WecTypeInput.class.isAssignableFrom(systemParticipantTypeInput.getClass())) {
      exceptions.addAll(checkWecType((WecTypeInput) systemParticipantTypeInput));
    } else {
      exceptions.add(
          new Failure<>(
              new InvalidEntityException(
                  "Validation failed due to: ",
                  checkNotImplementedException(systemParticipantTypeInput))));
    }

    return exceptions;
  }

  /**
   * Validates a bmInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkBmType(BmTypeInput)} confirms a valid type
   * properties <br>
   *
   * @param bmInput BmInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkBm(BmInput bmInput) {
    return checkType(bmInput.getType());
  }

  /**
   * Validates a bmTypeInput if: <br>
   * - its active power gradient is not negative <br>
   * - its efficiency of assets inverter is between 0% and 100%
   *
   * @param bmTypeInput BmTypeInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkBmType(BmTypeInput bmTypeInput) {
    return List.of(
        Try.apply(
            () ->
                detectNegativeQuantities(
                    new Quantity<?>[] {bmTypeInput.getActivePowerGradient()}, bmTypeInput)),
        Try.apply(
            () ->
                isBetweenZeroAndHundredPercent(
                    bmTypeInput, bmTypeInput.getEtaConv(), "Efficiency of inverter")));
  }

  /**
   * Validates a chpInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkChpType(ChpTypeInput)} confirms a valid type
   * properties
   *
   * @param chpInput ChpInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkChp(ChpInput chpInput) {
    return checkType(chpInput.getType());
  }

  /**
   * Validates a chpTypeInput if: <br>
   * - its efficiency of the electrical inverter is between 0% and 100% <br>
   * - its thermal efficiency of the system is between 0% and 100% <br>
   * - its rated thermal power is positive <br>
   * - its needed self-consumption is not negative
   *
   * @param chpTypeInput ChpTypeInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkChpType(ChpTypeInput chpTypeInput) {
    return List.of(
        Try.apply(
            () ->
                detectNegativeQuantities(new Quantity<?>[] {chpTypeInput.getpOwn()}, chpTypeInput)),
        Try.apply(
            () ->
                detectZeroOrNegativeQuantities(
                    new Quantity<?>[] {chpTypeInput.getpThermal()}, chpTypeInput)),
        Try.apply(
            () ->
                isBetweenZeroAndHundredPercent(
                    chpTypeInput, chpTypeInput.getEtaEl(), "Electrical efficiency")),
        Try.apply(
            () ->
                isBetweenZeroAndHundredPercent(
                    chpTypeInput, chpTypeInput.getEtaThermal(), "Thermal efficiency")));
  }

  /**
   * Validates a EvInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkEvType(EvTypeInput)} confirms a valid type
   * properties
   *
   * @param evInput EvInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkEv(EvInput evInput) {
    return checkType(evInput.getType());
  }

  /**
   * Validates a EvTypeInput if: <br>
   * - its available battery capacity is positive <br>
   * - its energy consumption per driven kilometre is positive
   *
   * @param evTypeInput EvTypeInput to validate
   * @return a try object either containing an {@link InvalidEntityException} or an empty Success
   */
  private static Try<Void, InvalidEntityException> checkEvType(EvTypeInput evTypeInput) {
    return Try.apply(
        () ->
            detectZeroOrNegativeQuantities(
                new Quantity<?>[] {evTypeInput.geteStorage(), evTypeInput.geteCons()},
                evTypeInput));
  }

  /**
   * Validates a FixedFeedInInput if: <br>
   * - its rated apparent power is not negative <br>
   * - its rated power factor is between 0 and 1
   *
   * @param fixedFeedInInput FixedFeedInInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkFixedFeedIn(
      FixedFeedInInput fixedFeedInInput) {
    return List.of(
        Try.apply(
            () ->
                detectNegativeQuantities(
                    new Quantity<?>[] {fixedFeedInInput.getsRated()}, fixedFeedInInput)),
        Try.apply(
            () -> checkRatedPowerFactor(fixedFeedInInput, fixedFeedInInput.getCosPhiRated())));
  }

  /**
   * Validates a HpInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkHpType(HpTypeInput)} confirms a valid type
   * properties
   *
   * @param hpInput HpInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkHp(HpInput hpInput) {
    return checkType(hpInput.getType());
  }

  /**
   * Validates a HpTypeInput if: <br>
   * - its rated thermal power is positive
   *
   * @param hpTypeInput HpTypeInput to validate
   * @return a try object either containing an {@link InvalidEntityException} or an empty Success
   */
  private static Try<Void, InvalidEntityException> checkHpType(HpTypeInput hpTypeInput) {
    return Try.apply(
        () ->
            detectZeroOrNegativeQuantities(
                new Quantity<?>[] {hpTypeInput.getpThermal()}, hpTypeInput));
  }

  /**
   * Validates a LoadInput if: <br>
   * - its standard load profile is not null <br>
   * - its rated apparent power is not negative <br>
   * - its annual energy consumption is not negative <br>
   * - its rated power factor is between 0 and 1
   *
   * @param loadInput LoadInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkLoad(LoadInput loadInput) {
    List<Try<Void, InvalidEntityException>> exceptions = new ArrayList<>();

    if (loadInput.getLoadProfile() == null) {
      exceptions.add(
          new Failure<>(
              new InvalidEntityException("No standard load profile defined for load", loadInput)));
    }

    exceptions.add(
        Try.apply(
            () ->
                detectNegativeQuantities(
                    new Quantity<?>[] {loadInput.getsRated(), loadInput.geteConsAnnual()},
                    loadInput)));
    exceptions.add(Try.apply(() -> checkRatedPowerFactor(loadInput, loadInput.getCosPhiRated())));

    return exceptions;
  }

  /**
   * Validates a PvInput if: <br>
   * - its rated apparent power is not negative <br>
   * - its albedo value of the plant's surrounding is between 0 and 1 <br>
   * - its inclination in a compass direction (azimuth) is between -90° and 90° <br>
   * - its efficiency of the asset's inverter (etaConv) is between 0% and 100% <br>
   * - its tilted inclination from horizontal (elevation angle) is between 0° and 90° <br>
   * - its rated power factor is between 0 and 1
   *
   * @param pvInput PvInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkPv(PvInput pvInput) {
    return List.of(
        Try.apply(() -> detectNegativeQuantities(new Quantity<?>[] {pvInput.getsRated()}, pvInput)),
        Try.apply(() -> checkAlbedo(pvInput)),
        Try.apply(() -> checkAzimuth(pvInput)),
        Try.apply(
            () ->
                isBetweenZeroAndHundredPercent(
                    pvInput, pvInput.getEtaConv(), "Efficiency of the converter")),
        Try.apply(() -> checkElevationAngle(pvInput)),
        Try.apply(() -> checkRatedPowerFactor(pvInput, pvInput.getCosPhiRated())));
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
  private static void checkElevationAngle(PvInput pvInput) {
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
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkStorage(StorageInput storageInput) {
    return checkType(storageInput.getType());
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
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkStorageType(
      StorageTypeInput storageTypeInput) {
    List<Try<Void, InvalidEntityException>> exceptions = new ArrayList<>();

    if (storageTypeInput.getLifeCycle() < 0) {
      exceptions.add(
          new Failure<>(
              new InvalidEntityException(
                  "Permissible amount of life cycles of the storage type must be zero or positive",
                  storageTypeInput)));
    }

    exceptions.add(
        Try.apply(
            () ->
                isBetweenZeroAndHundredPercent(
                    storageTypeInput,
                    storageTypeInput.getEta(),
                    "Efficiency of the electrical converter")));
    exceptions.add(
        Try.apply(
            () ->
                isBetweenZeroAndHundredPercent(
                    storageTypeInput,
                    storageTypeInput.getDod(),
                    "Maximum permissible depth of discharge")));
    exceptions.add(
        Try.apply(
            () ->
                detectNegativeQuantities(
                    new Quantity<?>[] {
                      storageTypeInput.getpMax(),
                      storageTypeInput.getActivePowerGradient(),
                      storageTypeInput.getLifeTime()
                    },
                    storageTypeInput)));
    exceptions.add(
        Try.apply(
            () ->
                detectZeroOrNegativeQuantities(
                    new Quantity<?>[] {storageTypeInput.geteStorage()}, storageTypeInput)));

    return exceptions;
  }

  /**
   * Validates a WecInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkWecType(WecTypeInput)} confirms a valid type
   * properties
   *
   * @param wecInput WecInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkWec(WecInput wecInput) {
    return checkType(wecInput.getType());
  }

  /**
   * Validates a WecTypeInput if: <br>
   * - its efficiency of the assets converter is between 0% and 100% <br>
   * - its rotor area is not negative <br>
   * - its height of the rotor hub is not negative
   *
   * @param wecTypeInput WecTypeInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkWecType(WecTypeInput wecTypeInput) {
    return List.of(
        Try.apply(
            () ->
                isBetweenZeroAndHundredPercent(
                    wecTypeInput, wecTypeInput.getEtaConv(), "Efficiency of the converter")),
        Try.apply(
            () ->
                detectNegativeQuantities(
                    new Quantity<?>[] {wecTypeInput.getRotorArea(), wecTypeInput.getHubHeight()},
                    wecTypeInput)));
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
