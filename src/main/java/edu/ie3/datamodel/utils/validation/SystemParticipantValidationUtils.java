/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
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
   * - its operator is not null <br>
   * - its qCharacteristics are not null
   *
   * @param systemParticipant systemParticipant to validate
   */
  public static void check(SystemParticipantInput systemParticipant) {
    //Check if null
    checkNonNull(systemParticipant, "a system participant");
    //Check if node is null
    if (systemParticipant.getNode() == null)
      throw new InvalidEntityException("Node of system participant is null", systemParticipant);
    //Check if operator is null
    if (systemParticipant.getOperator() == null)
      throw new InvalidEntityException("No operator for system participant assigned", systemParticipant);
    //Check if qCharacteristics is null
    if (systemParticipant.getqCharacteristics() == null)
      throw new InvalidEntityException("qCharacteristics of system participant not defined", systemParticipant);

    //Further checks for subclasses
    if (BmInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkBm((BmInput) systemParticipant);
    if (ChpInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkChp((ChpInput) systemParticipant);
    if (EvInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkEv((EvInput) systemParticipant);
    if (FixedFeedInInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkFixedFeedIn((FixedFeedInInput) systemParticipant);
    if (HpInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkHp((HpInput) systemParticipant);
    if (LoadInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkLoad((LoadInput) systemParticipant);
    if (PvInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkPv((PvInput) systemParticipant);
    if (StorageInput.class.isAssignableFrom(systemParticipant.getClass()))
      checkStorage((StorageInput) systemParticipant);
  }

  //TODO @NSteffan: Ist das eine gute (und funktionierende) Lösung für weniger Redundanz?
  /**
   * Validates a system participant type if: <br>
   * - capex, opex, or sRated are null <br>
   * - capex, opex, or sRated are negative
   *
   * @param systemParticipantTypeInput systemParticipant Type to validate
   */
  public static void checkType(SystemParticipantTypeInput systemParticipantTypeInput) {
    //Check if any values are null
    if ((systemParticipantTypeInput.getCapex() == null)
            || (systemParticipantTypeInput.getOpex() == null)
            || (systemParticipantTypeInput.getsRated() == null))
      throw new InvalidEntityException("at least one of capex, opex, or sRated of the system participant is null",
              systemParticipantTypeInput);
    //Check if any values are negative
    detectNegativeQuantities(
        new Quantity<?>[] {
            systemParticipantTypeInput.getCapex(),
            systemParticipantTypeInput.getOpex(),
            systemParticipantTypeInput.getsRated()
        },
        systemParticipantTypeInput);
  }

  /**
   * Validates a bmInput if: <br>
   * - feed in tariff is null <br>
   * - {@link SystemParticipantValidationUtils#checkBmType(BmTypeInput)} confirms a
   * valid type properties <br>
   *
   * @param bmInput BmInput to validate
   */
  public static void checkBm(BmInput bmInput) {
    //Check if feed in tariff is null
    if (bmInput.getFeedInTariff() == null)
      throw new InvalidEntityException("Feed in tariff of biomass unit is null", bmInput);
    //TODO @NSteffan: booleans don't need to be checked, right?
    //Check BmType
    checkBmType(bmInput.getType());
  }

  /**
   * Validates a bmTypeInput if: <br>
   * - it is not null <br>
   * - common system participants values (capex, opex, sRated) are null or negative <br>
   * - its active power gradient is null or negative <br>
   * - its efficiency of assets inverter is null or negative
   *
   * @param bmTypeInput BmTypeInput to validate
   */
  public static void checkBmType(BmTypeInput bmTypeInput) {
    //Check if null
    checkNonNull(bmTypeInput, "a bmTypeInput");
    //Check if any common values of system participants are null or negative
    try {
      checkType(bmTypeInput);
    } catch (InvalidEntityException e) {
      throw new InvalidEntityException("At least one value of bmTypeInput is null", bmTypeInput);
    }
    //Check if active power gradient is null
    if (bmTypeInput.getActivePowerGradient() == null)
      throw new InvalidEntityException("Active power gradient of bmTypeInput is null", bmTypeInput);
    //Check if efficiency of assets inverter is null
    if (bmTypeInput.getEtaConv() == null)
      throw new InvalidEntityException("Efficiency of assets inverter of bmTypeInput is null", bmTypeInput);
    //Check if any values are negative
    detectNegativeQuantities(
            new Quantity<?>[] {
                    bmTypeInput.getActivePowerGradient(),
                    bmTypeInput.getEtaConv(),
            },
            bmTypeInput);
    //TODO @NSteffan: Einschränkung < / <= 0 richtig?
  }

  /**
   * Validates a chpInput if: <br>
   * - thermal bus is null <br>
   * - thermal storage is null <br>
   * - {@link SystemParticipantValidationUtils#checkChpType(ChpTypeInput)} confirms a
   * valid type properties
   *
   * @param chpInput ChpInput to validate
   */
  public static void checkChp(ChpInput chpInput) {
    //Check if thermal bus is null
    if (chpInput.getThermalBus() == null)
      throw new InvalidEntityException("Thermal bus of CHP unit is null", chpInput);
    //Check if thermal storage is null
    if (chpInput.getThermalStorage() == null)
      throw new InvalidEntityException("Thermal storage of CHP unit is null", chpInput);
    //Check ChpType
    checkChpType(chpInput.getType());
  }

  /**
   * Validates a chpTypeInput if: <br>
   * - it is not null <br>
   * - common system participants values (capex, opex, sRated) are null or negative <br>
   * - its efficiency of the electrical inverter is null or negative <br>
   * - its thermal efficiency of the system is null or negative <br>
   * - its rated thermal power is null or negative <br>
   * - its needed self-consumption is null or negative
   *
   * @param chpTypeInput ChpTypeInput to validate
   */
  public static void checkChpType(ChpTypeInput chpTypeInput) {
    //Check if null
    checkNonNull(chpTypeInput, "a chpTypeInput");
    //Check if any common values of system participants are null or negative
    try {
      checkType(chpTypeInput);
    } catch (InvalidEntityException e) {
      throw new InvalidEntityException("At least one value of chpTypeInput is null", chpTypeInput);
    }
    //Check if efficiency of the electrical inverter is null
    if (chpTypeInput.getEtaEl() == null)
      throw new InvalidEntityException("Efficiency of the electrical inverter of chpTypeInput is null", chpTypeInput);
    //Check if thermal efficiency of the system is null
    if (chpTypeInput.getEtaThermal() == null)
      throw new InvalidEntityException("Thermal efficiency of the system of chpTypeInput is null", chpTypeInput);
    //Check if rated thermal power is null
    if (chpTypeInput.getpThermal() == null)
      throw new InvalidEntityException("Rated thermal power of chpTypeInput is null", chpTypeInput);
    //Check if needed self-consumption is null
    if (chpTypeInput.getpOwn() == null)
      throw new InvalidEntityException("Needed self-consumption of chpTypeInput is null", chpTypeInput);
    //Check if any values are negative
    detectNegativeQuantities(
            new Quantity<?>[] {
                    chpTypeInput.getEtaEl(),
                    chpTypeInput.getEtaThermal(),
                    chpTypeInput.getpThermal(),
                    chpTypeInput.getpOwn()
            },
            chpTypeInput);
    //TODO @NSteffan: Einschränkung < / <= 0 richtig?
  }

  /**
   * Validates a EvInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkEvType(EvTypeInput)} confirms a
   * valid type properties
   *
   * @param evInput EvInput to validate
   */
  public static void checkEv(EvInput evInput) {
    //Check EvType
    checkEvType(evInput.getType());
  }

  /**
   * Validates a EvTypeInput if: <br>
   * - it is not null <br>
   * - common system participants values (capex, opex, sRated) are null or negative <br>
   * - its available battery capacity is null or negative <br>
   * - its energy consumption per driven kilometre is null or negative <br>
   * - its rated power factor is not null and between 0 and 1
   *
   * @param evTypeInput EvTypeInput to validate
   */
  public static void checkEvType(EvTypeInput evTypeInput) {
    //Check if null
    checkNonNull(evTypeInput, "a evTypeInput");
    //Check if any common values of system participants are null or negative
    try {
      checkType(evTypeInput);
    } catch (InvalidEntityException e) {
      throw new InvalidEntityException("At least one value of evTypeInput is null", evTypeInput);
    }
    //Check if available battery capacity is null
    if (evTypeInput.geteStorage() == null)
      throw new InvalidEntityException("Available battery capacity of evTypeInput is null", evTypeInput);
    //Check if energy consumption per driven kilometre is null
    if (evTypeInput.geteCons() == null)
      throw new InvalidEntityException("Energy consumption per driven kilometre of evTypeInput is null", evTypeInput);
    //Check if rated power factor is between 0 and 1
    if (evTypeInput.getCosPhiRated() < 0d || evTypeInput.getCosPhiRated() > 1d)
      throw new InvalidEntityException("Rated power factor of evTypeInput must be between zero and one", evTypeInput);
    //Check if any values are zero or negative
    detectZeroOrNegativeQuantities(
            new Quantity<?>[] {
                    evTypeInput.geteStorage(),
                    evTypeInput.geteCons()
            },
            evTypeInput);
    //TODO @NSteffan: Einschränkung < / <= 0 richtig?
  }

  /**
   * Validates a FixedFeedInInput if: <br>
   * - its rated apparent power is not null or negative <br>
   * - its rated power factor is between 0 and 1
   *
   * @param fixedFeedInInput FixedFeedInInput to validate
   */
  public static void checkFixedFeedIn(FixedFeedInInput fixedFeedInInput) {
    //Check if rated apparent power is null or negative
    if (fixedFeedInInput.getsRated() == null)
      throw new InvalidEntityException("Rated apparent power of fixed feed-in unit is null", fixedFeedInInput);
    detectZeroOrNegativeQuantities(new Quantity<?>[] { fixedFeedInInput.getsRated() }, fixedFeedInInput);
        //TODO NSteffan: Can fixed feed-in sRated be zero/negative?
    //Check if rated power factor is between 0 and 1
    if (fixedFeedInInput.getCosPhiRated() < 0d || fixedFeedInInput.getCosPhiRated() > 1d)
      throw new InvalidEntityException("Rated power factor of fixed feed-in unit must be between zero and one", fixedFeedInInput);
  }

  /**
   * Validates a HpInput if: <br>
   * - its thermal bus is not null
   * - {@link SystemParticipantValidationUtils#checkHpType(HpTypeInput)} confirms a
   * valid type properties
   *
   * @param hpInput HpInput to validate
   */
  public static void checkHp(HpInput hpInput) {
    //Check if thermal bus is null
    if (hpInput.getThermalBus() == null)
      throw new InvalidEntityException("Thermal bus of hpInput is null", hpInput);
    //Check HpType
    checkHpType(hpInput.getType());
  }

  /**
   * Validates a HpTypeInput if: <br>
   * - it is not null <br>
   * - common system participants values (capex, opex, sRated) are null or negative <br>
   * - its rated power factor is between 0 and 1 <br>
   * - its rated thermal power is not null and positive
   *
   * @param hpTypeInput HpTypeInput to validate
   */
  public static void checkHpType(HpTypeInput hpTypeInput) {
    //Check if null
    checkNonNull(hpTypeInput, "a hpTypeInput");
    //Check if any common values of system participants are null or negative
    try {
      checkType(hpTypeInput);
    } catch (InvalidEntityException e) {
      throw new InvalidEntityException("At least one value of hpTypeInput is null", hpTypeInput);
    }
    //Check if rated power factor is between 0 and 1
    if (hpTypeInput.getCosPhiRated() < 0d || hpTypeInput.getCosPhiRated() > 1d)
      throw new InvalidEntityException("Rated power factor of hpTypeInput must be between zero and one", hpTypeInput);
    //Check if rated thermal power is null or negative
    detectZeroOrNegativeQuantities(
        new Quantity<?>[] {
            hpTypeInput.getpThermal()
        },
        hpTypeInput);
    //TODO @NSteffan: Einschränkung < / <= 0 richtig?
  }

  /**
   * Validates a LoadInput if: <br>
   * - its standard load profile is not null <br>
   * - its rated apparent power is not null and positive <br>
   * - its annual energy consumption is not null and positive <br>
   * - its rated power factor is between 0 and 1
   *
   * @param loadInput LoadInput to validate
   */
  public static void checkLoad(LoadInput loadInput) {
    //Check if standard load profile is null
    if (loadInput.getStandardLoadProfile() == null)
      throw new InvalidEntityException("No standard load profile defined for loadInput", loadInput);
    //Check if rated apparent power is null
    if (loadInput.getsRated() == null)
      throw new InvalidEntityException("Rated apparent power of loadInput is null", loadInput);
    //Check if annual energy consumption is null
    if (loadInput.geteConsAnnual() == null)
      throw new InvalidEntityException("Annual energy consumption of loadInput is null", loadInput);
    //Check if values are zero or negative
    detectZeroOrNegativeQuantities(
        new Quantity<?>[] {
            loadInput.getsRated(),
            loadInput.geteConsAnnual()
        },
        loadInput);
        //TODO NSteffan: Can sRated be zero/negative/positive?
    //Check if rated power factor is between 0 and 1
    if (loadInput.getCosPhiRated() < 0d || loadInput.getCosPhiRated() > 1d)
      throw new InvalidEntityException("Rated power factor of loadInput must be between zero and one", loadInput);
  }

  /**
   * Validates a PvInput if: <br>
   * - its rated apparent power is not null and positive <br>
   * - its inclination in a compass direction (azimuth) is not null and is between -90° and 90° <br>
   * - its efficiency of the asset's inverter (etaConv) is not null and is between 0% and 100% <br>
   * - its tilted inclination from horizontal (height) is not null and is between 0° and 90° <br>
   * . its albedo value of the plant's surrounding is between 0 and 1 <br>
   * - its rated power factor is between 0 and 1
   *
   * @param pvInput PvInput to validate
   */
  public static void checkPv(PvInput pvInput) {
    //Check if any values are null
    if ((pvInput.getsRated() == null)
        || (pvInput.getAzimuth() == null)
        || (pvInput.getEtaConv() == null)
        || (pvInput.getHeight() == null))
      throw new InvalidEntityException("at least one value of pvInput is null", pvInput);
    //Check if rated apparent power is zero or negative
    detectZeroOrNegativeQuantities(new Quantity<?>[] { pvInput.getsRated() }, pvInput);
        //TODO NSteffan: Can sRated be zero/negative/positive?
    //Check if albedo is between 0 and 1
    if (pvInput.getAlbedo() < 0d || pvInput.getAlbedo() > 1d)
      throw new InvalidEntityException("Albedo of the plant's surrounding of pvInput must be between zero and one", pvInput);
    //Check if azimuth angle is between -90° and 90°
    if (pvInput.getAzimuth().getValue().doubleValue() < -90d || pvInput.getAzimuth().getValue().doubleValue() > 90d)
      throw new InvalidEntityException("Azimuth angle of pvInput must be between -90° (east) and 90° (west)", pvInput);
    //Check if efficiency of the assets converter (etaConv) is between 0% and 100%
    if (pvInput.getEtaConv().getValue().doubleValue() < 0d || pvInput.getEtaConv().getValue().doubleValue() > 100d)
      throw new InvalidEntityException("Efficiency of the assets converter of pvInput must be between 0% and 100%", pvInput);
    //Check if tilted inclination from horizontal is between 0° and 90°
    if (pvInput.getHeight().getValue().doubleValue() < 0d || pvInput.getHeight().getValue().doubleValue() > 90d )
      throw new InvalidEntityException("Tilted inclination from horizontal of pvInput must be between 0° and 90°", pvInput);
        //TODO NSteffan: Checks for boundaries for albedo, etaConv, azimuth and height correct?
    //Check if rated power factor is between 0 and 1
    if (pvInput.getCosPhiRated() < 0d || pvInput.getCosPhiRated() > 1d)
      throw new InvalidEntityException("Rated power factor of pvInput must be between zero and one", pvInput);
        //TODO NSteffan: Keine Einschränkungen für kG, kT -> richtig?
  }

  /**
   * Validates a StorageInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkStorageType(StorageTypeInput)} confirms a
   * valid type properties
   *
   * @param storageInput StorageInput to validate
   */
  public static void checkStorage(StorageInput storageInput) {
    //Check StorageType
    checkStorageType(storageInput.getType());
  }

  /**
   * Validates a StorageTypeInput if: <br>
   * - it is not null <br>
   * - common system participants values (capex, opex, sRated) are null or negative <br>
   * - its rated power factor is between 0 and 1 <br>
   * - its permissible amount of full cycles is positive <br>
   * - its efficiency of the electrical converter is not null and between 0% and 100% <br>
   * - its maximum permissible depth of discharge is not null and between 0% and 100% <br>
   * - its active power gradient is not null and positive <br>
   * - its battery capacity is not null and positive <br>
   * - its maximum permissible active power (in-feed or consumption) is not null and positive <br>
   * - its permissible hours of full use is not null and positive
   *
   * @param storageTypeInput StorageTypeInput to validate
   */
  public static void checkStorageType(StorageTypeInput storageTypeInput) {
    //Check if null
    checkNonNull(storageTypeInput, "a storageInput type");
    //Check if any common values of system participants are null or negative
    try {
      checkType(storageTypeInput);
    } catch (InvalidEntityException e) {
      throw new InvalidEntityException("At least one value of storageTypeInput is null", storageTypeInput);
    }
    //Check if any values are null
    if ((storageTypeInput.geteStorage() == null)
        || (storageTypeInput.getpMax() == null)
        || (storageTypeInput.getActivePowerGradient() == null)
        || (storageTypeInput.getEta() == null)
        || (storageTypeInput.getDod() == null)
        || (storageTypeInput.getLifeTime() == null))
      throw new InvalidEntityException("at least one value of storageTypeInput is null", storageTypeInput);

    //Check if rated power factor is between 0 and 1
    if (storageTypeInput.getCosPhiRated() < 0d || storageTypeInput.getCosPhiRated() > 1d)
      throw new InvalidEntityException("Rated power factor of storageTypeInput must be between zero and one", storageTypeInput);
    //Check if permissible amount of full cycles is positive
    if (storageTypeInput.getLifeCycle() < 0)
      throw new InvalidEntityException("Permissible amount of life cycles of storageTypeInput must be positive", storageTypeInput);
    //Check if efficiency of the electrical converter is between 0% and 100%
    if (storageTypeInput.getEta().getValue().doubleValue() < 0d
        || storageTypeInput.getEta().getValue().doubleValue() > 100d)
      throw new InvalidEntityException("Efficiency of the electrical converter must be between 0% and 100%", storageTypeInput);
    //Check if maximum permissible depth of discharge is between 0% and 100%
    if (storageTypeInput.getDod().getValue().doubleValue() < 0d
            || storageTypeInput.getDod().getValue().doubleValue() > 100d)
      throw new InvalidEntityException("Maximum permissible depth of discharge must be between 0% and 100%", storageTypeInput);
    //Check if eStorage, pMax, activePowerGradient or lifeTime are zero or negative
    detectZeroOrNegativeQuantities(
            new Quantity<?>[] {
                    storageTypeInput.geteStorage(),
                    storageTypeInput.getpMax(),
                    storageTypeInput.getActivePowerGradient(), //TODO NSteffan: can be over 100%, correct?
                    storageTypeInput.getLifeTime()
            },
            storageTypeInput);
    //TODO @NSteffan: Einschränkung < / <= 0 richtig?
  }

  /**
   * Validates a WecInput if: <br>
   * - {@link SystemParticipantValidationUtils#checkWecType(WecTypeInput)} confirms a
   * valid type properties
   *
   * @param wecInput WecInput to validate
   */
  public static void checkWec(WecInput wecInput) {
    //Check WecType
    checkWecType(wecInput.getType());
  }

  /**
   * Validates a WecTypeInput if: <br>
   * - it is not null <br>
   * - common system participants values (capex, opex, sRated) are null or negative <br>
   * - its rated power factor is between 0 and 1 <br>
   * - its cpCharacteristic is not null <br>
   * - its efficiency of the assets converter is not null and between 0% and 100% <br>
   * - its rotor area is not null and positive <br>
   * - its height of the rotor hub is not null and positive
   *
   * @param wecTypeInput WecTypeInput to validate
   */
  public static void checkWecType(WecTypeInput wecTypeInput) {
    //Check if null
    checkNonNull(wecTypeInput, "a wecInput type");
    //Check if any common values of system participants are null or negative
    try {
      checkType(wecTypeInput);
    } catch (InvalidEntityException e) {
      throw new InvalidEntityException("At least one value of wecTypeInput is null", wecTypeInput);
    }
    //Check if rated power factor is between 0 and 1
    if (wecTypeInput.getCosPhiRated() < 0d || wecTypeInput.getCosPhiRated() > 1d)
      throw new InvalidEntityException("Rated power factor of wecTypeInput must be between zero and one", wecTypeInput);
    //Check if any values are null
    if ((wecTypeInput.getCpCharacteristic() == null)
            || (wecTypeInput.getEtaConv() == null)
            || (wecTypeInput.getRotorArea() == null)
            || (wecTypeInput.getHubHeight() == null))
      throw new InvalidEntityException("at least one value of wecTypeInput is null", wecTypeInput);
    //TODO NSteffan: Check of CpCharacteristics necessary/how?
    //Check if efficiency of the assets converter is between 0% and 100%
    if (wecTypeInput.getEtaConv().getValue().doubleValue() < 0d
            || wecTypeInput.getEtaConv().getValue().doubleValue() > 100d)
      throw new InvalidEntityException("Efficiency of the assets converter must be between 0% and 100%", wecTypeInput);
    //Check if rotorArea or hubHeight are zero or negative
    detectZeroOrNegativeQuantities(
            new Quantity<?>[] {
                    wecTypeInput.getRotorArea(),
                    wecTypeInput.getHubHeight()
            },
            wecTypeInput);
  }

}
