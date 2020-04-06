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

/**
 * Characteristic giving the reactive power behaviour based on the current voltage magnitude at the
 * connecting node
 */
public class QV extends ReactivePowerCharacteristic<Dimensionless> {
  public static final Pattern MATCHING_PATTERN = buildMatchingPattern("qV");
  public static final QV CONSTANT_CHARACTERISTIC =
      new QV(UUID.fromString("d129c2d0-7fc8-42c0-9359-3883ebb09682"), "qV:{(0.0,0.0)}");

  public QV(
      UUID uuid,
      SortedSet<CharacteristicCoordinate<Dimensionless, Dimensionless>> characteristicCoordinates) {
    super(uuid, characteristicCoordinates, "qV", 2);
  }

  public QV(UUID uuid, String input) {
    super(
        uuid,
        input,
        MATCHING_PATTERN,
        StandardUnits.VOLTAGE_MAGNITUDE,
        StandardUnits.Q_CHARACTERISTIC,
        "qV",
        2);
  }

  @Override
  public String toString() {
    return "QV{" + "uuid=" + uuid + ", coordinates=" + coordinates + '}';
  }
}
