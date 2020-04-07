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
import javax.measure.quantity.Speed;

/** Characteristic for overhead line monitoring */
public class OlmCharacteristicInput extends CharacteristicInput<Speed, Dimensionless> {
  private static final Pattern MATCHING_PATTERN = buildMatchingPattern("olm");
  public static final OlmCharacteristicInput CONSTANT_CHARACTERISTIC =
      new OlmCharacteristicInput("olm:{(0.0,1.0)}");

  public OlmCharacteristicInput(
      SortedSet<CharacteristicCoordinate<Speed, Dimensionless>> characteristicCoordinates) {
    super(characteristicCoordinates, "olm", 2);
  }

  public OlmCharacteristicInput(String input) {
    super(
        input,
        MATCHING_PATTERN,
        StandardUnits.WIND_VELOCITY,
        StandardUnits.OLM_CHARACTERISTIC,
        "olm",
        2);
  }

  @Override
  public String toString() {
    return "OlmCharacteristicInput{" + "coordinates=" + coordinates + '}';
  }
}
