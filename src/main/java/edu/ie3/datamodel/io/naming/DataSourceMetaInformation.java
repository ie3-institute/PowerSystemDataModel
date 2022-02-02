/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import java.util.Objects;
import java.util.UUID;

/** Meta information, that describe a certain data source */
public abstract class DataSourceMetaInformation {
  private final UUID uuid;

  protected DataSourceMetaInformation(UUID uuid) {
    this.uuid = uuid;
  }

  public UUID getUuid() {
    return uuid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DataSourceMetaInformation that)) return false;
    return uuid.equals(that.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }

  @Override
  public String toString() {
    return "DataSourceMetaInformation{" + "uuid=" + uuid + '}';
  }
}
