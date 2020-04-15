/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import edu.ie3.datamodel.exceptions.ParsingException;
import java.util.SortedSet;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;

/** Abstract class (only for grouping all reactive power characteristics together */
public abstract class ReactivePowerCharacteristic
    extends CharacteristicInput<Dimensionless, Dimensionless> {
  public ReactivePowerCharacteristic(
      SortedSet<CharacteristicPoint<Dimensionless, Dimensionless>> characteristicPoints,
      String prefix,
      int decimalPlaces) {
    super(characteristicPoints, prefix, decimalPlaces);
  }

  public ReactivePowerCharacteristic(
      String input,
      Unit<Dimensionless> abscissaUnit,
      Unit<Dimensionless> ordinateUnit,
      String prefix,
      int decimalPlaces)
      throws ParsingException {
    super(input, abscissaUnit, ordinateUnit, prefix, decimalPlaces);
  }

  /**
   * Parses a given input to a valid reactive power characteristic, if it is recognized correctly.
   * Otherwise, an IllegalArgumentException is thrown.
   *
   * @param input String to parse
   * @return Matching reactive power characteristic
   */
  public static ReactivePowerCharacteristic parse(String input) throws ParsingException {
    if (input.startsWith(CosPhiFixed.PREFIX + ":{")) return new CosPhiFixed(input);
    else if (input.startsWith(CosPhiP.PREFIX + ":{")) return new CosPhiP(input);
    else if (input.startsWith(QV.PREFIX + ":{")) return new QV(input);
    else
      throw new ParsingException(
          "Cannot parse '"
              + input
              + "' to a reactive power characteristic, as it does not meet the specifications of any of the available classes.");
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
