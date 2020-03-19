/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import java.util.Objects;
import java.util.UUID;

/** Supplies every subclass with a generated UUID, making it unique */
public abstract class UniqueEntity {
  private final UUID uuid;

  public UniqueEntity() {
    uuid = UUID.randomUUID();
  }

  public UniqueEntity(UUID uuid) {
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
    return Objects.equals(uuid, that.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }

  @Override
  public String toString() {
    return "UniqueEntity{" + "uuid=" + uuid + '}';
  }
}
