/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.io.naming.timeseries.ColumnScheme.*;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import java.util.EnumSet;

public class TimeSeriesConstants {
  private TimeSeriesConstants() {
    // do not instantiate
  }

  public static final EnumSet<ColumnScheme> ACCEPTED_COLUMN_SCHEMES =
      EnumSet.of(
          ACTIVE_POWER,
          APPARENT_POWER,
          ENERGY_PRICE,
          APPARENT_POWER_AND_HEAT_DEMAND,
          ACTIVE_POWER_AND_HEAT_DEMAND,
          HEAT_DEMAND);
}
