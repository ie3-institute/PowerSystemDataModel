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
import javax.measure.quantity.Power;

/**
 * Characteristic denoting a power factor, that is dependent on the current power consumption or
 * infeed
 */
public class CosPhiFixed extends ReactivePowerCharacteristic<Power> {
  public static final Pattern MATCHING_PATTERN = buildMatchingPattern("cosPhiFixed");
  public static final CosPhiFixed CONSTANT_CHARACTERISTIC =
      new CosPhiFixed(
          UUID.fromString("5b0e96f0-9e6c-4958-b171-633f4f02f58e"), "cosPhiFixed:{(0.0,1.0)}");

  public CosPhiFixed(
      UUID uuid,
      SortedSet<CharacteristicCoordinate<Power, Dimensionless>> characteristicCoordinates) {
    super(uuid, characteristicCoordinates, "cosPhiFixed", 2);
  }

  public CosPhiFixed(UUID uuid, String input) {
    super(
        uuid,
        input,
        MATCHING_PATTERN,
        StandardUnits.ACTIVE_POWER_IN,
        StandardUnits.Q_CHARACTERISTIC,
        "cosPhiFixed",
        2);
  }

  @Override
  public String toString() {
    return "cosPhiFixed{" + "uuid=" + uuid + ", coordinates=" + coordinates + '}';
  }
}
