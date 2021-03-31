/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.value.HeatAndPValue
import edu.ie3.util.TimeUtil
import edu.ie3.util.interval.ClosedInterval
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class CsvTimeSeriesSourceIT extends Specification implements CsvTestDataMeta {

	@Shared
	CsvTimeSeriesSource source

	@Shared
	TimeBasedSimpleValueFactory<HeatAndPValue> factory

	def setup() {
		factory = new TimeBasedSimpleValueFactory<>(HeatAndPValue)
		source = new CsvTimeSeriesSource(";", timeSeriesFolderPath, new EntityPersistenceNamingStrategy(), UUID.fromString("76c9d846-797c-4f07-b7ec-2245f679f5c7"), "its_ph_76c9d846-797c-4f07-b7ec-2245f679f5c7", HeatAndPValue, factory)
	}

	def "A csv time series source throw an Exception, if the file cannot be found"() {
		given:
		def filePath = "file/not/found.csv"

		when:
		source.buildIndividualTimeSeries(UUID.fromString("fbc59b5b-9307-4fb4-a406-c1f08f26fee5"), filePath, { null })

		then:
		def ex = thrown(SourceException)
		ex.message == "Unable to find a file with path '" + filePath + "'."
		ex.cause.class == FileNotFoundException
	}

	def "A csv time series source is able to read in a proper file correctly"() {
		given:
		def filePath = "its_ph_76c9d846-797c-4f07-b7ec-2245f679f5c7"
		def tsUuid = UUID.fromString("76c9d846-797c-4f07-b7ec-2245f679f5c7")

		when:
		def actual = source.buildIndividualTimeSeries(tsUuid, filePath, { source.buildTimeBasedValue(it, HeatAndPValue, factory) })

		then:
		noExceptionThrown()
		actual.entries.size() == 2
	}

	def "Construction a csv time series source with malicious parameters, leads to IllegalArgumentException"() {
		when:
		new CsvTimeSeriesSource(";", timeSeriesFolderPath, new EntityPersistenceNamingStrategy(), UUID.fromString("fbc59b5b-9307-4fb4-a406-c1f08f26fee5"), "file/not/found", HeatAndPValue, factory)

		then:
		def e = thrown(IllegalArgumentException)
		e.message == "Unable to obtain time series with UUID 'fbc59b5b-9307-4fb4-a406-c1f08f26fee5'. Please check arguments!"
		e.cause.class == SourceException
	}

	def "A csv time series source is able to return a time series for a period of interest"() {
		given:
		def interval = new ClosedInterval(TimeUtil.withDefaults.toZonedDateTime("2020-01-01 00:15:00"), TimeUtil.withDefaults.toZonedDateTime("2020-01-01 00:15:00"))

		when:
		def actual = source.getTimeSeries(interval)

		then:
		actual.entries.size() == 1
	}

	def "A csv time series source is able to return a single value, if it is covered"() {
		given:
		def time = TimeUtil.withDefaults.toZonedDateTime("2020-01-01 00:15:00")

		when:
		def actual = source.getValue(time)

		then:
		actual.present
		actual.get() == new HeatAndPValue(Quantities.getQuantity(1250.0, StandardUnits.ACTIVE_POWER_IN), Quantities.getQuantity(12.0, StandardUnits.HEAT_DEMAND))
	}
}