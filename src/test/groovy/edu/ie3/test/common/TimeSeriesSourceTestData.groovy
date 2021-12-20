/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.value.HeatAndPValue
import edu.ie3.datamodel.models.value.PValue
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime

final class  TimeSeriesSourceTestData {

	private TimeSeriesSourceTestData() {
		// restrict instantiation
	}

	public static final ZonedDateTime TIME_00MIN = ZonedDateTime.parse("2020-01-01T00:00:00Z[UTC]")
	public static final ZonedDateTime TIME_15MIN = ZonedDateTime.parse("2020-01-01T00:15:00Z[UTC]")

	public static final PValue P_VALUE_00MIN = new PValue(
	Quantities.getQuantity(1000.0d, StandardUnits.ACTIVE_POWER_IN)
	)
	public static final PValue P_VALUE_15MIN = new PValue(
	Quantities.getQuantity(1250.0d, StandardUnits.ACTIVE_POWER_IN)
	)

	public static final HeatAndPValue PH_VALUE_00MIN = new HeatAndPValue(
	Quantities.getQuantity(1000.0d, StandardUnits.ACTIVE_POWER_IN),
	Quantities.getQuantity(8.0, StandardUnits.HEAT_DEMAND)
	)
	public static final HeatAndPValue PH_VALUE_15MIN = new HeatAndPValue(
	Quantities.getQuantity(1250.0d, StandardUnits.ACTIVE_POWER_IN),
	Quantities.getQuantity(12.0, StandardUnits.HEAT_DEMAND)
	)
}
