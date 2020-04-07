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

/**
 * Characteristic denoting a power factor, that is dependent on the current power consumption or
 * infeed
 */
public class CosPhiP extends ReactivePowerCharacteristic {
  public static final Pattern MATCHING_PATTERN = buildMatchingPattern("cosPhiP");

  public CosPhiP(
      SortedSet<CharacteristicCoordinate<Dimensionless, Dimensionless>> characteristicCoordinates) {
    super(characteristicCoordinates, "cosPhiP", 2);
  }

  public CosPhiP(String input) {
    super(
        input,
        MATCHING_PATTERN,
        StandardUnits.Q_CHARACTERISTIC,
        StandardUnits.Q_CHARACTERISTIC,
        "cosPhiP",
        2);
  }

  @Override
  public String toString() {
    return "CosPhiP{" + "coordinates=" + coordinates + '}';
  }
}
