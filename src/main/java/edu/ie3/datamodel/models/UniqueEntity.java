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
public abstract class UniqueEntity implements Serializable {
  /** Field name of {@link UniqueEntity} uuid */
  public static final String UUID_FIELD_NAME = "uuid";

  private final UUID uuid;

  protected UniqueEntity() {
    uuid = UUID.randomUUID();
  }

  protected UniqueEntity(UUID uuid) {
    this.uuid = uuid == null ? UUID.randomUUID() : uuid;
  }

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
   * @version 0.1
   * @since 05.06.20
   */
  protected abstract static class UniqueEntityCopyBuilder<T extends UniqueEntityBuilder>
      implements UniqueEntityBuilder {

    private UUID uuid;

    protected UniqueEntityCopyBuilder(UniqueEntity entity) {
      this.uuid = entity.getUuid();
    }

    public T uuid(UUID uuid) {
      this.uuid = uuid;
      return childInstance();
    }

    protected UUID getUuid() {
      return uuid;
    }

    protected abstract T childInstance();
  }

  protected interface UniqueEntityBuilder {

    UniqueEntity build();
  }
}
