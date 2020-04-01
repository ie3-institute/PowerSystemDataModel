/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.deserialize

import static edu.ie3.util.quantities.PowerSystemUnits.EURO_PER_MEGAWATTHOUR

import edu.ie3.datamodel.io.CsvFileDefinition
import edu.ie3.datamodel.models.timeseries.IndividualTimeSeries
import edu.ie3.datamodel.models.value.EnergyPriceValue
import edu.ie3.datamodel.models.value.TimeBasedValue
import edu.ie3.util.TimeTools
import edu.ie3.util.io.FileIOUtils
import spock.lang.Shared
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import java.time.ZoneId

class TimeSeriesDeserializerTest extends Specification {
	static {
		TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
	}

	@Shared
	IndividualTimeSeries<EnergyPriceValue> individualTimeSeries

	@Shared
	String[] headLineElements

	@Shared
	String testBaseFolderPath

	def setupSpec() {
		individualTimeSeries = new IndividualTimeSeries<>(
				UUID.fromString("178892cf-500f-4e62-9d1f-ff9e3a92215e"),
				[
					new TimeBasedValue<>(TimeTools.toZonedDateTime("2020-03-31 19:00:00"), new EnergyPriceValue(Quantities.getQuantity(1d, EURO_PER_MEGAWATTHOUR))),
					new TimeBasedValue<>(TimeTools.toZonedDateTime("2020-03-31 19:15:00"), new EnergyPriceValue(Quantities.getQuantity(2d, EURO_PER_MEGAWATTHOUR))),
					new TimeBasedValue<>(TimeTools.toZonedDateTime("2020-03-31 19:30:00"), new EnergyPriceValue(Quantities.getQuantity(3d, EURO_PER_MEGAWATTHOUR))),
					new TimeBasedValue<>(TimeTools.toZonedDateTime("2020-03-31 19:45:00"), new EnergyPriceValue(Quantities.getQuantity(4d, EURO_PER_MEGAWATTHOUR)))
				]
				)

		headLineElements = ["uuid", "time", "price"]
		testBaseFolderPath = "test"
	}

	def cleanup() {
		// delete files after each test if they exist
		if (new File(testBaseFolderPath).exists()) {
			FileIOUtils.deleteRecursively(testBaseFolderPath)
		}
	}

	def "The IndividualTimeSeriesDeserializer determines the headline elements correctly"() {
		given:
		IndividualTimeSeriesDeserializer<EnergyPriceValue> timeSeriesDeserializer = new IndividualTimeSeriesDeserializer<>(EnergyPriceValue, testBaseFolderPath)
		String[] expected = headLineElements

		when:
		String[] actual = timeSeriesDeserializer.determineHeadLineElements()

		then:
		actual == expected
	}

	def "The IndividualTimeSeriesDeserializer determines the correct CsvFileDefinition"() {
		given:
		IndividualTimeSeriesDeserializer<EnergyPriceValue> timeSeriesDeserializer = new IndividualTimeSeriesDeserializer<>(EnergyPriceValue, testBaseFolderPath)
		CsvFileDefinition expected = new CsvFileDefinition("individual_timeseries_178892cf-500f-4e62-9d1f-ff9e3a92215e", headLineElements)

		when:
		CsvFileDefinition actual = timeSeriesDeserializer.determineFileDefinition(UUID.fromString("178892cf-500f-4e62-9d1f-ff9e3a92215e"))

		then:
		actual == expected
	}

	def "The IndividualTimeSeriesDeserializer handles a single time based value correctly"() {
		given:
		IndividualTimeSeriesDeserializer<EnergyPriceValue> timeSeriesDeserializer = new IndividualTimeSeriesDeserializer<>(EnergyPriceValue, testBaseFolderPath)
		TimeBasedValue<EnergyPriceValue> dut = new TimeBasedValue<>(TimeTools.toZonedDateTime("2020-03-31 19:00:00"), new EnergyPriceValue(Quantities.getQuantity(1d, EURO_PER_MEGAWATTHOUR)))
		Map expected = [
			"uuid": "Egal - Michael Wendler",
			"time": "2020-03-31 19:00:00",
			"price": "1.0"
		]

		when:
		LinkedHashMap<String, String> actual = timeSeriesDeserializer.handleTimeBasedValue(dut)

		then:
		/* The uuid is randomly generated here and therefore not checked */
		actual.size() == expected.size()
		expected.forEach { k, v ->
			if (k == "uuid") {
				assert actual.containsKey(k)
			} else {
				assert (v == actual.get(k))
			}
		}
	}

	def "The IndividualTimeSeriesDeserializer creates the correct file on deserialization"() {
		given:
		IndividualTimeSeriesDeserializer<EnergyPriceValue> timeSeriesDeserializer = new IndividualTimeSeriesDeserializer<>(EnergyPriceValue, testBaseFolderPath)

		when:
		timeSeriesDeserializer.deserialize(individualTimeSeries)
		timeSeriesDeserializer.csvFileSink.dataConnector.shutdown()

		then:
		new File(testBaseFolderPath).exists()
		new File(testBaseFolderPath + File.separator + "individual_timeseries_178892cf-500f-4e62-9d1f-ff9e3a92215e.csv").exists()
	}
}
