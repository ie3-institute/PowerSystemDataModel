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

import javax.measure.quantity.Power
import java.time.ZonedDateTime

class ResultEntityTestData {

	public static final int wecResultsSize = 1000
	public static final int pvResultsSize = 1000
	public static final int bmResultsSize = 1

	public static final UUID bmResultUuid = UUID.fromString("44b9be7a-af97-4c2a-bb84-9d21abba442f")
	public static final UUID bmInputModelUuid = UUID.fromString("66df67d0-c789-4393-b0a5-897a3bc821a2")
	public static final ComparableQuantity<Power> bmActivePower = Quantities.getQuantity(-1, PowerSystemUnits.MEGAWATT)
	public static final ComparableQuantity<Power> bmReactivePower = Quantities.getQuantity(-5, PowerSystemUnits.MEGAVAR)
	public static final ZonedDateTime bmZonedDateTime = TimeUtil.withDefaults.toZonedDateTime("2011-01-01 00:00:00")
}
