/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.util.TimeUtil
import edu.ie3.util.quantities.PowerSystemUnits
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime
import javax.measure.quantity.Power

class ResultEntityTestData {

  public static final int WEC_RESULT_SIZE = 1000
  public static final int PV_RESULT_SIZE = 1000
  public static final int BM_RESULT_SIZE = 1
  public static final int FIXED_FEED_IN_RESULT_SIZE = 1
  public static final int EM_RESULT_SIZE = 1

  public static final UUID BM_UUID = UUID.fromString("44b9be7a-af97-4c2a-bb84-9d21abba442f")
  public static final UUID BM_INPUT_MODEL = UUID.fromString("66df67d0-c789-4393-b0a5-897a3bc821a2")
  public static final ComparableQuantity<Power> BM_ACTIVE_POWER = Quantities.getQuantity(-1, PowerSystemUnits.MEGAWATT)
  public static final ComparableQuantity<Power> BM_REACTIVE_POWER = Quantities.getQuantity(-5, PowerSystemUnits.MEGAVAR)
  public static final ZonedDateTime BM_TIME = TimeUtil.withDefaults.toZonedDateTime("2011-01-01T00:00:00Z")
}
