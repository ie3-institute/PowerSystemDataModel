/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import edu.ie3.datamodel.models.StandardUnits;
import java.util.SortedSet;
import java.util.regex.Pattern;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;

/**
 * Represents the charging characteristic in dependency of the current state of charge as a
 * dimensionless multiplier to the rated active power
 */
public class EvCharacteristicInput extends CharacteristicInput<Power, Dimensionless> {
  private static final Pattern MATCHING_PATTERN = CharacteristicInput.buildMatchingPattern("ev");

  @Deprecated
  public EvCharacteristicInput(
      SortedSet<CharacteristicCoordinate<Power, Dimensionless>> characteristicCoordinates) {
    super(characteristicCoordinates, "ev", 2);
  }

  @Deprecated
  public EvCharacteristicInput(String input) {
    super(
        input,
        MATCHING_PATTERN,
        StandardUnits.ACTIVE_POWER_IN,
        StandardUnits.EV_CHARACTERISTIC,
        "ev",
        2);
  }

  @Override
  public String toString() {
    return "EvCharacteristicInput{" + "coordinates=" + coordinates + '}';
  }
}
