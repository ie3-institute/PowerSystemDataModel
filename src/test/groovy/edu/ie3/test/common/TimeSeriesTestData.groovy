/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.datamodel.models.timeseries.IntValue
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.EnergyPriceValue
import tec.uom.se.quantity.Quantities

import java.time.ZoneId
import java.time.ZonedDateTime

import static edu.ie3.util.quantities.PowerSystemUnits.EURO_PER_MEGAWATTHOUR

trait TimeSeriesTestData {
	TimeBasedValue<EnergyPriceValue> timeBasedEntry = new TimeBasedValue<>(
	UUID.fromString("9e4dba1b-f3bb-4e40-bd7e-2de7e81b7704"),
	ZonedDateTime.of(2020, 4, 2, 10, 0, 0, 0, ZoneId.of("UTC")),
	new EnergyPriceValue(Quantities.getQuantity(5d, EURO_PER_MEGAWATTHOUR)))

	IndividualTimeSeries<EnergyPriceValue> individualEnergyPriceTimeSeries =  new IndividualTimeSeries<>(
	UUID.fromString("a4bbcb77-b9d0-4b88-92be-b9a14a3e332b"),
	[
		timeBasedEntry,
		new TimeBasedValue<>(
		UUID.fromString("520d8e37-b842-40fd-86fb-32007e88493e"),
		ZonedDateTime.of(2020, 4, 2, 10, 15, 0, 0, ZoneId.of("UTC")),
		new EnergyPriceValue(Quantities.getQuantity(15d, EURO_PER_MEGAWATTHOUR))),
		new TimeBasedValue<>(
		UUID.fromString("593d006c-ef76-46a9-b8db-f8666f69c5db"),
		ZonedDateTime.of(2020, 4, 2, 10, 30, 0, 0, ZoneId.of("UTC")),
		new EnergyPriceValue(Quantities.getQuantity(10d, EURO_PER_MEGAWATTHOUR))),
	] as Set
	)

	Set<LinkedHashMap<String, String>>  individualEnergyPriceTimeSeriesProcessed = [
		[
			"uuid" 	: "9e4dba1b-f3bb-4e40-bd7e-2de7e81b7704",
			"time"	: "2020-04-02 10:00:00",
			"price"	: "5.0"
		] as LinkedHashMap,
		[
			"uuid" 	: "520d8e37-b842-40fd-86fb-32007e88493e",
			"time"	: "2020-04-02 10:15:00",
			"price"	: "15.0"
		] as LinkedHashMap,
		[
			"uuid" 	: "593d006c-ef76-46a9-b8db-f8666f69c5db",
			"time"	: "2020-04-02 10:30:00",
			"price"	: "10.0"
		] as LinkedHashMap
	] as Set

	IndividualTimeSeries<IntValue> individualIntTimeSeries = new IndividualTimeSeries<>(
	UUID.randomUUID(),
	[
		new TimeBasedValue<IntValue>(UUID.fromString("52ccf570-53a5-490e-85d4-7a57082ebdfc"), ZonedDateTime.of(1990, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")), new IntValue(3)),
		new TimeBasedValue<IntValue>(UUID.fromString("23727eb1-e108-4187-99b2-bef959797078"), ZonedDateTime.of(1990, 1, 1, 0, 15, 0, 0, ZoneId.of("UTC")), new IntValue(4)),
		new TimeBasedValue<IntValue>(UUID.fromString("21b1a544-2961-4488-a9f6-0e94a706a68f"), ZonedDateTime.of(1990, 1, 1, 0, 30, 0, 0, ZoneId.of("UTC")), new IntValue(1))
	] as Set
	)
}