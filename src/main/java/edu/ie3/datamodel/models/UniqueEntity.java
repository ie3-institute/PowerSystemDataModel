package edu.ie3.datamodel.models;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public abstract class UniqueEntity implements Entity, Uniqueness, Serializable {
  private final UUID uuid;

  public UniqueEntity() {
    this.uuid = UUID.randomUUID();
  }

  public UniqueEntity(UUID uuid) {
    this.uuid = uuid;
  }

  public UUID getUuid() {
    return uuid;
  }

  public abstract UniqueEntityCopyBuilder<?> copy();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UniqueEntity that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return Objects.equals(uuid, that.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }

  @Override
  public String toString() {
    return "UniqueEntity{"
        + "uuid=" + getUuid()
        + '}';
  }

  public abstract static class UniqueEntityCopyBuilder<B extends UniqueEntityCopyBuilder<B>> {
    private UUID uuid;

    protected UniqueEntityCopyBuilder(UniqueEntity entity) {
      this.uuid = entity.getUuid();
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
