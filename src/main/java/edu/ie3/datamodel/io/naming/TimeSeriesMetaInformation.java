/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.models.input.UniqueInputEntity;
import java.util.UUID;

/** Meta information, that describe a certain data source */
public abstract class TimeSeriesMetaInformation extends UniqueInputEntity {

  protected TimeSeriesMetaInformation(UUID uuid) {
    super(uuid);
  }
}
