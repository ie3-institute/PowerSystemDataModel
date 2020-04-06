/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import java.util.SortedSet;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;

/**
 * Represents the charging characteristic in dependency of the current state of charge as a
 * dimensionless multiplier to the rated active power
 */
public class EvCharacteristicInput extends CharacteristicInput<Power, Dimensionless> {
  @Deprecated
  public EvCharacteristicInput(
      UUID uuid,
      SortedSet<CharacteristicCoordinate<Power, Dimensionless>> characteristicCoordinates) {
    super(uuid, characteristicCoordinates);
  }

  @Override
  public String toString() {
    return "EvCharacteristicInput{" + "uuid=" + uuid + ", coordinates=" + coordinates + '}';
  }
}
