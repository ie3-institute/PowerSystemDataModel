/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import java.util.SortedSet;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Speed;

/** Characteristic mapping the wind velocity to its corresponding Betz coefficient */
public class WecCharacteristicInput extends CharacteristicInput<Speed, Dimensionless> {
  private static final String PREFIX = "cP";
  private static final Pattern MATCHING_PATTERN = CharacteristicInput.buildMatchingPattern(PREFIX);

  public WecCharacteristicInput(
      UUID uuid,
      SortedSet<CharacteristicCoordinate<Speed, Dimensionless>> characteristicCoordinates) {
    super(uuid, characteristicCoordinates, PREFIX, 2);
  }

  @Override
  public String toString() {
    return "WecCharacteristicInput{" + "uuid=" + uuid + ", coordinates=" + coordinates + '}';
  }
}
