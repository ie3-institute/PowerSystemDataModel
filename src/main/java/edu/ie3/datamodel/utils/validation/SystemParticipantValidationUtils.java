/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.models.input.system.BmInput;
import edu.ie3.datamodel.models.input.system.ChpInput;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.models.input.system.type.BmTypeInput;
import edu.ie3.datamodel.models.input.system.type.ChpTypeInput;
import edu.ie3.datamodel.models.input.system.type.SystemParticipantTypeInput;

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
   * Validates a bmInput type if: <br>
   * - it is not null <br>
   * - common system participants values (capex, opex, sRated) are null or negative <br>
   * - its active power gradient is null or negative <br>
   * - its efficiency of assets inverter is null or negative
   *
   * @param bmInputType BmInput Type to validate
   */
  public static void checkBmType(BmTypeInput bmInputType) {
    //Check if null
    checkNonNull(bmInputType, "a bmInput type");
    //Check if any common values of system participants are null or negative
    try {
      checkType(bmInputType);
    } catch (InvalidEntityException e) {
      throw new InvalidEntityException("At least one value of bmInput type is null", bmInputType);
    }
    //Check if active power gradient is null
    if (bmInputType.getActivePowerGradient() == null)
      throw new InvalidEntityException("Active power gradient of bmInput type is null", bmInputType);
    //Check if efficiency of assets inverter is null
    if (bmInputType.getEtaConv() == null)
      throw new InvalidEntityException("Efficiency of assets inverter of bmInput Type is null", bmInputType);
    //Check if any values are negative
    detectNegativeQuantities(
            new Quantity<?>[] {
                    bmInputType.getActivePowerGradient(),
                    bmInputType.getEtaConv(),
            },
            bmInputType);
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
   * Validates a chpInput type if: <br>
   * - it is not null <br>
   * - common system participants values (capex, opex, sRated) are null or negative <br>
   * - its efficiency of the electrical inverter is null or negative <br>
   * - its thermal efficiency of the system is null or negative <br>
   * - its rated thermal power is null or negative <br>
   * - its needed self-consumption is null or negative
   *
   * @param chpInputType ChpInput Type to validate
   */
  public static void checkChpType(ChpTypeInput chpInputType) {
    //Check if null
    checkNonNull(chpInputType, "a chpInput type");
    //Check if any common values of system participants are null or negative
    try {
      checkType(chpInputType);
    } catch (InvalidEntityException e) {
      throw new InvalidEntityException("At least one value of chpInput type is null", chpInputType);
    }
    //Check if efficiency of the electrical inverter is null
    if (chpInputType.getEtaEl() == null)
      throw new InvalidEntityException("Efficiency of the electrical inverter of chpInput type is null", chpInputType);
    //Check if thermal efficiency of the system is null
    if (chpInputType.getEtaThermal() == null)
      throw new InvalidEntityException("Thermal efficiency of the system of chpInput Type is null", chpInputType);
    //Check if rated thermal power is null
    if (chpInputType.getpThermal() == null)
      throw new InvalidEntityException("Rated thermal power of chpInput Type is null", chpInputType);
    //Check if needed self-consumption is null
    if (chpInputType.getpOwn() == null)
      throw new InvalidEntityException("Needed self-consumption of chpInput Type is null", chpInputType);
    //Check if any values are negative
    detectNegativeQuantities(
            new Quantity<?>[] {
                    chpInputType.getEtaEl(),
                    chpInputType.getEtaThermal(),
                    chpInputType.getpThermal(),
                    chpInputType.getpOwn()
            },
            chpInputType);
    //TODO @NSteffan: Einschränkung < / <= 0 richtig?
  }



}
