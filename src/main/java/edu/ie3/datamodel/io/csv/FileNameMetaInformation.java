/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.io.naming.TimeSeriesMetaInformation;
import java.util.Objects;
import java.util.UUID;

/**
 * Meta information, that can be derived from a certain file name
 *
 * @deprecated since 3.0. Use {@link TimeSeriesMetaInformation} instead
 */
@Deprecated(since = "3.0", forRemoval = true)
public abstract class FileNameMetaInformation {
  private final UUID uuid;

  protected FileNameMetaInformation(UUID uuid) {
    this.uuid = uuid;
  }

  public UUID getUuid() {
    return uuid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FileNameMetaInformation that)) return false;
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
