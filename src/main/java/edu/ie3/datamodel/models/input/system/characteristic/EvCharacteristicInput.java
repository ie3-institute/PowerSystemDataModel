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
import javax.measure.quantity.Power;

/**
 * Represents the charging characteristic in dependency of the current state of charge as a
 * dimensionless multiplier to the rated active power
 */
public class EvCharacteristicInput extends CharacteristicInput<Power, Dimensionless> {
  private static final String PREFIX = "ev";
  private static final Pattern MATCHING_PATTERN = CharacteristicInput.buildMatchingPattern(PREFIX);

  @Deprecated
  public EvCharacteristicInput(
      UUID uuid,
      SortedSet<CharacteristicCoordinate<Power, Dimensionless>> characteristicCoordinates) {
    super(uuid, characteristicCoordinates, PREFIX, 2);
  }

  @Override
  public String toString() {
    return "EvCharacteristicInput{" + "uuid=" + uuid + ", coordinates=" + coordinates + '}';
  }
}
