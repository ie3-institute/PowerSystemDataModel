/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system.characteristic;

import edu.ie3.models.input.InputEntity;
import java.util.Objects;
import java.util.UUID;

/** Describes characteristics of assets */
public abstract class AssetCharacteristicInput extends InputEntity {

  /** Type name of this characteristic */
  private final String type;

  /**
   * @param uuid of the input entity
   * @param type The type name of this characteristic
   */
  public AssetCharacteristicInput(UUID uuid, String type) {
    super(uuid);
    this.type = type;
  }

  public String getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    AssetCharacteristicInput that = (AssetCharacteristicInput) o;
    return Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type);
  }
}
