/*
 * © 2020. TU Dortmund University,
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
      SortedSet<CharacteristicCoordinate<Speed, Dimensionless>> characteristicCoordinates) {
    super(characteristicCoordinates, "cP", 2);
  }

  public WecCharacteristicInput(String input) throws ParsingException {
    super(input, StandardUnits.WIND_VELOCITY, StandardUnits.CP_CHARACTERISTIC, "cP", 2);
  }

  @Override
  public String toString() {
    return "WecCharacteristicInput{" + "coordinates=" + coordinates + '}';
  }
}
