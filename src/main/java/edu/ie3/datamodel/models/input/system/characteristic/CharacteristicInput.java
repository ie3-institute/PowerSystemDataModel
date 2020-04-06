/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import edu.ie3.datamodel.models.input.InputEntity;
import java.util.Objects;
import java.util.SortedSet;
import java.util.UUID;
import javax.measure.Quantity;

/** Describes characteristics of assets */
public abstract class CharacteristicInput<A extends Quantity<A>, O extends Quantity<O>>
    extends InputEntity {
  protected final SortedSet<CharacteristicCoordinate<A, O>> coordinates;

  public CharacteristicInput(UUID uuid, SortedSet<CharacteristicCoordinate<A, O>> coordinates) {
    super(uuid);
    this.coordinates = coordinates;
  }

  public SortedSet<CharacteristicCoordinate<A, O>> getCoordinates() {
    return coordinates;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacteristicInput<?, ?> that = (CharacteristicInput<?, ?>) o;
    return coordinates.equals(that.coordinates);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), coordinates);
  }

  @Override
  public String toString() {
    return "CharacteristicInput{" + "uuid=" + uuid + ", coordinates=" + coordinates + '}';
  }
}
