/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import edu.ie3.datamodel.io.processor.Processable;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.UUID;

/** Supplies every subclass with a generated UUID, making it unique. */
public abstract class UniqueEntity implements Entity, Uniqueness, Serializable, Processable {
  /** Unique identifier for an entity. */
  private final UUID uuid;

  /**
   * @param uuid Unique identifier for an entity.
   */
  protected UniqueEntity(UUID uuid) {
    this.uuid = uuid;
  }

  public UUID getUuid() {
    return uuid;
  }

  @Override
  public SequencedMap<String, String> toMap() {
    SequencedMap<String, String> map = new LinkedHashMap<>();
    map.put("uuid", uuid.toString());
    return map;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UniqueEntity that)) return false;
    return Objects.equals(uuid, that.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }

  @Override
  public String toString() {
    return "UniqueEntity{" + "uuid=" + uuid + "}";
  }

  public abstract UniqueEntityCopyBuilder<?> copy();

  public abstract static class UniqueEntityCopyBuilder<B extends UniqueEntityCopyBuilder<B>> {
    private UUID uuid;

    protected UniqueEntityCopyBuilder(UniqueEntity entity) {
      this.uuid = entity.uuid;
    }

    public B uuid(UUID uuid) {
      this.uuid = uuid;
      return thisInstance();
    }

    protected UUID getUuid() {
      return uuid;
    }

    protected abstract B thisInstance();

    public abstract UniqueEntity build();
  }
}
