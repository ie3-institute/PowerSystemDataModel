/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import static edu.ie3.datamodel.models.StandardUnits.*;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.TryException;
import edu.ie3.datamodel.models.input.UniqueInputEntity;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.Failure;
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
   * Validates a system participant if:
   *
   * <ul>
   *   <li>it is not null
   *   <li>its qCharacteristics are not null
   * </ul>
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
    Try<Void, InvalidEntityException> isNull =
        checkNonNull(systemParticipant, "a system participant");

    if (isNull.isFailure()) {
      return List.of(isNull);
    }

    List<Try<Void, InvalidEntityException>> exceptions = new ArrayList<>();

    exceptions.add(
        Try.ofVoid(
            systemParticipant.getqCharacteristics() == null,
            () ->
                new InvalidEntityException(
                    "Reactive power characteristics of system participant is not defined",
                    systemParticipant)));

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
      exceptions.addAll(checkEvcs((EvcsInput) systemParticipant));
    } else {
      logNotImplemented(systemParticipant);
    }

    return exceptions;
  }

  /**
   * Validates a system participant type if:
   *
   * <ul>
   *   <li>it is not null
   *   <li>capex is not null and not negative
   *   <li>opex is not null and not negative
   *   <li>sRated is not null and not negative
   *   <li>cosphiRated is between zero and one
   * </ul>
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
    Try<Void, InvalidEntityException> isNull =
        checkNonNull(systemParticipantTypeInput, "a system participant type");

    if (isNull.isFailure()) {
      return List.of(isNull);
    }

    List<Try<Void, InvalidEntityException>> exceptions = new ArrayList<>();

    exceptions.add(
        Try.ofVoid(
            (systemParticipantTypeInput.getCapex() == null)
                || (systemParticipantTypeInput.getOpex() == null)
                || (systemParticipantTypeInput.getsRated() == null),
            () ->
                new InvalidEntityException(
                    "At least one of capex, opex, or sRated is null", systemParticipantTypeInput)));

    try {
      exceptions.add(
          Try.ofVoid(
              () ->
                  detectNegativeQuantities(
                      new Quantity<?>[] {
                        systemParticipantTypeInput.getCapex(),
                        systemParticipantTypeInput.getOpex(),
                        systemParticipantTypeInput.getsRated()
                      },
                      systemParticipantTypeInput),
              InvalidEntityException.class));
    } catch (TryException e) {
      Throwable wronglyCaught = e.getCause();
      exceptions.add(
          Failure.ofVoid(new InvalidEntityException(wronglyCaught.getMessage(), wronglyCaught)));
    }

    exceptions.add(
        Try.ofVoid(
            () ->
                checkRatedPowerFactor(
                    systemParticipantTypeInput, systemParticipantTypeInput.getCosPhiRated()),
            InvalidEntityException.class));

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
      logNotImplemented(systemParticipantTypeInput);
    }

    return exceptions;
  }

  /**
   * Validates a bmInput if: <br>
   *
   * <ul>
   *   <li>{@link SystemParticipantValidationUtils#checkBmType(BmTypeInput)} confirms a valid type
   *       properties
   * </ul>
   *
   * @param bmInput BmInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkBm(BmInput bmInput) {
    return checkType(bmInput.getType());
  }

  /**
   * Validates a bmTypeInput if:
   *
   * <ul>
   *   <li>its active power gradient is not negative#
   *   <li>its efficiency of assets inverter is between 0% and 100%
   * </ul>
   *
   * @param bmTypeInput BmTypeInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkBmType(BmTypeInput bmTypeInput) {
    return Try.ofVoid(
        InvalidEntityException.class,
        () ->
            detectNegativeQuantities(
                new Quantity<?>[] {bmTypeInput.getActivePowerGradient()}, bmTypeInput),
        () ->
            isBetweenZeroAndHundredPercent(
                bmTypeInput, bmTypeInput.getEtaConv(), "Efficiency of inverter"));
  }

  /**
   * Validates a chpInput if:
   *
   * <ul>
   *   <li>{@link SystemParticipantValidationUtils#checkChpType(ChpTypeInput)} confirms a valid type
   *       properties
   * </ul>
   *
   * @param chpInput ChpInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkChp(ChpInput chpInput) {
    return checkType(chpInput.getType());
  }

  /**
   * Validates a chpTypeInput if:
   *
   * <ul>
   *   <li>its efficiency of the electrical inverter is between 0% and 100%
   *   <li>its thermal efficiency of the system is between 0% and 100%
   *   <li>its rated thermal power is positive
   *   <li>its needed self-consumption is not negative
   * </ul>
   *
   * @param chpTypeInput ChpTypeInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkChpType(ChpTypeInput chpTypeInput) {
    return Try.ofVoid(
        InvalidEntityException.class,
        () -> detectNegativeQuantities(new Quantity<?>[] {chpTypeInput.getpOwn()}, chpTypeInput),
        () ->
            detectZeroOrNegativeQuantities(
                new Quantity<?>[] {chpTypeInput.getpThermal()}, chpTypeInput),
        () ->
            isBetweenZeroAndHundredPercent(
                chpTypeInput, chpTypeInput.getEtaEl(), "Electrical efficiency"),
        () ->
            isBetweenZeroAndHundredPercent(
                chpTypeInput, chpTypeInput.getEtaThermal(), "Thermal efficiency"));
  }

  /**
   * Validates a EvInput if:
   *
   * <ul>
   *   <li>{@link SystemParticipantValidationUtils#checkEvType(EvTypeInput)} confirms a valid type
   *       properties
   * </ul>
   *
   * @param evInput EvInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkEv(EvInput evInput) {
    return checkType(evInput.getType());
  }

  /**
   * Validates a EvTypeInput if:
   *
   * <ul>
   *   <li>its available battery capacity is positive
   *   <li>its energy consumption per driven kilometre is positive
   * </ul>
   *
   * @param evTypeInput EvTypeInput to validate
   * @return a try object either containing an {@link InvalidEntityException} or an empty Success
   */
  private static Try<Void, InvalidEntityException> checkEvType(EvTypeInput evTypeInput) {
    return Try.ofVoid(
        () ->
            detectZeroOrNegativeQuantities(
                new Quantity<?>[] {
                  evTypeInput.geteStorage(), evTypeInput.geteCons(),
                },
                evTypeInput),
        InvalidEntityException.class);
  }

  /**
   * Validates a FixedFeedInInput if:
   *
   * <ul>
   *   <li>its rated apparent power is not negative
   *   <li>its rated power factor is between 0 and 1
   * </ul>
   *
   * @param fixedFeedInInput FixedFeedInInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkFixedFeedIn(
      FixedFeedInInput fixedFeedInInput) {
    return Try.ofVoid(
        InvalidEntityException.class,
        () ->
            detectNegativeQuantities(
                new Quantity<?>[] {fixedFeedInInput.getsRated()}, fixedFeedInInput),
        () -> checkRatedPowerFactor(fixedFeedInInput, fixedFeedInInput.getCosPhiRated()));
  }

  /**
   * Validates a HpInput if:
   *
   * <ul>
   *   <li>{@link SystemParticipantValidationUtils#checkHpType(HpTypeInput)} confirms a valid type
   *       properties
   * </ul>
   *
   * @param hpInput HpInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkHp(HpInput hpInput) {
    return checkType(hpInput.getType());
  }

  /**
   * Validates a HpTypeInput if:
   *
   * <ul>
   *   <li>its rated thermal power is positive
   * </ul>
   *
   * @param hpTypeInput HpTypeInput to validate
   * @return a try object either containing an {@link InvalidEntityException} or an empty Success
   */
  private static Try<Void, InvalidEntityException> checkHpType(HpTypeInput hpTypeInput) {
    return Try.ofVoid(
        () ->
            detectZeroOrNegativeQuantities(
                new Quantity<?>[] {hpTypeInput.getpThermal()}, hpTypeInput),
        InvalidEntityException.class);
  }

  /**
   * Validates a LoadInput if:
   *
   * <ul>
   *   <li>its standard load profile is not null
   *   <li>its rated apparent power is not negative
   *   <li>its annual energy consumption is not negative
   *   <li>its rated power factor is between 0 and 1
   * </ul>
   *
   * @param loadInput LoadInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkLoad(LoadInput loadInput) {
    List<Try<Void, InvalidEntityException>> exceptions = new ArrayList<>();

    exceptions.add(
        Try.ofVoid(
            loadInput.getLoadProfile() == null,
            () ->
                new InvalidEntityException(
                    "No standard load profile defined for load", loadInput)));

    exceptions.addAll(
        Try.ofVoid(
            InvalidEntityException.class,
            () ->
                detectNegativeQuantities(
                    new Quantity<?>[] {loadInput.getsRated(), loadInput.geteConsAnnual()},
                    loadInput),
            () -> checkRatedPowerFactor(loadInput, loadInput.getCosPhiRated())));

    return exceptions;
  }

  /**
   * Validates a PvInput if:
   *
   * <ul>
   *   <li>its rated apparent power is not negative
   *   <li>its albedo value of the plant's surrounding is between 0 and 1
   *   <li>its inclination in a compass direction (azimuth) is between -90° and 90°
   *   <li>its efficiency of the asset's inverter (etaConv) is between 0% and 100%
   *   <li>its tilted inclination from horizontal (elevation angle) is between 0° and 90°
   *   <li>its rated power factor is between 0 and 1
   * </ul>
   *
   * @param pvInput PvInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkPv(PvInput pvInput) {
    return Try.ofVoid(
        InvalidEntityException.class,
        () -> detectNegativeQuantities(new Quantity<?>[] {pvInput.getsRated()}, pvInput),
        () -> checkAlbedo(pvInput),
        () -> checkAzimuth(pvInput),
        () ->
            isBetweenZeroAndHundredPercent(
                pvInput, pvInput.getEtaConv(), "Efficiency of the converter"),
        () -> checkElevationAngle(pvInput),
        () -> checkRatedPowerFactor(pvInput, pvInput.getCosPhiRated()));
  }

  /**
   * Check if albedo of pvInput is between 0 and 1
   *
   * @param pvInput PvInput to validate
   */
  private static void checkAlbedo(PvInput pvInput) throws InvalidEntityException {
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
  private static void checkAzimuth(PvInput pvInput) throws InvalidEntityException {
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
  private static void checkElevationAngle(PvInput pvInput) throws InvalidEntityException {
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
   * Validates a StorageInput if:
   *
   * <ul>
   *   <li>{@link SystemParticipantValidationUtils#checkStorageType(StorageTypeInput)} confirms a
   *       valid type properties
   * </ul>
   *
   * @param storageInput StorageInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkStorage(StorageInput storageInput) {
    return checkType(storageInput.getType());
  }

  /**
   * Validates a StorageTypeInput if:
   *
   * <ul>
   *   <li>its permissible amount of full cycles is not negative
   *   <li>its efficiency of the electrical converter is between 0% and 100%
   *   <li>its maximum permissible depth of discharge is between 0% and 100%
   *   <li>its active power gradient is not negative
   *   <li>its battery capacity is positive
   *   <li>its maximum permissible active power (in-feed or consumption) is not negative
   *   <li>its permissible hours of full use is not negative
   * </ul>
   *
   * @param storageTypeInput StorageTypeInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkStorageType(
      StorageTypeInput storageTypeInput) {
    List<Try<Void, InvalidEntityException>> exceptions = new ArrayList<>();

    exceptions.addAll(
        Try.ofVoid(
            InvalidEntityException.class,
            () ->
                isBetweenZeroAndHundredPercent(
                    storageTypeInput,
                    storageTypeInput.getEta(),
                    "Efficiency of the electrical converter"),
            () ->
                detectNegativeQuantities(
                    new Quantity<?>[] {
                      storageTypeInput.getpMax(), storageTypeInput.getActivePowerGradient(),
                    },
                    storageTypeInput),
            () ->
                detectZeroOrNegativeQuantities(
                    new Quantity<?>[] {storageTypeInput.geteStorage()}, storageTypeInput)));

    return exceptions;
  }

  /**
   * Validates a WecInput if:
   *
   * <ul>
   *   <li>{@link SystemParticipantValidationUtils#checkWecType(WecTypeInput)} confirms a valid type
   *       properties
   * </ul>
   *
   * @param wecInput WecInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkWec(WecInput wecInput) {
    return checkType(wecInput.getType());
  }

  /**
   * Validates a WecTypeInput if:
   *
   * <ul>
   *   <li>its efficiency of the assets converter is between 0% and 100%
   *   <li>its rotor area is not negative
   *   <li>its height of the rotor hub is not negative
   * </ul>
   *
   * @param wecTypeInput WecTypeInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkWecType(WecTypeInput wecTypeInput) {
    return Try.ofVoid(
        InvalidEntityException.class,
        () ->
            isBetweenZeroAndHundredPercent(
                wecTypeInput, wecTypeInput.getEtaConv(), "Efficiency of the converter"),
        () ->
            detectNegativeQuantities(
                new Quantity<?>[] {wecTypeInput.getRotorArea(), wecTypeInput.getHubHeight()},
                wecTypeInput));
  }

  /**
   * Validates a EvcsInput if:
   *
   * <ul>
   *   <li>its number of charging points is < 1
   *   <li>its rated power factor is between 0 and 1
   *   <li>its rated apparent power is not negative
   * </ul>
   *
   * @param evcsInput EvcsInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  private static List<Try<Void, InvalidEntityException>> checkEvcs(EvcsInput evcsInput) {
    Try.VoidSupplier<InvalidEntityException> chargingPointValidation =
        () -> {
          if (evcsInput.getChargingPoints() < 1)
            throw new InvalidEntityException(
                "Invalid number of charging points: '"
                    + evcsInput.getChargingPoints()
                    + "'. At least one charging point is needed.",
                evcsInput);
        };

    return Try.ofVoid(
        InvalidEntityException.class,
        chargingPointValidation,
        () -> checkRatedPowerFactor(evcsInput, evcsInput.getCosPhiRated()),
        () ->
            detectNegativeQuantities(new Quantity[] {evcsInput.getType().getsRated()}, evcsInput));
  }

  /**
   * Validates if the rated power factor is between 0 and 1, otherwise throws an {@link
   * InvalidEntityException}
   *
   * @param input entity to validate
   * @param cosPhiRated rated power factor to check
   */
  private static void checkRatedPowerFactor(UniqueInputEntity input, double cosPhiRated)
      throws InvalidEntityException {
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
      UniqueInputEntity input, ComparableQuantity<Dimensionless> value, String string)
      throws InvalidEntityException {
    if (value.isLessThan(Quantities.getQuantity(0d, Units.PERCENT))
        || value.isGreaterThan(Quantities.getQuantity(100d, Units.PERCENT)))
      throw new InvalidEntityException(
          string + " of " + input.getClass().getSimpleName() + " must be between 0% and 100%",
          input);
  }
}
