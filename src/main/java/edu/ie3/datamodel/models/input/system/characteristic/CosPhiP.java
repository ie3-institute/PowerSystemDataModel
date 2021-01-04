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
 * Characteristic denoting a power factor, that is dependent on the current power consumption or
 * infeed
 */
public class CosPhiP extends ReactivePowerCharacteristic {
  public static final String PREFIX = "cosPhiP";
  public static final String STARTING_REGEX = buildStartingRegex(PREFIX);

  public CosPhiP(
      SortedSet<CharacteristicPoint<Dimensionless, Dimensionless>> characteristicPoints) {
    super(characteristicPoints, PREFIX, 2);
  }

  public CosPhiP(String input) throws ParsingException {
    super(input, StandardUnits.Q_CHARACTERISTIC, StandardUnits.Q_CHARACTERISTIC, PREFIX, 2);
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
    return "CosPhiP{" + "points=" + points + '}';
  }
}
