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
import javax.measure.quantity.Power;

/**
 * Represents the charging characteristic in dependency of the current state of charge as a
 * dimensionless multiplier to the rated active power
 *
 * @deprecated This model is currently not well worked out. Wait for a more elaborate model.
 */
@Deprecated
public class EvCharacteristicInput extends CharacteristicInput<Power, Dimensionless> {

  public EvCharacteristicInput(
      SortedSet<CharacteristicPoint<Power, Dimensionless>> characteristicPoints) {
    super(characteristicPoints, "ev", 2);
  }

  public EvCharacteristicInput(String input) throws ParsingException {
    super(input, StandardUnits.ACTIVE_POWER_IN, StandardUnits.EV_CHARACTERISTIC, "ev", 2);
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
    return "EvCharacteristicInput{" + "points=" + getPoints() + '}';
  }
}
