/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import java.util.Objects;
import java.util.UUID;

/** Meta information, that can be derived from a certain file name */
public abstract class FileNameMetaInformation {
  private final UUID uuid;

  public FileNameMetaInformation(UUID uuid) {
    this.uuid = uuid;
  }

  public UUID getUuid() {
    return uuid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FileNameMetaInformation)) return false;
    FileNameMetaInformation that = (FileNameMetaInformation) o;
    return uuid.equals(that.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }

  @Override
  public String toString() {
    return "FileNameMetaInformation{" + "uuid=" + uuid + '}';
  }
}
