/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.influxdb;

import edu.ie3.datamodel.models.UniqueEntity;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.TimeColumn;

import java.time.Instant;

public abstract class InfluxDbEntity<E extends UniqueEntity> {

  @TimeColumn
  @Column(name = "time")
  protected Instant time;

  public InfluxDbEntity(Instant time) {
    this.time = time;
  }

  public InfluxDbEntity() {}
}
