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
 * Characteristic giving the reactive power behaviour based on the current voltage magnitude at the
 * connecting node
 */
public class QV extends ReactivePowerCharacteristic {
  public static final Pattern MATCHING_PATTERN = buildMatchingPattern("qV");

  public QV(
      SortedSet<CharacteristicCoordinate<Dimensionless, Dimensionless>> characteristicCoordinates) {
    super(characteristicCoordinates, "qV", 2);
  }

  public QV(String input) {
    super(
        input,
        MATCHING_PATTERN,
        StandardUnits.VOLTAGE_MAGNITUDE,
        StandardUnits.Q_CHARACTERISTIC,
        "qV",
        2);
  }

  @Override
  public String toString() {
    return "QV{" + "coordinates=" + coordinates + '}';
  }
}
