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
   * Instantiates a new Asset type input.
   *
   * @param uuid of the input entity
   * @param id of the asset
   */
  protected AssetTypeInput(UUID uuid, String id) {
    super(uuid);
    this.id = id;
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Copy asset type input copy builder.
   *
   * @return the asset type input copy builder
   */
  public abstract AssetTypeInputCopyBuilder<?> copy();

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

  /**
   * Abstract class for all builder that build child entities of abstract class {@link
   * AssetTypeInput}*
   *
   * @param <B> the type parameter
   */
  public abstract static class AssetTypeInputCopyBuilder<
          B extends AssetTypeInput.AssetTypeInputCopyBuilder<B>>
      extends UniqueEntityCopyBuilder<B> {

    private String id;

    /**
     * Instantiates a new Asset type input copy builder.
     *
     * @param entity the entity
     */
    protected AssetTypeInputCopyBuilder(AssetTypeInput entity) {
      super(entity);
      this.id = entity.getId();
    }

    /**
     * Id b.
     *
     * @param id the id
     * @return the b
     */
    public B id(String id) {
      this.id = id;
      return thisInstance();
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    protected String getId() {
      return id;
    }

    @Override
    public abstract AssetTypeInput build();

    @Override
    protected abstract B thisInstance();
  }
}
