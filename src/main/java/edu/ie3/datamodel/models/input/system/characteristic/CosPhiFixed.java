/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import static edu.ie3.util.quantities.PowerSystemUnits.PU;
import static java.util.Collections.unmodifiableSortedSet;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.StandardUnits;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.measure.quantity.Dimensionless;
import tec.uom.se.quantity.Quantities;

/**
 * Characteristic denoting a power factor, that is dependent on the current power consumption or
 * infeed
 */
public class CosPhiFixed extends ReactivePowerCharacteristic {
  public static final String PREFIX = "cosPhiFixed";
  public static final CosPhiFixed CONSTANT_CHARACTERISTIC = buildConstantCharacteristic();
  public static final String STARTING_REGEX = buildStartingRegex(PREFIX);

  public CosPhiFixed(
      SortedSet<CharacteristicCoordinate<Dimensionless, Dimensionless>> characteristicCoordinates) {
    super(characteristicCoordinates, PREFIX, 2);
  }

  public CosPhiFixed(String input) throws ParsingException {
    super(input, StandardUnits.Q_CHARACTERISTIC, StandardUnits.Q_CHARACTERISTIC, PREFIX, 2);
  }

  private static CosPhiFixed buildConstantCharacteristic() {
    TreeSet<CharacteristicCoordinate<Dimensionless, Dimensionless>> coordinates = new TreeSet<>();
    coordinates.add(
        new CharacteristicCoordinate<>(
            Quantities.getQuantity(0d, PU), Quantities.getQuantity(1d, PU)));
    return new CosPhiFixed(unmodifiableSortedSet(coordinates));
  }

  @Override
  public String toString() {
    return "cosPhiFixed{" + "coordinates=" + coordinates + '}';
  }
}
