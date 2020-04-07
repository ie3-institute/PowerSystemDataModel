/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import java.util.SortedSet;
import java.util.regex.Pattern;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;

/** Abstract class (only for grouping all reactive power characteristics together */
public abstract class ReactivePowerCharacteristic
    extends CharacteristicInput<Dimensionless, Dimensionless> {
  public ReactivePowerCharacteristic(
      SortedSet<CharacteristicCoordinate<Dimensionless, Dimensionless>> characteristicCoordinates,
      String prefix,
      int decimalPlaces) {
    super(characteristicCoordinates, prefix, decimalPlaces);
  }

  public ReactivePowerCharacteristic(
      String input,
      Pattern matchingPattern,
      Unit<Dimensionless> abscissaUnit,
      Unit<Dimensionless> ordinateUnit,
      String prefix,
      int decimalPlaces) {
    super(input, matchingPattern, abscissaUnit, ordinateUnit, prefix, decimalPlaces);
  }

  /**
   * Parses a given input to a valid reactive power characteristic, if it is recognized correctly.
   * Otherwise, an IllegalArgumentException is thrown.
   *
   * @param input String to parse
   * @return Matching reactive power characteristic
   */
  public static ReactivePowerCharacteristic parse(String input) {
    if (CosPhiFixed.MATCHING_PATTERN.matcher(input).matches()) return new CosPhiFixed(input);
    else if (CosPhiP.MATCHING_PATTERN.matcher(input).matches()) return new CosPhiP(input);
    else if (QV.MATCHING_PATTERN.matcher(input).matches()) return new QV(input);
    else
      throw new IllegalArgumentException(
          "Cannot parse '"
              + input
              + "' to a reactive power characteristic, as it does not meet the specifications of any of the available classes.");
  }
}
