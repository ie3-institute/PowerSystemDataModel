/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming.timeseries;

import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.Uniqueness;
import java.util.Objects;
import java.util.UUID;

/** Meta information, that describe a certain data source */
public abstract class TimeSeriesMetaInformation implements Entity, Uniqueness {
  private final UUID uuid;

  protected TimeSeriesMetaInformation(UUID uuid) {
    this.uuid = uuid;
  }

  public UUID getUuid() {
    return uuid;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    TimeSeriesMetaInformation that = (TimeSeriesMetaInformation) o;
    return Objects.equals(uuid, that.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(uuid);
  }

  @Override
  public String toString() {
    return "TimeSeriesMetaInformation{" + "uuid=" + uuid + '}';
  }
}
