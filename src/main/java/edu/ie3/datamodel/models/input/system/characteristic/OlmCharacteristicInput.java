/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import static edu.ie3.util.quantities.PowerSystemUnits.PU;
import static java.util.Collections.unmodifiableSortedSet;
import static tec.uom.se.unit.Units.METRE_PER_SECOND;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.StandardUnits;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Speed;
import tec.uom.se.quantity.Quantities;

/** Characteristic for overhead line monitoring */
public class OlmCharacteristicInput extends CharacteristicInput<Speed, Dimensionless> {
  public static final OlmCharacteristicInput CONSTANT_CHARACTERISTIC =
      buildConstantCharacteristic();

  public OlmCharacteristicInput(
      SortedSet<CharacteristicPoint<Speed, Dimensionless>> characteristicPoints) {
    super(characteristicPoints, "olm", 2);
  }

  public OlmCharacteristicInput(String input) throws ParsingException {
    super(input, StandardUnits.WIND_VELOCITY, StandardUnits.OLM_CHARACTERISTIC, "olm", 2);
  }

  private static OlmCharacteristicInput buildConstantCharacteristic() {
    TreeSet<CharacteristicPoint<Speed, Dimensionless>> points = new TreeSet<>();
    points.add(
        new CharacteristicPoint<>(
            Quantities.getQuantity(0d, METRE_PER_SECOND), Quantities.getQuantity(1d, PU)));
    return new OlmCharacteristicInput(unmodifiableSortedSet(points));
  }

  @Override
  public String toString() {
    return "OlmCharacteristicInput{" + "points=" + points + '}';
  }
}
