/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.StandardUnits;
import java.util.SortedSet;
import javax.measure.quantity.Dimensionless;

/**
 * Characteristic giving the reactive power behaviour based on the current voltage magnitude at the
 * connecting node
 */
public class QV extends ReactivePowerCharacteristic {
  public static final String PREFIX = "qV";
  public static final String STARTING_REGEX = buildStartingRegex(PREFIX);

  public QV(SortedSet<CharacteristicPoint<Dimensionless, Dimensionless>> characteristicPoints) {
    super(characteristicPoints, PREFIX, 2);
  }

  public QV(String input) throws ParsingException {
    super(input, StandardUnits.VOLTAGE_MAGNITUDE, StandardUnits.Q_CHARACTERISTIC, PREFIX, 2);
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    return "QV{" + "points=" + getPoints() + '}';
  }
}
