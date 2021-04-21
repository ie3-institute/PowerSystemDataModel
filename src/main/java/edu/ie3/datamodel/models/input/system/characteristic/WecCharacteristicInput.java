/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.StandardUnits;
import java.util.SortedSet;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Speed;

/** Characteristic mapping the wind velocity to its corresponding Betz coefficient */
public class WecCharacteristicInput extends CharacteristicInput<Speed, Dimensionless> {
  public WecCharacteristicInput(
      SortedSet<CharacteristicPoint<Speed, Dimensionless>> characteristicPoints) {
    super(characteristicPoints, "cP", 2);
  }

  public WecCharacteristicInput(String input) throws ParsingException {
    super(input, StandardUnits.WIND_VELOCITY, StandardUnits.CP_CHARACTERISTIC, "cP", 2);
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    return "WecCharacteristicInput{" + "points=" + getPoints() + '}';
  }
}
