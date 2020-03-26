/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.input.system.type.SystemParticipantTypeInput;
import java.util.Objects;
import java.util.UUID;

/** Describes characteristics of assets */
public abstract class AssetCharacteristicInput<T extends SystemParticipantTypeInput>
    extends InputEntity {

  /** Type name of this characteristic */
  protected final T type;

  /** Actual characteristic */
  protected final String characteristic;

  /**
   * @param uuid of the input entity
   * @param type The type name of this characteristic
   * @param characteristic Actual characteristic
   */
  public AssetCharacteristicInput(UUID uuid, T type, String characteristic) {
    super(uuid);
    this.type = type;
    this.characteristic = characteristic;
  }

  public T getType() {
    return type;
  }

  public String getCharacteristic() {
    return characteristic;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    AssetCharacteristicInput<?> that = (AssetCharacteristicInput<?>) o;
    return type.equals(that.type) && characteristic.equals(that.characteristic);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, characteristic);
  }

  @Override
  public String toString() {
    return "AssetCharacteristicInput{"
        + "type="
        + type
        + ", characteristic='"
        + characteristic
        + '\''
        + '}';
  }
}
