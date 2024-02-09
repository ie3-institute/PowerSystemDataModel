/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import java.util.Objects;
import java.util.UUID;

/** Describes the type of an {@link edu.ie3.datamodel.models.input.AssetInput} */
public abstract class AssetTypeInput extends UniqueInputEntity {
  /** Name or ID of the asset */
  private final String id;

  /**
   * @param uuid of the input entity
   * @param id of the asset
   */
  protected AssetTypeInput(UUID uuid, String id) {
    super(uuid);
    this.id = id;
  }

  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AssetTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id);
  }

  @Override
  public String toString() {
    return "AssetTypeInput{" + "uuid=" + getUuid() + ", id=" + id + "}";
  }
}
