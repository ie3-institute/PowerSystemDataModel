/*
 * © 2021. TU Dortmund University,
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
  /**
   * Instantiates a new Reactive power characteristic.
   *
   * @param characteristicPoints the characteristic points
   * @param prefix the prefix
   */
  protected ReactivePowerCharacteristic(
      SortedSet<CharacteristicPoint<Dimensionless, Dimensionless>> characteristicPoints,
      String prefix) {
    super(characteristicPoints, prefix);
  }

  /**
   * Instantiates a new Reactive power characteristic.
   *
   * @param input the input
   * @param abscissaUnit the abscissa unit
   * @param ordinateUnit the ordinate unit
   * @param prefix the prefix
   * @throws ParsingException the parsing exception
   */
  protected ReactivePowerCharacteristic(
      String input,
      Unit<Dimensionless> abscissaUnit,
      Unit<Dimensionless> ordinateUnit,
      String prefix)
      throws ParsingException {
    super(input, abscissaUnit, ordinateUnit, prefix);
  }

  /**
   * Parses a given input to a valid reactive power characteristic, if it is recognized correctly.
   * Otherwise, an IllegalArgumentException is thrown.
   *
   * @param input String to parse
   * @return Matching reactive power characteristic
   * @throws ParsingException If the given input cannot be mapped onto one of the known
   *     characteristics
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
