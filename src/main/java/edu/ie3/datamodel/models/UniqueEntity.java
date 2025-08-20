/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/** Supplies every subclass with a generated UUID, making it unique */
public abstract class UniqueEntity implements Entity, Serializable {
  /** Field name of {@link UniqueEntity} uuid */
  public static final String UUID_FIELD_NAME = "uuid";

  /** Unique identifier for this entity. */
  private final UUID uuid;

  /** Default constructor that generates a new random UUID. */
  protected UniqueEntity() {
    uuid = UUID.randomUUID();
  }

  /**
   * Constructor that allows setting a specific UUID.
   *
   * @param uuid the UUID to set; if null, a new random UUID will be generated
   */
  protected UniqueEntity(UUID uuid) {
    this.uuid = uuid == null ? UUID.randomUUID() : uuid;
  }

  /**
   * Gets the unique identifier (UUID) for this entity.
   *
   * @return the unique identifier of this entity
   */
  public UUID getUuid() {
    return uuid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UniqueEntity that = (UniqueEntity) o;
    return uuid.equals(that.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }

  @Override
  public String toString() {
    return "UniqueEntity{" + "uuid=" + uuid + '}';
  }

  /**
   * Abstract class for all builder that build child entities of abstract class {@link UniqueEntity}
   *
   * @param <B> The builder type extending from {@link UniqueEntityBuilder}
   */
  public abstract static class UniqueEntityCopyBuilder<B extends UniqueEntityBuilder>
      implements UniqueEntityBuilder {

    private UUID uuid;

    /**
     * Constructor that initializes the builder with an existing entity's UUID.
     *
     * @param entity the existing entity to copy the UUID from
     */
    protected UniqueEntityCopyBuilder(UniqueEntity entity) {
      this.uuid = entity.getUuid();
    }

    /**
     * Sets the UUID for this builder.
     *
     * @param uuid the new UUID to set
     * @return this concrete builder instance
     */
    public B uuid(UUID uuid) {
      this.uuid = uuid;
      return thisInstance();
    }

    /**
     * Returns the UUID associated with this builder.
     *
     * @return the unique identifier (UUID) for this builder
     */
    protected UUID getUuid() {
      return uuid;
    }

    /**
     * Returns an instance of the concrete builder implementation.
     *
     * @return This concrete builder instance.
     */
    protected abstract B thisInstance();
  }

  /** Builds and returns a unique entity. */
  protected interface UniqueEntityBuilder {

    /**
     * Build method.
     *
     * @return an instance of UniqueEntity
     */
    UniqueEntity build();
  }
}
