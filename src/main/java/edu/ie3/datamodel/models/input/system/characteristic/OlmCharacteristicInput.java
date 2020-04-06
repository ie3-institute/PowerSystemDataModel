/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import edu.ie3.datamodel.models.StandardUnits;
import java.util.SortedSet;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Speed;

/** Characteristic for overhead line monitoring */
public class OlmCharacteristicInput extends CharacteristicInput<Speed, Dimensionless> {
  private static final Pattern MATCHING_PATTERN = buildMatchingPattern("olm");
  public static final OlmCharacteristicInput CONSTANT_CHARACTERISTIC =
      new OlmCharacteristicInput(
          UUID.fromString("9723ddea-f713-4552-8355-71d9fd831127"), "olm:{(0.0,1.0)}");

  public OlmCharacteristicInput(
      UUID uuid,
      SortedSet<CharacteristicCoordinate<Speed, Dimensionless>> characteristicCoordinates) {
    super(uuid, characteristicCoordinates, "olm", 2);
  }

  public OlmCharacteristicInput(UUID uuid, String input) {
    super(
        uuid,
        input,
        MATCHING_PATTERN,
        StandardUnits.WIND_VELOCITY,
        StandardUnits.OLM_CHARACTERISTIC,
        "olm",
        2);
  }

  @Override
  public String toString() {
    return "OlmCharacteristicInput{" + "uuid=" + uuid + ", coordinates=" + coordinates + '}';
  }
}
