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
public class CosPhiFixed extends ReactivePowerCharacteristic {
  public static final Pattern MATCHING_PATTERN = buildMatchingPattern("cosPhiFixed");
  public static final CosPhiFixed CONSTANT_CHARACTERISTIC =
      new CosPhiFixed("cosPhiFixed:{(0.0,1.0)}");

  public CosPhiFixed(
      SortedSet<CharacteristicCoordinate<Dimensionless, Dimensionless>> characteristicCoordinates) {
    super(characteristicCoordinates, "cosPhiFixed", 2);
  }

  public CosPhiFixed(String input) {
    super(
        input,
        MATCHING_PATTERN,
        StandardUnits.Q_CHARACTERISTIC,
        StandardUnits.Q_CHARACTERISTIC,
        "cosPhiFixed",
        2);
  }

  @Override
  public String toString() {
    return "cosPhiFixed{" + "coordinates=" + coordinates + '}';
  }
}
