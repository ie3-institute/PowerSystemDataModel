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
public class CosPhiP extends ReactivePowerCharacteristic<Power> {
  public static final Pattern MATCHING_PATTERN = buildMatchingPattern("cosPhiP");
  public static final CosPhiP CONSTANT_CHARACTERISTIC =
      new CosPhiP(UUID.fromString("de42d205-b970-4ae3-9d3e-8cdeb6fcf372"), "cosPhiP:{(0.0,1.0)}");

  public CosPhiP(
      UUID uuid,
      SortedSet<CharacteristicCoordinate<Power, Dimensionless>> characteristicCoordinates) {
    super(uuid, characteristicCoordinates, "cosPhiP", 2);
  }

  public CosPhiP(UUID uuid, String input) {
    super(
        uuid,
        input,
        MATCHING_PATTERN,
        StandardUnits.ACTIVE_POWER_IN,
        StandardUnits.Q_CHARACTERISTIC,
        "cosPhiP",
        2);
  }

  @Override
  public String toString() {
    return "CosPhiP{" + "uuid=" + uuid + ", coordinates=" + coordinates + '}';
  }
}
