
/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.influxdb;

import edu.ie3.models.UniqueEntity;
import edu.ie3.models.result.connector.LineResult;

public class InfluxDbMapper {

  public static InfluxDbEntity transformToInfluxDbEntity(UniqueEntity entity) {
    if (entity instanceof LineResult) return new InfluxDbLineResult((LineResult) entity);
    throw new IllegalArgumentException(
        "Unknown entity class: " + entity.getClass().getSimpleName());
  }
}
